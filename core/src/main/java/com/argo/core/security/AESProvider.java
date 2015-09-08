package com.argo.core.security;

import com.google.common.base.Charsets;
import org.apache.commons.codec.binary.Hex;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by yamingd on 9/8/15.
 */
public class AESProvider {

    private String SaltSuffix = "&vKUnrpaCvL9gH&!";
    private String IvSuffix = "&i973144334676&!";

    public AESProvider(String saltSuffix, String ivSuffix) {
        SaltSuffix = saltSuffix;
        IvSuffix = ivSuffix;
    }

    private byte[] fullZore(String data,int blockSize){
        byte[] dataBytes = data.getBytes(Charsets.UTF_8);
        int plaintextLength = dataBytes.length;
        if (plaintextLength % blockSize != 0) {
            plaintextLength = plaintextLength + (blockSize - (plaintextLength % blockSize));
        }
        byte[] plaintext = new byte[plaintextLength];
        System.arraycopy(dataBytes, 0, plaintext, 0, dataBytes.length);
        return plaintext;
    }

    /**
     * 加密手机号
     *
     * @param content 需要加密的内容
     * @return
     */
    public String encrypt(String content) {

        try {

            Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
            int blockSize = cipher.getBlockSize();
            //System.out.println(blockSize);
            SecretKeySpec keyspec = new SecretKeySpec(fullZore(SaltSuffix, blockSize), "AES");
            IvParameterSpec ivspec = new IvParameterSpec(fullZore(IvSuffix, blockSize));
            cipher.init(Cipher.ENCRYPT_MODE, keyspec, ivspec);
            byte[] encrypted = cipher.doFinal(fullZore(content, blockSize));
            return Hex.encodeHexString(encrypted).trim();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    /**解密
     * @param content  待解密内容
     * @return
     */
    public String decrypt(String content) {

        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
            int blockSize = cipher.getBlockSize();
            SecretKeySpec keyspec = new SecretKeySpec(fullZore(SaltSuffix, blockSize), "AES");
            IvParameterSpec ivspec = new IvParameterSpec(fullZore(IvSuffix, blockSize));
            cipher.init(Cipher.DECRYPT_MODE, keyspec, ivspec);

            byte[] data = Hex.decodeHex(content.toCharArray());
            byte[] decrypted = cipher.doFinal(data);

            return new String(decrypted, "UTF-8").trim();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }


    }

}
