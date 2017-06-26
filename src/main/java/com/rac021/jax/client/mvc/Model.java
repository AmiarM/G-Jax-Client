
package com.rac021.jax.client.mvc ;

import java.io.File ;
import java.util.UUID ;
import javax.json.Json ;
import java.awt.Toolkit ;
import java.io.StringReader ;
import javax.json.JsonString ;
import javax.ws.rs.core.Form ;
import javax.json.JsonReader ;
import java.security.KeyStore ;
import java.io.FileOutputStream ;
import javax.ws.rs.core.Response ;
import javax.ws.rs.client.Entity ;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.client.Client ;
import javax.ws.rs.client.Invocation ;
import java.awt.datatransfer.Clipboard ;
import javax.ws.rs.client.ClientBuilder ;
import com.rac021.client.security.ICryptor ;
import java.awt.datatransfer.StringSelection ;
import com.rac021.client.security.EncDecRyptor ;
import com.rac021.client.security.FactoryCipher ;

/**
 *
 * @author ryahiaoui
 */

public class Model {
    
    static KeyStore keystore ; 
    
    static {
        
           /* Initialise KeyStore for SSL Connections */
        
           try {
               
                String key      = UUID.randomUUID().toString()    ;
                String filename = "cacerts"                       ;
                
                /*
                String filename = System.getProperty("java.home")   +
                                  "/lib/security/cacerts"
                                  .replace('/', File.separatorChar) ;
                */
                File f = new File(filename) ;
                if( ! f.exists() ) f.createNewFile() ;
                
                keystore = KeyStore.getInstance(KeyStore.getDefaultType()) ;

                char[] password = key.toCharArray() ;
                keystore.load(null, password )      ;

                // Store away the keystore.
               try ( FileOutputStream fos = new FileOutputStream(filename) ) {
                   keystore.store(fos, password )           ;
               }

                keystore  = KeyStore.getInstance( KeyStore
                                    .getDefaultType() )     ;
                
           } catch( Exception ex )      {
               System.out.println( ex ) ;
           }
    }
    
    public Model() {
    }
        
    public static String getToken(  String url           , 
                                    String username      ,
                                    String password      , 
                                    String client_id     , 
                                    String client_secret ) throws Exception {

        Client clientB  ;
        
        if( url != null && url.startsWith("https"))    {
            
            clientB = ClientBuilder.newBuilder()
                                   .trustStore(keystore)
                                   .build()            ;
        } else {
            
            clientB = ClientBuilder.newClient()        ;
        }
         
        Invocation.Builder client = clientB.target( url )
                                           .request(MediaType.APPLICATION_JSON) ;
        
        Form form = new Form()                     ;
        form.param("username", username)           ;
        form.param("password", password)           ;
        form.param("client_id", client_id)         ;
        form.param("grant_type", "password")       ;
        form.param("client_secret", client_secret) ;
   
        Response response= client.post(Entity.form(form), Response.class) ;

        if (response.getStatus() == 200) {
            
               StringReader stringReader  = new StringReader(response.readEntity(String.class))  ;
               
               try (JsonReader jsonReader = Json.createReader(stringReader)) {
                   
                   JsonString jsonString = jsonReader.readObject().getJsonString("access_token") ;
                    
                    if( jsonString == null ) {
                        System.out.println(" BAD AUTHENTICATION ! ") ;
                        return null                                  ;
                    }
                    else {
                        return jsonString.getString() ;
                    }
               }
        }
        else {
          return   "_NULL_:" +response.readEntity(String.class) ;
        }
    }    
         
    public static String invokeService_Using_SSO ( String url    ,
                                                   String token  ,
                                                   String accept , 
                                                   Class  clazz  ,
                                                   String keep   ) throws Exception {
        Client clientB  ;
        
        if( url != null && url.startsWith("https"))    {
            
            clientB = ClientBuilder.newBuilder()
                                   .trustStore(keystore)
                                   .build()            ;
        } else {
            
            clientB = ClientBuilder.newClient()        ;
        }
         
        Invocation.Builder client = clientB.target( url )
                                           .request()
                                           .header("accept", accept)
                                           .header("Authorization", " Bearer " + token ) ;
        
        if( keep != null && ! keep.isEmpty() ) {
            client.header("keep", keep ) ;
        }
        
        return client.get(String.class)  ;

    }
    
    public static String invokeService_Using_Custom ( String url    ,
                                                      String accept , 
                                                      String token  ,
                                                      Class  clazz  ,
                                                      String keep   ,
                                                      String cipher ) throws Exception {

        Client clientB  ;
        
        if( url != null && url.startsWith("https"))    {
            
            clientB = ClientBuilder.newBuilder()
                                   .trustStore(keystore)
                                   .build()            ;
        } else {
            
            clientB = ClientBuilder.newClient()        ;
        }
         
        Invocation.Builder client = clientB.target( url )
                                           .request()
                                           .header("accept", accept)
                                           .header( "API-key-Token", token.trim() ) ;
        
        if( keep != null && ! keep.isEmpty() ) {
             client.header("keep", keep ) ;
        }
        
        if( cipher != null && ! cipher.isEmpty() ) {
             client.header("cipher", cipher ) ;
        }
        
        return client.get(String.class) ;
    }
    
    private static String getBlanc( int nbr ) {
        String blanc = " " ;
        for(int i = 0 ; i < nbr ; i++ ) {
            blanc += " "   ;
        }
        return blanc       ;
    }
    
    public static String generateScriptSSO( String keyCloakUrl , 
                                            String userName    , 
                                            String password    , 
                                            String client_id   , 
                                            String secret_id   , 
                                            String keep        , 
                                            String url         , 
                                            String params      , 
                                            String accept )    {
        
        
        String trustCertkeyCloakUrl = keyCloakUrl.trim().startsWith("https") ? " -k " : " " ;
        String trustCertUrl         = url.trim().startsWith("https") ? " -k " : " " ;
         
        String KEYCLOAK_RESPONSE = " KEYCLOAK_RESPONSE=`curl "
                                   + trustCertkeyCloakUrl
                                   + "-s -X POST " + keyCloakUrl  + " \\\n " 
                                   + getBlanc(50) + " -H \"Content-Type: application/x-www-form-urlencoded\" \\\n " 
                                   + getBlanc(50) + " -d 'username=" + userName + "' \\\n "
                                   + getBlanc(50) + " -d 'password=" + password + "' \\\n "
                                   + getBlanc(50) + " -d 'grant_type=password' \\\n "
                                   + getBlanc(50) + " -d 'client_id=" + client_id + "' \\\n "
                                   + getBlanc(50) + " -d 'client_secret=" + secret_id + "' ` \n " ;
                 
        String _token = " ACCESS_TOKEN=`echo $KEYCLOAK_RESPONSE | " + 
                        "sed 's/.*access_token\":\"//g' | sed 's/\".*//g'` " ;
               
        String invokeService =   " curl "        +
                                 trustCertUrl    +
                                 "-H \"accept: " + 
                                 accept + "\"  " + 
                                 " -H \"Authorization: Bearer $ACCESS_TOKEN\" " ;
               
        if( keep != null && ! keep.isEmpty() ) {
             invokeService += " -H \"keep: " + keep + " \" " ;
        }

        String _url = url ;
        if( params != null && ! params.isEmpty() )
           _url += "?" + params ;
        
        invokeService += "\"" + _url + "\" " ;
               
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
    
    public static String decrypt( String cipher , String pass ,String text ) throws Exception {

        ICryptor crypt = FactoryCipher.getCipher( cipher , pass ) ;

        crypt.setOperationMode(EncDecRyptor._Operation.Decrypt )  ;

        return new String ( crypt.process( text, EncDecRyptor._CipherOperation.dofinal ) ) ;
        
    }
    
    public static String generateScriptCUSTOM ( String url           , 
                                                String login         , 
                                                String password      , 
                                                String params        , 
                                                String keep          ,
                                                String accept        ,
                                                String hashLogin     , 
                                                String hashPassword  , 
                                                String hashTimeStamp ,
                                                String algoSign      ,
                                                String cipher      ) {
            
        String _url = url ;
        if( params != null && ! params.isEmpty() )
           _url += "?" + params ;
        
        String trustCert = url.trim().startsWith("https") ? " -k " : " " ;
        
        String invokeService =  " curl "        +
                                trustCert       +
                                "-H \"accept: " +
                                accept + "\"  " ;
               
        if( keep != null && ! keep.isEmpty() ) {
          invokeService += " -H \"keep: " + keep + " \" " ;
        }
        
        if( cipher != null && ! cipher.isEmpty() ) {
          invokeService += " -H \"cipher: " + cipher + " \" " ;
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
          return " Hashed_"                            + 
                 variable.trim()                       + 
                 "=` echo -n $"                        + 
                 variable.trim()                       + 
                 " | sha1sum  | cut -d ' ' -f 1 ` \n"  + 
                 "  Hashed_"                           + 
                 variable.trim()                       + 
                 "=` echo $Hashed_"                    + 
                 variable.trim()                       + 
                 " | sed 's/^0*//'`"                   ;
        }
        if(algo.equalsIgnoreCase("SHA2")) {
          return " Hashed_"                             + 
                 variable.trim()                        + 
                 "=` echo -n $"                         + 
                 variable.trim()                        + 
                 " | sha256sum  | cut -d ' ' -f 1 ` \n" + 
                 "  Hashed_"                            + 
                 variable.trim()                        + 
                 "=` echo $Hashed_"                     + 
                 variable.trim()                        + 
                 " | sed 's/^0*//'`"                    ;
        }
        else if(algo.equalsIgnoreCase("MD5")) {
           return " Hashed_"                           + 
                  variable.trim()                      + 
                  "=` echo -n $"                       + 
                  variable.trim()                      + 
                  " | md5sum  | cut -d ' ' -f 1` \n"   + 
                  "  Hashed_" + variable.trim()        +
                  "=` echo $Hashed_" + variable.trim() +
                  " | sed 's/^0*//'`" ;
        }
        
        return " Hashed_" + variable.trim() + "=\"$" + variable.trim() + "\""  ;
    }
    
    
    private static String getSigneScript( String algo ) {
      
        if(algo.equalsIgnoreCase("SHA1"))  {
          return " SIGNE=`echo -n $Hashed_Login$Hashed_Password$Hashed_TimeStamp" + 
                  " | sha1sum  | cut -d ' ' -f 1 ` \n "                           + 
                  " SIGNE=` echo $SIGNE | sed 's/^0*//' ` "                       ; 
        }
        if(algo.equalsIgnoreCase("SHA2"))  {
          return " SIGNE=`echo -n $Hashed_Login$Hashed_Password$Hashed_TimeStamp" + 
                  " | sha256sum  | cut -d ' ' -f 1 ` \n "                         + 
                  " SIGNE=` echo $SIGNE | sed 's/^0*//' ` "                       ; 
        }
        else if(algo.equalsIgnoreCase("MD5")) {
          return " SIGNE=`echo -n $Hashed_Login$Hashed_Password$Hashed_TimeStamp " + 
                 "| md5sum  | cut -d ' ' -f 1 ` \n "                               +
                 " SIGNE=` echo $SIGNE | sed 's/^0*//' ` "                         ;
        }
        return " SIGNE=`echo -n $Hashed_Login$Hashed_Password$Hashed_TimeStamp ` " ;
    }
    
    public static void copyToClipBoard( String text )                         {
        StringSelection stringSelection = new StringSelection (text)          ;
        Clipboard clpbrd = Toolkit.getDefaultToolkit ().getSystemClipboard () ;
        clpbrd.setContents (stringSelection, null)                            ;
    }

}
