
package entry ;

import com.rac021.jax.client.mvc.Model ;
import com.rac021.jax.client.mvc.Controler ;
import com.rac021.jax.client.mvc.MainFrame ;

/**
 *
 * @author ryahiaoui
 */

public class Starter {

	
    public static void main(String[] args) throws Exception               {
            
       Controler controler = new Controler (new MainFrame(), new Model()) ;
    
    }

}

        
