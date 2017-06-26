
package com.rac021.client.security ;

import com.rac021.client.security.EncDecRyptor._Operation ;
import com.rac021.client.security.EncDecRyptor._CipherOperation ;



/**
 *
 * @author ryahiaoui
 */
public interface ICryptor {
    
      public byte[] process ( String message, _CipherOperation cipherOperation ) ;
      
      public void   setOperationMode( _Operation op )                            ;
      
      public byte[] getIvBytesEncoded64()                                        ;

}
