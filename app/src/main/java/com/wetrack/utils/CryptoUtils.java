package com.wetrack.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public abstract class CryptoUtils {

    public static String md5Digest(String original) {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("MD5");
        }  catch (NoSuchAlgorithmException ex) {}
        md.update(original.getBytes());
        byte[] digest = md.digest();
        StringBuilder builder = new StringBuilder();
        for (byte b : digest) {
            builder.append(String.format("%02x", b & 0xff));
        }
        return builder.toString();
    }

}
