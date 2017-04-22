
package com.rac021.jax.client.security ;

import java.util.Objects ;
import java.math.BigInteger ;
import java.security.MessageDigest ;
import java.security.NoSuchAlgorithmException ;


/**
 *
 * @author yahiaoui
 */
public class Digestor {
    
    public static String digestMD5(final String message ) throws NoSuchAlgorithmException{
        Objects.requireNonNull(message)                     ;
        MessageDigest md = MessageDigest.getInstance("MD5") ;
        return digest(md, message)                          ;
    }
   
    public static String digestSha1(String message) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-1") ;
        return digest(md, message)                            ;
    }

    public static String digest(MessageDigest messageDigest, String message ) throws NoSuchAlgorithmException {
        byte[] hash = messageDigest.digest(message.getBytes()) ;
        BigInteger bI = new BigInteger(1, hash)                ;
        return bI.toString(16)                                 ;
    }

    public static String generateSignatureSHA1(String login, String password, String timeStamp) throws NoSuchAlgorithmException {
        Objects.requireNonNull(login)     ;
        Objects.requireNonNull(password)  ;
        Objects.requireNonNull(timeStamp) ;
        return digestSha1( login + password + timeStamp ) ;
    }
    
    public static String generateSignatureMD5(String login, String password, String timeStamp) throws NoSuchAlgorithmException {
        Objects.requireNonNull(login)     ;
        Objects.requireNonNull(password)  ;
        Objects.requireNonNull(timeStamp) ;
        return digestMD5(login + password + timeStamp ) ;
    }

    private Digestor() {
    }

}
