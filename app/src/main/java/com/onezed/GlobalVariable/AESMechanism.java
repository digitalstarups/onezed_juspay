package com.onezed.GlobalVariable;

import android.util.Base64;

import java.security.MessageDigest;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class AESMechanism {
    String AES="AES";

    public String decrypt(String outputString,String password) throws Exception {
        SecretKeySpec key=generateKey(password);
        Cipher decryptionCipher = Cipher.getInstance(AES);
        decryptionCipher.init(Cipher.DECRYPT_MODE, key);
        byte[] decodeValue = Base64.decode(outputString,Base64.DEFAULT);
        byte[] decValue= decryptionCipher.doFinal(decodeValue);
        String decryptedValue=new String(decValue);
        return decryptedValue;
    }

    public String encrypt(String Data, String password ) throws  Exception{
        SecretKeySpec key=generateKey(password);
        Cipher c = Cipher.getInstance(AES);
        c.init(Cipher.ENCRYPT_MODE,key);
        byte[] encVal=c.doFinal(Data.getBytes());
        String encryptedValue= Base64.encodeToString(encVal,Base64.DEFAULT);
        return encryptedValue;
    }
    public SecretKeySpec generateKey(String password) throws Exception{
        final MessageDigest digest=MessageDigest.getInstance("SHA-256");
        byte[] bytes=password.getBytes("UTF-8");
        digest.update(bytes,0,bytes.length);
        byte[] key=digest.digest();
        SecretKeySpec secretKeySpec=new SecretKeySpec(key,"AES");
        return secretKeySpec;
    }
}