package com.messi.languagehelper.meinv.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by luli on 09/09/2017.
 */

public class MD5 {

    public MD5() {
    }

    public static String encode(String val) {
        MessageDigest md5 = null;

        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException var3) {
            var3.printStackTrace();
        }

        md5.update(val.getBytes());
        byte[] m = md5.digest();
        return getString(m);
    }

    private static String getString(byte[] bytes) {
        StringBuffer sb = new StringBuffer();

        for(int i = 0; i < bytes.length; ++i) {
            byte b = bytes[i];
            if((b & 255) < 16) {
                sb.append("0");
            }

            sb.append(Integer.toHexString(b & 255));
        }

        return sb.toString();
    }
}
