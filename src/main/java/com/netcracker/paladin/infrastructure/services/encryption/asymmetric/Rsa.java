package com.netcracker.paladin.infrastructure.services.encryption.asymmetric;

/**
 * Created by ivan on 27.11.16.
 */

import sun.security.rsa.RSAPrivateCrtKeyImpl;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.security.spec.X509EncodedKeySpec;

public class Rsa implements AsymmetricEncryption {

    private final String ALGORITHM = "RSA";
    private final int KEYSIZE = 1024;

    private final KeyFactory keyFactory;
    private final Cipher cipher;
    private final KeyPairGenerator keyPairGenerator;
    private final Signature signature;

    public Rsa() {
        try {
            keyFactory = KeyFactory.getInstance(ALGORITHM);
            cipher = Cipher.getInstance(ALGORITHM);
            keyPairGenerator = KeyPairGenerator.getInstance(ALGORITHM);
            keyPairGenerator.initialize(KEYSIZE);
            signature = Signature.getInstance("MD5WithRSA");
        }catch (NoSuchAlgorithmException | NoSuchPaddingException e){
            throw new IllegalStateException(e);
        }
    }

    @Override
    public byte[] generatePrivateKey() {
        try {
            return keyPairGenerator.generateKeyPair().getPrivate().getEncoded();
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalStateException(e);
        }
    }

    @Override
    public byte[] generatePublicKey(byte[] privateKeyBytes){
        try {
            PrivateKey privateKey = keyFactory.generatePrivate(new PKCS8EncodedKeySpec(privateKeyBytes));
            RSAPrivateCrtKeyImpl rsaPrivateKey = (RSAPrivateCrtKeyImpl) privateKey;
            PublicKey publicKey = keyFactory.generatePublic(new RSAPublicKeySpec(rsaPrivateKey.getModulus(), rsaPrivateKey.getPublicExponent()));
            return publicKey.getEncoded();
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalStateException(e);
        }
    }

    @Override
    public byte[] encrypt(byte[] sequenceToEncrypt, PublicKey publicKey) {
        try {
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            return cipher.doFinal(sequenceToEncrypt);
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalStateException(e);
        }
    }

    @Override
    public byte[] encrypt(byte[] sequenceToEncrypt, byte[] publicKeyBytes) {
        return encrypt(sequenceToEncrypt, getPublicKeyFromBytes(publicKeyBytes));
    }

    @Override
    public byte[] decrypt(byte[] sequenceToDecrypt, PrivateKey privateKey) {
        try {
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            return cipher.doFinal(sequenceToDecrypt);
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalStateException(e);
        }
    }

    @Override
    public byte[] decrypt(byte[] sequenceToDecrypt, byte[] privateKeyBytes) {
        return decrypt(sequenceToDecrypt, getPrivateKeyFromBytes(privateKeyBytes));
    }

    @Override
    public byte[] createSignature(byte[] data, byte[] privateKeyBytes){
        try {
            signature.initSign(getPrivateKeyFromBytes(privateKeyBytes));
            signature.update(data);
            return signature.sign();
        }catch (InvalidKeyException | SignatureException e){
            throw new IllegalStateException(e);
        }
    }

    @Override
    public boolean verifySignature(byte[] data, byte[] signatureBytes, byte[] publicKeyBytes){
        try {
            signature.initVerify(getPublicKeyFromBytes(publicKeyBytes));
            signature.update(data);
            return signature.verify(signatureBytes);
        }catch (InvalidKeyException | SignatureException e){
            throw new IllegalStateException(e);
        }
    }

    private PrivateKey getPrivateKeyFromBytes(byte[] privateKeyBytes){
        try {
            return keyFactory.generatePrivate(new PKCS8EncodedKeySpec(privateKeyBytes));
        }catch (InvalidKeySpecException e){
            throw new IllegalStateException(e);
        }
    }

    private PublicKey getPublicKeyFromBytes(byte[] publicKeyBytes){
        try {
            return keyFactory.generatePublic(new X509EncodedKeySpec(publicKeyBytes));
        }catch (InvalidKeySpecException e){
            throw new IllegalStateException(e);
        }
    }
}
