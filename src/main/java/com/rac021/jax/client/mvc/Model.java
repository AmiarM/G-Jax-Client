
package com.rac021.jax.client.mvc ;

import java.util.List ;
import javax.json.Json ;
import java.util.ArrayList ;
import javax.json.JsonReader ;
import javax.json.JsonString ;
import javax.ws.rs.client.Client ;
import org.apache.http.HttpResponse ;
import javax.ws.rs.client.Invocation ;
import javax.ws.rs.client.ClientBuilder ;
import org.apache.http.client.HttpClient ;
import org.apache.http.client.methods.HttpPost ;
import com.rac021.jax.client.security.Encryptor ;
import org.apache.http.message.BasicNameValuePair ;
import org.apache.http.impl.client.DefaultHttpClient ;
import org.apache.http.client.entity.UrlEncodedFormEntity ;

/**
 *
 * @author ryahiaoui
 */

public class Model {

    public Model() {
    }
    
    
    public static String getToken(  String url           , 
                                    String username      ,
                                    String password      , 
                                    String client_id     , 
                                    String client_secret ) throws Exception {
 
     HttpClient client = new DefaultHttpClient()                               ;
     HttpPost post = new HttpPost( url )                                       ;
     post.setHeader("Content-Type", "application/x-www-form-urlencoded")       ;
     List urlParameters = new ArrayList<>()                                    ;
     urlParameters.add(new BasicNameValuePair("username", username))           ;
     urlParameters.add(new BasicNameValuePair("password", password))           ;
     urlParameters.add(new BasicNameValuePair("client_id", client_id))         ;
     urlParameters.add(new BasicNameValuePair("grant_type", "password"))       ;
     urlParameters.add(new BasicNameValuePair("client_secret", client_secret)) ;
     post.setEntity(new UrlEncodedFormEntity(urlParameters))                   ;

     HttpResponse response = client.execute(post)                                 ;
     JsonReader   reader   = Json.createReader(response.getEntity().getContent()) ;
     
     JsonString jsonString = reader.readObject().getJsonString("access_token")    ;
     
     if( jsonString != null ) {
         String accessToken = jsonString.getString()      ;
         if(accessToken == null ) {
             System.out.println(" BAD AUTHENTICATION ! ") ;
             return null ;
         }
         return accessToken ;
     }
     return null ;

    }    
         
    public static String invokeService_Using_SSO ( String url    ,
                                                   String token  ,
                                                   String accept , 
                                                   Class  clazz  ,
                                                   String sort   ) throws Exception {
 
        Client clientB = ClientBuilder.newClient()  ;
         
        Invocation.Builder client = clientB.target( url )
                                           .request()
                                           .header("accept", accept)
                                           .header("Authorization", " Bearer " + token ) ;
        
        if( sort != null && ! sort.isEmpty() ) {
            client.header("sort", sort ) ;
        }
        
        return client.get(String.class)  ;

    }
    
    public static String invokeService_Using_Custom ( String url   ,
                                                     String accept , 
                                                     String token  ,
                                                     Class  clazz  ,
                                                     String sort   ) throws Exception {
 
        Client clientB = ClientBuilder.newClient()  ;
         
        Invocation.Builder client = clientB.target( url )
                                           .request()
                                           .header("accept", accept)
                                           .header("API-key-Token", token.trim() ) ;
        
        if( sort != null && ! sort.isEmpty() ) {
             client.header("sort", sort ) ;
        }
        
        return client.get(String.class) ;
    }
    
    private static String getBlanc( int nbr ) {
        String blanc = " " ;
        for(int i = 0 ; i < nbr ; i++ ) {
            blanc += " " ;
        }
        return blanc     ;
    }
    
    public static String generateScriptSSO( String keyCLoakUrl , 
                                            String userName    , 
                                            String password    , 
                                            String client_id   , 
                                            String secret_id   , 
                                            String sort        , 
                                            String url         , 
                                            String params      , 
                                            String accept )    {
        
        String KEYCLOAK_RESPONSE = " KEYCLOAK_RESPONSE=`curl -s -X POST " + keyCLoakUrl  + " \\\n " 
                                   + getBlanc(50) + " -H \"Content-Type: application/x-www-form-urlencoded\" \\\n " 
                                   + getBlanc(50) + " -d 'username=" + userName + "' \\\n "
                                   + getBlanc(50) + " -d 'password=" + password + "' \\\n "
                                   + getBlanc(50) + " -d 'grant_type=password' \\\n "
                                   + getBlanc(50) + " -d 'client_id=" + client_id + "' \\\n "
                                   + getBlanc(50) + " -d 'client_secret=" + secret_id + "' ` \n " ;
                 
        String _token = " ACCESS_TOKEN=`echo $KEYCLOAK_RESPONSE | sed 's/.*access_token\":\"//g' | sed 's/\".*//g'` " ;
               
        String invokeService =   " curl -H \"accept: " + accept + "\"  "
                                + " -H \"Authorization: Bearer $ACCESS_TOKEN\" " ;
               
        if( sort != null && ! sort.isEmpty() ) {
             invokeService += " -H \"sort: " + sort + " \" " ;
        }

        invokeService += "\"" + url + "\" " ;
               
        return  "# !/bin/bash"  + "\n\n "               + 
                "# Script generated by G-JAX-CLIENT \n" +
                "# Author : Rac021 \n\n\n "             +  
                " # INVOKE KEYCLOAD ENDPOINT \n "       + 
                KEYCLOAK_RESPONSE + "\n\n "             + 
                " # PARSE TOKEN FROM RESPONSE \n "      + 
                _token + " \n\n "                       + 
                "# INVOKE THE WEB SERVICE \n "          + 
                invokeService                           ;
    }
    
    public static String decrypt(String algo, String pass ,String text) throws Exception {

        if(algo.equalsIgnoreCase("AES"))                 {
           return Encryptor.aes128CBC7Decrypt(pass, text ) ;
        }
        throw new IllegalStateException(" Algo [ " + algo + " ] Not Implemented Yet !! " ) ;
    }
    
    public static String generateScriptCUSTOM( String url           , 
                                               String login         , 
                                               String password      , 
                                               String params        , 
                                               String sort          ,
                                               String accept        ,
                                               String hashLogin     , 
                                               String hashPassword  , 
                                               String hashTimeStamp ,
                                               String algoSign      ) {
            
        String _url = url ;
        if( params != null && ! params.isEmpty() )
           _url += "?" + params ;
        
        String invokeService = " curl -H \"accept: " + accept + "\"  " ;
               
        if( sort != null && ! sort.isEmpty() ) {
          invokeService += " -H \"sort: " + sort + " \" " ;
        }
        
        return    " # !/bin/bash \n\n" 
                + " # Script generated by G-JAX-CLIENT \n" 
                + " # Author : Rac021             \n\n\n " 
                + " Login=\""     + login    + "\"  \n\n " 
                + " Password=\""  + password + "\"  \n\n "
                + " TimeStamp=$(date +%s)           \n\n " 
                + getHashedScript( "Login"     , hashLogin     ) + "\n\n " 
                + getHashedScript( "Password"  , hashPassword  ) + "\n\n " 
                + getHashedScript( "TimeStamp" , hashTimeStamp ) + "\n\n "  
                + getSigneScript( algoSign )                     +  "\n\n " 
                + invokeService
                + "-H \"API-key-Token: " + "$Login $TimeStamp $SIGNE\" "  
                + "\"" +_url.replaceAll(" ", "%20") + "\"" ;
    }

    private static String getHashedScript( String variable, String algo ) {
      
        if(algo.equalsIgnoreCase("SHA1")) {
          return " Hashed_" + variable.trim() + "=` echo -n $" + variable.trim() + " | sha1sum  | cut -d ' ' -f 1 ` \n" 
               + "  Hashed_" + variable.trim() + "=` echo $Hashed_" + variable.trim() + " | sed 's/^0*//'`";
        }
        else if(algo.equalsIgnoreCase("MD5")) {
           return " Hashed_" + variable.trim() + "=` echo -n $" + variable.trim() + " | md5sum  | cut -d ' ' -f 1` \n" 
                + "  Hashed_" + variable.trim() + "=` echo $Hashed_" + variable.trim() + " | sed 's/^0*//'`" ;
        }
        
        return " Hashed_" + variable.trim() + "=\"$" + variable.trim() + "\""  ;
    }
    
    
    private static String getSigneScript( String algo ) {
      
        if(algo.equalsIgnoreCase("SHA1"))  {
          return " SIGNE=`echo -n $Hashed_Login$Hashed_Password$Hashed_TimeStamp | sha1sum  | cut -d ' ' -f 1 ` \n " 
               + " SIGNE=` echo $SIGNE | sed 's/^0*//' ` " ; 
        }
        else if(algo.equalsIgnoreCase("MD5")) {
          return " SIGNE=`echo -n $Hashed_Login$Hashed_Password$Hashed_TimeStamp | md5sum  | cut -d ' ' -f 1 ` \n "
               + " SIGNE=` echo $SIGNE | sed 's/^0*//' ` " ;
        }
        return " SIGNE=`echo -n $Hashed_Login$Hashed_Password$Hashed_TimeStamp ` " ;
    }

}
