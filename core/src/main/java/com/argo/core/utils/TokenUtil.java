package com.argo.core.utils;

import com.argo.core.configuration.SiteConfig;
import com.argo.core.exception.CookieInvalidException;
import com.google.common.base.Charsets;
import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import com.google.common.io.BaseEncoding;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.Random;

/**
 * 描述 ：
 *
 * @author yaming_deng
 * @date 2012-5-23
 */
public class TokenUtil {

    public static final Charset UTF_8 = Charset.forName("UTF-8");
    private static Logger logger = LoggerFactory.getLogger(TokenUtil.class);

	private static final String HMAC_SHA1 = "HmacSHA256";
	private static Random RAND = new Random();

    /**
     * HMAC 256 with UTF-8 encoding
     * @param data
     * @param secret
     * @return
     */
	public static String generate(String data, String secret) {
        byte[] byteHMAC = null;
        try {
            Mac mac = Mac.getInstance(HMAC_SHA1);
            SecretKeySpec spec;
            String oauthSignature = encode(secret) + "&";
            spec = new SecretKeySpec(oauthSignature.getBytes(UTF_8), HMAC_SHA1);
            mac.init(spec);
            byteHMAC = mac.doFinal(data.getBytes(UTF_8));
            return BaseEncoding.base64Url().encode(byteHMAC);
        } catch (InvalidKeyException e) {
            e.printStackTrace();
            return null;
        } catch (NoSuchAlgorithmException ignore) {
            // should never happen
            return null;
        }

    }

    /**
     * HMAC 256 with UTF-8 encoding
     * @param data
     * @param secret
     * @return
     */
    public static String generateHex(String data, byte[] secret) {
        byte[] byteHMAC = null;
        try {
            Mac mac = Mac.getInstance(HMAC_SHA1);
            SecretKeySpec spec;
            spec = new SecretKeySpec(secret, "RAW");
            mac.init(spec);
            byteHMAC = mac.doFinal(data.getBytes(UTF_8));
            return byte2HexStr(byteHMAC);
        } catch (InvalidKeyException e) {
            e.printStackTrace();
            return null;
        } catch (NoSuchAlgorithmException ignore) {
            // should never happen
            return null;
        }
    }

    public static String generateHex(byte[] data, byte[] secret) {
        byte[] byteHMAC = null;
        try {
            Mac mac = Mac.getInstance(HMAC_SHA1);
            SecretKeySpec spec;
            spec = new SecretKeySpec(secret, "RAW");
            mac.init(spec);
            byteHMAC = mac.doFinal(data);
            return byte2HexStr(byteHMAC);
        } catch (InvalidKeyException e) {
            e.printStackTrace();
            return null;
        } catch (NoSuchAlgorithmException ignore) {
            // should never happen
            return null;
        }
    }

    public static byte[] hexToBytes(String hexString){
        try {
            return Hex.decodeHex(hexString.toCharArray());
        } catch (DecoderException e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }

    public static String byte2HexStr(byte[] b){
        return Hex.encodeHexString(b);
    }

	public static String random(String data){
		long timestamp = System.currentTimeMillis() / 1000;
        long nonce = timestamp + RAND.nextInt();
        return generate(data, nonce+"$v1.0");
	}

    public static void verifyRequestSign(String cookieId, String cookieSecret, String url, String userId, String hexKey, String sign0){

        byte[] keypart = hexToBytes(hexKey);

        StringBuilder plain = new StringBuilder();
        try {
            plain.append(new String(keypart, "US-ASCII"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        plain.append("|").append(userId).append("|").append(url).append("|").append(cookieSecret).append("|").append(cookieId);

        logger.debug("plain: {}", plain.toString());

        byte[] data = plain.toString().getBytes(Charsets.US_ASCII);

        HashFunction hf = Hashing.sha256();
        HashCode hc = hf.newHasher().putBytes(data).hash();
        String sign1 = hc.toString();

        if (!sign0.equals(sign1)){
            logger.error("Error Sign. cookieId={}, cookieSecret={}, url={}, key={}, userId={}, server sign={}, client sign={}", cookieId, cookieSecret, url, hexKey, userId, sign1, sign0);
        }

    }
	
	/**
     * @param value string to be encoded
     * @return encoded string
     * @see <a href="http://wiki.oauth.net/TestCases">OAuth / TestCases</a>
     * @see <a href="http://groups.google.com/group/oauth/browse_thread/thread/a8398d0521f4ae3d/9d79b698ab217df2?hl=en&lnk=gst&q=space+encoding#9d79b698ab217df2">Space encoding - OAuth | Google Groups</a>
     * @see <a href="http://tools.ietf.org/html/rfc3986#section-2.1">RFC 3986 - Uniform Resource Identifier (URI): Generic Syntax - 2.1. Percent-Encoding</a>
     */
	public static String encode(String value) {
        String encoded = null;
        try {
            encoded = URLEncoder.encode(value, "UTF-8");
        } catch (UnsupportedEncodingException ignore) {
        }
        StringBuilder buf = new StringBuilder(encoded.length());
        char focus;
        for (int i = 0; i < encoded.length(); i++) {
            focus = encoded.charAt(i);
            if (focus == '*') {
                buf.append("%2A");
            } else if (focus == '+') {
                buf.append("%20");
            } else if (focus == '%' && (i + 1) < encoded.length()
                    && encoded.charAt(i + 1) == '7' && encoded.charAt(i + 2) == 'E') {
                buf.append('~');
                i += 2;
            } else {
                buf.append(focus);
            }
        }
        return buf.toString();
    }
	
	/**
	 * 加密Cookie值.
	 * @param name
	 * @param value
	 * @return
	 */
	public static String createSignedValue(String name, String value) throws Exception {
		String secret = getCookieSecretSalt();
		long timestamp = new Date().getTime() / 1000;
		value = BaseEncoding.base64Url().encode(value.getBytes("UTF-8"));
		String signature = createSignatureValue(timestamp+"", secret, name+"|"+value);
		return value+"|"+timestamp+"|"+signature;
	}

    public static String md5(String value){
        HashFunction hf = Hashing.md5();
        HashCode hc = hf.newHasher().putString(value, UTF_8).hash();
        return hc.toString();
    }

	public static String getCookieSecretSalt() {
        String val = (String)SiteConfig.instance.getCookie().get("secret");
        return md5(val);
	}
	
	private static String createSignatureValue(String timestamp, String secret, String data){
        HashFunction hf = Hashing.sha256();
        HashCode hc = hf.newHasher().putString(timestamp + "|" + secret + "|" + data, UTF_8).hash();
        return hc.toString();
	}
	
	/**
	 * 解密Cookie值.
	 * @param name
	 * @param value
	 * @return
	 */
	public static String decodeSignedValue(String name, String value) throws CookieInvalidException {
//        if (logger.isDebugEnabled()){
//            logger.debug("{}:{}", name, value);
//        }
		Object days = SiteConfig.instance.getCookie().get("age");
        if (days==null){
            days = 30;
        }
		return decodeSignedValue(name, value, (Integer)days);
	}

	public static String decodeSignedValue(String name, String value,
			Integer days) throws CookieInvalidException {
		String secret = getCookieSecretSalt();
		String[] parts = value.split("\\|"); // email+timestamp+signature
		if(parts.length!=3){
            throw new CookieInvalidException(value);
		}
		String signature = createSignatureValue(parts[1], secret, name+"|"+parts[0]);
		if(!parts[2].equals(signature)){
			logger.error("Invalid Cookie signature: " + value);
            throw new CookieInvalidException(value);
		}
		long timestamp = Long.parseLong(parts[1]) * 1000L;
		long now = new Date().getTime();
		if(timestamp < (now - days * 24 * 3600 * 1000L)){
            logger.error("Expired Cookie: " + value);
			return null;
		}
		if(timestamp > (now + 31L * 24 * 3600 * 1000)){
            logger.error("Cookie timestamp in future; possible tampering " + value);
			return null;
		}
		value = new String(BaseEncoding.base64().decode(parts[0]));
		return value;
	}
	
	public static void main(String[] args) throws IOException{

    }
}
