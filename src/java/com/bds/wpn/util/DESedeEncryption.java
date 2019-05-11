/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bds.wpn.util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.DESedeKeySpec;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;


/**
 *
 * @author rony.rodriguez
 */
public class DESedeEncryption extends BDSUtil {

    public static final String DESEDE_ENCRYPTION_SCHEMEP = "DESede";
    private KeySpec myKeySpec;
    private SecretKeyFactory mySecretKeyFactory;
    private Cipher cipher;
    byte[] keyAsBytes;
    private String myEncryptionKey;
    private String myEncryptionScheme;
    SecretKey key;
    private SecretKey llave;
    private String semilla;

    /**
     * Contructor del objeto DESedeEncryption
     *
     * @throws Exception
     */
    public DESedeEncryption(){
        try {
            myEncryptionKey = "ThisIsSecretEncryptionKey";
            myEncryptionScheme = DESEDE_ENCRYPTION_SCHEMEP;
            keyAsBytes = myEncryptionKey.getBytes(UNICODE_FORMAT);
            myKeySpec = new DESedeKeySpec(keyAsBytes);
            mySecretKeyFactory = SecretKeyFactory.getInstance(myEncryptionScheme);
            cipher = Cipher.getInstance(myEncryptionScheme);
            key = mySecretKeyFactory.generateSecret(myKeySpec);
        } catch (Exception e) {
        }

    }

    /**
     * Metodo para encriptar un texto
     *
     * @param unencryptedString String
     * @return String
     */
    public String encrypt(String unencryptedString) {
        String encryptedString = null;
        try {
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] plainText = unencryptedString.getBytes(UNICODE_FORMAT);
            byte[] encryptedText = cipher.doFinal(plainText);
            BASE64Encoder base64encoder = new BASE64Encoder();
            encryptedString = base64encoder.encode(encryptedText);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return encryptedString;
    }

    /**
     * Metodo para desencriptar un texto
     *
     * @param encryptedString String
     * @return String
     */
    public String decrypt(String encryptedString) {
        String decryptedText = null;
        try {
            cipher.init(Cipher.DECRYPT_MODE, key);
            BASE64Decoder base64decoder = new BASE64Decoder();
            byte[] encryptedText = base64decoder.decodeBuffer(encryptedString);
            byte[] plainText = cipher.doFinal(encryptedText);
            decryptedText = bytes2String(plainText);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return decryptedText;
    }

    /**
     * Obtener un string de caracteres a partir de un arreglo de bytes
     *
     * @param bytes byte[]
     * @return String
     */
    private static String bytes2String(byte[] bytes) {
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < bytes.length; i++) {
            stringBuffer.append((char) bytes[i]);
        }
        return stringBuffer.toString();
    }

    /**
     * metodo que gener una llave en DES desde una semilla
     *
     */
    public void generarLlave() {
        try {
            DESKeySpec keySpec = new DESKeySpec(semilla.getBytes(UNICODE_FORMAT));
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(DESEDE_ENCRYPTION_SCHEME);
            llave = keyFactory.generateSecret(keySpec);
        } catch (InvalidKeyException | UnsupportedEncodingException | NoSuchAlgorithmException | InvalidKeySpecException e) {

        }

    }

    /**
     * metodo que se encarga de encriptar un texto dada una semilla
     *
     * @param texto
     * @return
     */
    public String encriptar(String texto) {
        String resultado = "";
        try {
            //generarLlave();
            sun.misc.BASE64Encoder base64encoder = new BASE64Encoder();
            // ENCODE plainTextPassword String
            byte[] cleartext = texto.getBytes(UNICODE_FORMAT);
            Cipher cipher = Cipher.getInstance(DESEDE_ENCRYPTION_SCHEME); // cipher is not thread safe
            cipher.init(Cipher.ENCRYPT_MODE, llave);
            resultado = base64encoder.encode(cipher.doFinal(cleartext));
        } catch (UnsupportedEncodingException | NoSuchAlgorithmException | NoSuchPaddingException | BadPaddingException | InvalidKeyException | IllegalBlockSizeException e) {

        }
        return texto;
    }

    /**
     * metodo que se encarga de desencriptar un texto en DES desde una semilla
     *
     * @param texto
     * @return
     */
    public String desencriptar(String texto) {
        String resultado = "";
        try {
           // generarLlave();
            sun.misc.BASE64Decoder base64decoder = new BASE64Decoder();
            // DECODE texto
            byte[] encrypedPwdBytes = base64decoder.decodeBuffer(texto);
            cipher = Cipher.getInstance(DESEDE_ENCRYPTION_SCHEME);// cipher is not thread safe
            cipher.init(Cipher.DECRYPT_MODE, llave);
            byte[] plainTextPwdBytes = (cipher.doFinal(encrypedPwdBytes));
            resultado = new String(plainTextPwdBytes);
        } catch (IOException | NoSuchAlgorithmException | NoSuchPaddingException | BadPaddingException | InvalidKeyException | IllegalBlockSizeException e) {
            e.toString();
        } catch (Exception ex){
            ex.toString();
        }
        return texto;
    }
    
    /**
     * retorna la semilla para encriptar la data sensible
     * @return semilla para encriptar la data sensible
     */
    public String getSemilla() {
        return semilla;
    }

    /**
     * asigna la semilla para encriptar la data sensible
     * @param semilla semilla para encriptar la data sensible
     */
    public void setSemilla(String semilla) {
        this.semilla = semilla;
    }
    
    
}
