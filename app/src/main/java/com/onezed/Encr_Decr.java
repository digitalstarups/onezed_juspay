package com.onezed;

import android.util.Base64;

import com.onezed.GlobalVariable.GlobalStrings;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;


public class Encr_Decr {



    public String encrypt(String value, String key)
            throws UnsupportedEncodingException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException
    {
        byte[] value_bytes = value.getBytes("UTF-8");
        byte[] key_bytes = getKeyBytes(key);
        return Base64.encodeToString(encrypt(value_bytes, key_bytes, key_bytes), 0);
    }

    public byte[] encrypt(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, byte[] paramArrayOfByte3)
            throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException
    {
        // setup AES cipher in CBC mode with PKCS #5 padding
        Cipher localCipher = Cipher.getInstance(GlobalStrings.StrEncryptMedhod);

        // encrypt
        localCipher.init(1, new SecretKeySpec(paramArrayOfByte2, GlobalStrings.StrEncryptMedhod.substring(0,3)), new IvParameterSpec(paramArrayOfByte3));
        return localCipher.doFinal(paramArrayOfByte1);
    }



    public String decrypt(String value, String key)
            throws GeneralSecurityException, IOException
    {
        byte[] value_bytes = Base64.decode(value, 0);
        byte[] key_bytes = getKeyBytes(key);
       return new String(decrypt(value_bytes, key_bytes, key_bytes), "UTF-8");



    }

    public byte[] decrypt(byte[] ArrayOfByte1, byte[] ArrayOfByte2, byte[] ArrayOfByte3)
            throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException
    {

        Cipher localCipher = Cipher.getInstance(GlobalStrings.StrEncryptMedhod);


        localCipher.init(2, new SecretKeySpec(ArrayOfByte2, GlobalStrings.StrEncryptMedhod.substring(0,3)), new IvParameterSpec(ArrayOfByte3));
        return localCipher.doFinal(ArrayOfByte1);
    }



    private byte[] getKeyBytes(String paramString)
            throws UnsupportedEncodingException
    {
        byte[] arrayOfByte1 = new byte[16];
        byte[] arrayOfByte2 = paramString.getBytes("UTF-8");
        System.arraycopy(arrayOfByte2, 0, arrayOfByte1, 0, Math.min(arrayOfByte2.length, arrayOfByte1.length));
        return arrayOfByte1;
    }





}
