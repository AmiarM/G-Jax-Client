
package com.rac021.jax.client.mvc ;

import java.awt.Color ;
import java.time.Instant ;
import java.util.logging.Level ;
import java.util.logging.Logger ;
import java.awt.event.ActionEvent ;
import java.security.NoSuchAlgorithmException ;
import com.rac021.jax.client.security.Digestor ;

/**
 *
 * @author ryahiaoui
 */
public class Controler       {

     private MainFrame frame ;

     String  token    = null ;
     
     
    public Controler() {
    }
     
    public Controler(MainFrame frame, Model model) {

        this.frame = frame     ;
       
        setListeners()         ;       
        frame.setVisible(true) ;
    }
    
    private void setListeners()                 {
        button_Run_ActionPerformed()            ;
        button_Run_Custom_ActionPerformed()     ;
        button_ClearResult_ActionPerformed()    ;
        button_Gen_Script_SSO_ActionPerformed() ;
        button_Dectypt_ActionPerformed()        ;
        button_Clear_Custom_ActionPerformed()   ;
        button_Script_CUSTOM()                  ;
       
    }
    
    
    private void button_Run_ActionPerformed() {
        
        frame.getButton_Run().addActionListener(e -> {
            try {
                button_Run_ActionPerformed(e) ;
            } catch (Exception ex) {
                Logger.getLogger(Controler.class.getName()).log(Level.SEVERE, null, ex) ;
            }
        }) ;
    }
    
    private synchronized void button_Run_ActionPerformed(ActionEvent e) throws Exception {
         
        frame.getXBusy().setBusy(true)                ;
        frame.getProgressBar().setIndeterminate(true) ;
        frame.getTextArea_Result().setText("")        ;
        
        Runnable r = () -> {
        
            frame.getTextArea_Result().setText("") ;
         
            String keyCLoakUrl = frame.getTextField_URL_KEYCLOAK().getText()  ;
            String userName    = frame.getTextField_Username().getText()      ;
            String password    = frame.getPasswordField_Password().getText()  ;
            String client_id   = frame.getTextField_Client_id().getText()     ;
            String secret_id   = frame.getPasswordField_secret_id().getText() ;
            String sort        = frame.getTextField_Sort().getText()          ;

            if( frame.getCheckBox_Refresh_Token().isSelected() ||  this.token == null ) {
                
                try {
                     this.token = Model.getToken( keyCLoakUrl, userName, password, client_id, secret_id) ;
                     frame.getTextArea_Token().setBackground(  Color.DARK_GRAY )                         ;
                     
                } catch( Exception x ) {
                    
                     frame.getTextArea_Token().setBackground(java.awt.Color.MAGENTA) ;
                     frame.getTextArea_Result().setText(" " + x.getMessage() )       ;
                     frame.getTextArea_Token().updateUI()                            ;
                     frame.getTextArea_Token().repaint()                             ;
                     frame.getXBusy().setBusy(false)                                 ;
                     frame.getProgressBar().setIndeterminate(false)                  ;
                     return                                                          ;
                }
            }

            if( this.token == null ) {
               frame.getTextArea_Result().setText(" ERROR Authentication ! " ) ;
               frame.getTextArea_Token().setText( " ERROR Authentication ! " ) ;
               frame.getXBusy().setBusy(false)                                 ;
               frame.getProgressBar().setIndeterminate(false)                  ;
             }
            
             else {

               frame.getTextArea_Token().setText(token.substring(0, 800 ) + "..." )    ;
               String url    = frame.getTextField_RUL_SERVICE().getText()              ;
               String params = frame.getTextField_Params().getText()                   ;
               String accept = frame.getComboBox_Accept().getSelectedItem().toString() ;
               Class  clazz  = String.class                                            ;

               try {

                   frame.getTextArea_Result()
                        .setText( Model.invokeService_Using_SSO( url +"?" + params ,
                                                      token    ,
                                                      accept   ,
                                                      clazz    ,
                                                      sort ) ) ;
                  frame.getTextArea_Token().setBackground( Color.DARK_GRAY ) ;
                  
               } catch ( Exception ex ) {
                   
                  frame.getTextArea_Token().setBackground(java.awt.Color.MAGENTA) ;
                  frame.getTextArea_Result().setText( "   " + ex.getMessage() )   ;
                  ex.printStackTrace()                                            ;

               } finally {
                  frame.getXBusy().setBusy(false) ;
                  frame.getProgressBar().setIndeterminate(false);
               }
             }
        } ;
                 
        new Thread(r).start() ;
    }

    private void button_ClearResult_ActionPerformed()   {
          
        frame.getButton_Clear().addActionListener( e -> {
            try {
               this.frame.getTextArea_Result().setText("") ;
               this.frame.getTextArea_Token().setText("")  ;
            } catch (Exception ex) {
                Logger.getLogger(Controler.class.getName()).log(Level.SEVERE, null, ex) ;
            }
        }) ;
    }

    
    private void button_Gen_Script_SSO_ActionPerformed() {
        
        frame.getButton_Script_SSO().addActionListener( e -> {
            
           try {
               
                String keyCLoakUrl = frame.getTextField_URL_KEYCLOAK().getText()  ;
                String userName    = frame.getTextField_Username().getText()      ;
                String password    = frame.getPasswordField_Password().getText()  ;
                String client_id   = frame.getTextField_Client_id().getText()     ;
                String secret_id   = frame.getPasswordField_secret_id().getText() ;
                String sort        = frame.getTextField_Sort().getText()          ;
                
                String url    = frame.getTextField_RUL_SERVICE().getText()              ;
                String params = frame.getTextField_Params().getText()                   ;
                String accept = frame.getComboBox_Accept().getSelectedItem().toString() ;
               
                frame.getTextArea_Result().setText ( Model.generateScriptSSO( keyCLoakUrl , 
                                                                              userName    ,
                                                                              password    ,
                                                                              client_id   , 
                                                                              secret_id   ,
                                                                              sort        ,
                                                                              url         ,
                                                                              params      ,
                                                                              accept )  ) ;
           } catch (Exception ex) {
                frame.getTextArea_Result().setText (ex.getMessage()) ;
            }
        }) ;
    }

    private void button_Run_Custom_ActionPerformed()        {
    
        frame.getButton_Run_Custom().addActionListener(e -> {
            try {
                button_Run_ActionPerformed_Custom(e) ;
            } catch (Exception ex) {
                Logger.getLogger(Controler.class.getName()).log(Level.SEVERE, null, ex) ;
            }
        }) ;
    }

    private void button_Run_ActionPerformed_Custom(ActionEvent e) throws NoSuchAlgorithmException, Exception {
       
        frame.getXBusy().setBusy(true)                ;
        frame.getProgressBar().setIndeterminate(true) ;
        frame.getTextArea_Result_Custom().setText("") ;
        
        Runnable r = () -> {
           
          try {
                String params          = frame.getTextField_Params_Custom().getText() ;
                String urlService      = frame.getTextField_RUL_SERVICE_Custom().getText() ;

                if(params != null && !params.isEmpty()) {
                    urlService += "?" + params ;
                }

                String sort      = frame.getTextField_Sort_Custom().getText()                     ;
                String accept    = frame.getComboBox_Accept_Custom().getSelectedItem().toString() ;

                String algoSign  = frame.getComboBox_AlgoSign().getSelectedItem().toString()      ;

                String login     =  hashMessage( frame.getTextField_Username_Custom().getText() , 
                                                 frame.getComboBox_HashedLogin().getSelectedItem().toString() )    ;

                String password  =  hashMessage( frame.getPasswordField_Password_Custom().getText() , 
                                                 frame.getComboBox_HashedPassword().getSelectedItem().toString())  ;

                String timeStamp =  hashMessage( frame.getTextField_TimeStamp().getText() , 
                                                 frame.getComboBox_HashedTimeStamp().getSelectedItem().toString()) ;
                
                if(frame.getCheckBox_TimeStamp().isSelected()) {
                     timeStamp = String.valueOf(Instant.now().getEpochSecond()) ;
                     frame.getTextField_TimeStamp().setText(timeStamp)          ;
                }
                
                String signe = null ;

                if(algoSign.equalsIgnoreCase("SHA1")) {
                    signe = Digestor.digestSha1( login + password + timeStamp ) ;
                }
                else if(algoSign.equalsIgnoreCase("MD5")) {
                    signe = Digestor.digestMD5( login + password + timeStamp ) ;
                }

               frame.getTextArea_Result_Custom().setText(Model.invokeService_Using_Custom( urlService , 
                                                                                           accept     , 
                                                                                           login + " " + timeStamp + " " + signe , 
                                                                                           String.class, sort )) ;
                frame.getXBusy().setBusy(false)                ;
                frame.getProgressBar().setIndeterminate(false) ;
          
          } catch( Exception x ) {
                frame.getXBusy().setBusy(false)                          ;
                frame.getProgressBar().setIndeterminate(false)           ;
               frame.getTextArea_Result_Custom().setText(x.getMessage()) ;
          }    
        } ;
                
       new Thread(r).start() ;
       
    }

    private String hashMessage( String message, String hashAlog ) throws NoSuchAlgorithmException {
        if(hashAlog.equalsIgnoreCase("SHA1")) return Digestor.digestSha1(message) ;
        if(hashAlog.equalsIgnoreCase("MD5")) return Digestor.digestMD5(message)   ;
        // Else PLAIN
        return message ;
    }

    private void button_Dectypt_ActionPerformed() {
    
         frame. getButton_Decrypt_Custom().addActionListener( e -> {
            try {
                decryptMessage(e)  ;
            } catch (Exception ex) {
                 frame.getTextArea_Token().setText( ex.getMessage()) ;
            }
        }) ;
    }

    private void decryptMessage(ActionEvent e ) throws Exception {
        
        frame.getTextArea_Token().setBackground(java.awt.Color.WHITE )                ;
        frame.getTextArea_Token().setText("")                                         ;
        String algo = frame.getComboBox_Decrypt_Custom().getSelectedItem().toString() ;
        
        String password = hashMessage( frame.getPasswordField_Password_Custom().getText()  , 
                                       frame.getComboBox_HashedPassword().getSelectedItem().toString()) ;
        try { 
            
            String decryptedMessage = Model.decrypt( algo     , 
                                                     password ,
                                                     frame.getTextArea_Result_Custom()
                                                          .getText())    ;

            frame.getTextArea_Result_Custom().setText( decryptedMessage) ;
            
        } catch( Exception x ) {
            frame.getTextArea_Token().setBackground(java.awt.Color.MAGENTA) ;
            frame.getTextArea_Token().setText(x.getMessage())               ;
        }
    }

    private void button_Clear_Custom_ActionPerformed() {
        
         frame.getButton_Clear_Custom().addActionListener(e -> {
            try {
               this.frame.getTextArea_Result_Custom().setText("") ;
               this.frame.getTextArea_Token().setText("")         ;
            } catch (Exception ex) {
                Logger.getLogger(Controler.class.getName()).log(Level.SEVERE, null, ex) ;
            }
        }) ;
    }

    private void button_Script_CUSTOM() {
  
       frame.getButton_Script_CUSTOM().addActionListener(  e ->  {

         try {
              this.frame.getTextArea_Result_Custom().setText("") ;
              this.frame.getTextArea_Token().setText("")         ;
              
              String url           =  frame.getTextField_RUL_SERVICE_Custom().getText()  ;
              String login         =  frame.getTextField_Username_Custom().getText()     ;
              String password      =  frame.getPasswordField_Password_Custom().getText() ;

              String params        =  frame.getTextField_Params_Custom().getText()       ;
              String sort          =  frame.getTextField_Sort_Custom().getText()         ;

              String algoSign      =  frame .getComboBox_AlgoSign().getSelectedItem().toString()       ;
              
              String accept        =  frame.getComboBox_Accept_Custom().getSelectedItem().toString()   ;
              String hashLogin     =  frame.getComboBox_HashedLogin().getSelectedItem().toString()     ;
              String hashPassword  =  frame.getComboBox_HashedPassword().getSelectedItem().toString()  ;
              String hashTimeStamp =  frame.getComboBox_HashedTimeStamp().getSelectedItem().toString() ;
            
              frame.getTextArea_Result_Custom().setText ( Model.generateScriptCUSTOM( url           , 
                                                                                      login         ,
                                                                                      password      ,
                                                                                      params        ,
                                                                                      sort          ,
                                                                                      accept        ,
                                                                                      hashLogin     ,
                                                                                      hashPassword  ,
                                                                                      hashTimeStamp ,
                                                                                      algoSign )  ) ;
           } catch (Exception ex) {
                frame.getTextArea_Result().setText (ex.getMessage()) ;
            }
        }) ;
    }
    
}
