package com.argo.core.utils;

import com.argo.core.configuration.SiteConfig;
import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import com.google.common.io.BaseEncoding;
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

    private static Logger logger = LoggerFactory.getLogger(TokenUtil.class);

	private static final String HMAC_SHA1 = "HmacSHA256";
	private static Random RAND = new Random();
	
	public static String generate(String data, String secret) {
        byte[] byteHMAC = null;
        try {
            Mac mac = Mac.getInstance(HMAC_SHA1);
            SecretKeySpec spec;
            String oauthSignature = encode(secret) + "&";
            spec = new SecretKeySpec(oauthSignature.getBytes(), HMAC_SHA1);
            mac.init(spec);
            byteHMAC = mac.doFinal(data.getBytes());
            return BaseEncoding.base64Url().encode(byteHMAC);
        } catch (InvalidKeyException e) {
            e.printStackTrace();
            return null;
        } catch (NoSuchAlgorithmException ignore) {
            // should never happen
            return null;
        }

    }
	
	public static String random(String data){
		long timestamp = System.currentTimeMillis() / 1000;
        long nonce = timestamp + RAND.nextInt();
        return generate(data, nonce+"$v1.0");
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
        StringBuffer buf = new StringBuffer(encoded.length());
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
	public static String createSignedValue(String name, String value){
		String secret = getCookieSecretSalt();
		long timestamp = new Date().getTime();
		value = BaseEncoding.base64Url().encode(value.getBytes());
		String signature = createSignatureValue(timestamp+"", secret, name+"|"+value);
		return value+"|"+timestamp+"|"+signature;
	}

    public static String md5(String value){
        HashFunction hf = Hashing.md5();
        HashCode hc = hf.newHasher().putString(value, Charset.forName("UTF-8")).hash();
        return hc.toString();
    }

	private static String getCookieSecretSalt() {
        String val = (String)SiteConfig.instance.getCookie().get("secret");
        return md5(val);
	}
	
	private static String createSignatureValue(String timestamp, String secret, String data){
        HashFunction hf = Hashing.sha256();
        HashCode hc = hf.newHasher().putString(timestamp + "|" + secret + "|" + data, Charset.forName("UTF-8")).hash();
        return hc.toString();
	}
	
	/**
	 * 解密Cookie值.
	 * @param name
	 * @param value
	 * @return
	 */
	public static String decodeSignedValue(String name, String value){
		Object days = SiteConfig.instance.getCookie().get("age");
        if (days==null){
            days = 30;
        }
		return decodeSignedValue(name, value, (Integer)days);
	}

	public static String decodeSignedValue(String name, String value,
			Integer days) {
		String secret = getCookieSecretSalt();
		String[] parts = value.split("\\|"); // email+timestamp+signature
		if(parts.length!=3){
			return null;
		}
		String signature = createSignatureValue(parts[1], secret, name+"|"+parts[0]);
		if(!parts[2].equals(signature)){
			logger.warn("Invalid Cookie signature: " + value);
			return null;
		}
		long timestamp = Long.parseLong(parts[1]);
		long now = new Date().getTime();
		if(timestamp < (now - days * 86400 * 1000L)){
            logger.warn("Expired Cookie: " + value);
			return null;
		}
		if(timestamp > (now + 31L * 86400 * 1000)){
            logger.warn("Cookie timestamp in future; possible tampering " + value);
			return null;
		}
		value = new String(BaseEncoding.base64().decode(parts[0]));
		return value;
	}
	
	public static void main(String[] args) throws IOException{
		System.out.println(random("yaming_deng"));
		System.out.println(random("192.168.1.105"));
	}
}
