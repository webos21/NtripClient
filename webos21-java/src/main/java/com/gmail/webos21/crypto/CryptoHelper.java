package com.gmail.webos21.crypto;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class CryptoHelper {

    public static int DEF_AES_KEYSIZE = 256;
    public static int DEF_AES_BYTELEN = 32;

    public static String ALG_AES_KEY = "AES";

    // for compatibility of all the devices - Android, iOS, Plain-Java
    public static String ALG_AES_CIPHER = "AES/CBC/PKCS5Padding";

    public static KeyPair genKeyPair(String alg, String hash, int keySize) {
        KeyPair pair = null;

        try {
            int posSlashes = alg.indexOf('/');
            String safeAlg = (posSlashes < 0) ? alg : alg.substring(0, posSlashes);

            KeyPairGenerator keyGen = KeyPairGenerator.getInstance(safeAlg);
            SecureRandom random = SecureRandom.getInstance(hash);
            keyGen.initialize(keySize, random);

            pair = keyGen.generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return pair;
    }

    public static PrivateKey getPrivateKey(KeyPair keyPair) {
        return keyPair.getPrivate();
    }

    public static PrivateKey getPrivateKey(String alg, byte[] privKeyBytes) {
        PrivateKey key = null;
        try {
            int posSlashes = alg.indexOf('/');
            String safeAlg = (posSlashes < 0) ? alg : alg.substring(0, posSlashes);

            KeyFactory kf = KeyFactory.getInstance(safeAlg);
            key = kf.generatePrivate(new PKCS8EncodedKeySpec(privKeyBytes));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }
        return key;
    }

    public static PublicKey getPublicKey(KeyPair keyPair) {
        return keyPair.getPublic();
    }

    public static PublicKey getPublicKey(String alg, byte[] pubKeyBytes) {
        PublicKey key = null;
        try {
            int posSlashes = alg.indexOf('/');
            String safeAlg = (posSlashes < 0) ? alg : alg.substring(0, posSlashes);

            KeyFactory kf = KeyFactory.getInstance(safeAlg);
            key = kf.generatePublic(new X509EncodedKeySpec(pubKeyBytes));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }
        return key;
    }

    public static byte[] encryptWithPrivateKey(String alg, byte[] privKeyBytes, byte[] dataToEnc) {
        PrivateKey key = getPrivateKey(alg, privKeyBytes);
        byte[] result = null;

        try {
            Cipher pkCipher = Cipher.getInstance(alg);
            pkCipher.init(Cipher.ENCRYPT_MODE, key);
            result = pkCipher.doFinal(dataToEnc);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }

        return result;
    }

    public static byte[] encryptWithPrivateKey(String alg, byte[] privKeyBytes, String plainText, String charSet) {
        PrivateKey key = getPrivateKey(alg, privKeyBytes);
        byte[] result = null;

        try {
            Cipher pkCipher = Cipher.getInstance(alg);
            pkCipher.init(Cipher.ENCRYPT_MODE, key);
            result = pkCipher.doFinal(plainText.getBytes(charSet));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return result;
    }

    public static byte[] decryptWithPrivateKey(String alg, byte[] privKeyBytes, byte[] encTextBytes) {
        PrivateKey key = getPrivateKey(alg, privKeyBytes);
        byte[] result = null;

        try {
            Cipher pkCipher = Cipher.getInstance(alg);
            pkCipher.init(Cipher.DECRYPT_MODE, key);
            result = pkCipher.doFinal(encTextBytes);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }

        return result;
    }

    public static byte[] encryptWithPublicKey(String alg, byte[] pubKeyBytes, byte[] dataToEnc) {
        PublicKey key = getPublicKey(alg, pubKeyBytes);
        byte[] result = null;

        try {
            Cipher pkCipher = Cipher.getInstance(alg);
            pkCipher.init(Cipher.ENCRYPT_MODE, key);
            result = pkCipher.doFinal(dataToEnc);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }

        return result;
    }

    public static byte[] encryptWithPublicKey(String alg, byte[] pubKeyBytes, String plainText, String charSet) {
        PublicKey key = getPublicKey(alg, pubKeyBytes);
        byte[] result = null;

        try {
            Cipher pkCipher = Cipher.getInstance(alg);
            pkCipher.init(Cipher.ENCRYPT_MODE, key);
            result = pkCipher.doFinal(plainText.getBytes(charSet));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return result;
    }

    public static byte[] decryptWithPublicKey(String alg, byte[] publicKey, byte[] encTextBytes) {
        PublicKey key = getPublicKey(alg, publicKey);
        byte[] result = null;

        try {
            Cipher pkCipher = Cipher.getInstance(alg);
            pkCipher.init(Cipher.DECRYPT_MODE, key);
            result = pkCipher.doFinal(encTextBytes);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }

        return result;
    }

    public static SecretKey genAESKey() {
        return genAESKey(DEF_AES_KEYSIZE);
    }

    public static SecretKey genAESKey(int keySize) {
        SecretKey key = null;

        try {
            KeyGenerator kgen = KeyGenerator.getInstance(ALG_AES_KEY);
            kgen.init(keySize);
            key = kgen.generateKey();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return key;
    }

    public static Cipher getEncryptAESCipher(byte[] aesKeyBytes, byte[] iv) {
        if (aesKeyBytes.length != DEF_AES_BYTELEN) {
            byte[] realKey = new byte[DEF_AES_BYTELEN];
            System.arraycopy(aesKeyBytes, 0, realKey, 0, Math.min(aesKeyBytes.length, DEF_AES_BYTELEN));
            aesKeyBytes = realKey;
        }

        try {
            SecretKeySpec secretKey = new SecretKeySpec(aesKeyBytes, ALG_AES_KEY);

            Cipher cipher = Cipher.getInstance(ALG_AES_CIPHER);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, new IvParameterSpec(iv));
            return cipher;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static Cipher getDecryptAESCipher(byte[] aesKeyBytes, byte[] iv) {
        if (aesKeyBytes.length != DEF_AES_BYTELEN) {
            byte[] realKey = new byte[DEF_AES_BYTELEN];
            System.arraycopy(aesKeyBytes, 0, realKey, 0, Math.min(aesKeyBytes.length, DEF_AES_BYTELEN));
            aesKeyBytes = realKey;
        }

        try {
            SecretKeySpec secretKey = new SecretKeySpec(aesKeyBytes, ALG_AES_KEY);

            Cipher cipher = Cipher.getInstance(ALG_AES_CIPHER);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec(iv));
            return cipher;
        } catch (NoSuchAlgorithmException e1) {
            e1.printStackTrace();
        } catch (NoSuchPaddingException e1) {
            e1.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static byte[] encryptWithAESKey(byte[] aesKeyBytes, byte[] iv, byte[] dataToEnc) {
        byte[] result = null;

        if (aesKeyBytes.length != DEF_AES_BYTELEN) {
            byte[] realKey = new byte[DEF_AES_BYTELEN];
            System.arraycopy(aesKeyBytes, 0, realKey, 0, Math.min(aesKeyBytes.length, DEF_AES_BYTELEN));
            aesKeyBytes = realKey;
        }

        try {
            SecretKeySpec secretKey = new SecretKeySpec(aesKeyBytes, ALG_AES_KEY);

            Cipher cipher = Cipher.getInstance(ALG_AES_CIPHER);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, new IvParameterSpec(iv));
            result = cipher.doFinal(dataToEnc);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }

        return result;
    }

    public static byte[] encryptWithAESKey(byte[] aesKeyBytes, byte[] iv, String plainText, String charSet) {
        byte[] result = null;

        if (aesKeyBytes.length != DEF_AES_BYTELEN) {
            byte[] realKey = new byte[DEF_AES_BYTELEN];
            System.arraycopy(aesKeyBytes, 0, realKey, 0, Math.min(aesKeyBytes.length, DEF_AES_BYTELEN));
            aesKeyBytes = realKey;
        }

        try {
            SecretKeySpec secretKey = new SecretKeySpec(aesKeyBytes, ALG_AES_KEY);

            Cipher cipher = Cipher.getInstance(ALG_AES_CIPHER);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, new IvParameterSpec(iv));
            result = cipher.doFinal(plainText.getBytes(charSet));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }

        return result;
    }

    public static byte[] decryptWithAESKey(byte[] aesKeyBytes, byte[] iv, byte[] encTextBytes) {
        byte[] result = null;

        if (aesKeyBytes.length != DEF_AES_BYTELEN) {
            byte[] realKey = new byte[DEF_AES_BYTELEN];
            System.arraycopy(aesKeyBytes, 0, realKey, 0, Math.min(aesKeyBytes.length, DEF_AES_BYTELEN));
            aesKeyBytes = realKey;
        }

        try {
            SecretKeySpec secretKey = new SecretKeySpec(aesKeyBytes, ALG_AES_KEY);

            Cipher cipher = Cipher.getInstance(ALG_AES_CIPHER);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec(iv));
            result = cipher.doFinal(encTextBytes);
            return result;
        } catch (NoSuchAlgorithmException e1) {
            e1.printStackTrace();
        } catch (NoSuchPaddingException e1) {
            e1.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }

        return result;
    }

    public static byte[] decryptWithAESKey(byte[] aesKeyBytes, byte[] iv, String encText, String charSet) {
        byte[] result = null;

        if (aesKeyBytes.length != DEF_AES_BYTELEN) {
            byte[] realKey = new byte[DEF_AES_BYTELEN];
            System.arraycopy(aesKeyBytes, 0, realKey, 0, Math.min(aesKeyBytes.length, DEF_AES_BYTELEN));
            aesKeyBytes = realKey;
        }

        try {
            SecretKeySpec secretKey = new SecretKeySpec(aesKeyBytes, ALG_AES_KEY);

            Cipher cipher = Cipher.getInstance(ALG_AES_CIPHER);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec(iv));
            result = cipher.doFinal(encText.getBytes(charSet));
            return result;
        } catch (NoSuchAlgorithmException e1) {
            e1.printStackTrace();
        } catch (NoSuchPaddingException e1) {
            e1.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }

        return result;
    }

    public static byte[] md5hash(String plainText, String charSet) {
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            return md5.digest(plainText.getBytes(charSet));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static byte[] sha1hash(String plainText, String charSet) {
        try {
            MessageDigest sha1 = MessageDigest.getInstance("SHA1");
            return sha1.digest(plainText.getBytes(charSet));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static byte[] sha2hash(String plainText, String charSet) {
        try {
            MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
            return sha256.digest(plainText.getBytes(charSet));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

}
