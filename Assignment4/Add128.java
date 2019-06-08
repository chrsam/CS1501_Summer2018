/*
 *  Add128 Encryption Method 
 *  @author cam314 
 * 
 */

import java.util.*;

public class Add128 implements SymCipher {
    
    private byte[] key;  // Byte[] array to hold 128 bit key 
    
    // // Parameterless Constructor
    // public Add128() {
    //     Random rand = new Random(); // new random # generator to create random key bytes
    //     key = new byte[128]; // initialize byte array of size 128
    //     rand.nextBytes(key); // use random object to populate array with 128 random bytes
    // }
    
    // Constructor with byte[] as parameter to be used as key
    public Add128(byte[] b) {  
        if(b.length != 128){ // check for key length; throw exception if not 128 bytes
            throw new IllegalArgumentException("Invalid key parameter");
        }
        key = new byte[128]; // initialize 128 byte array
        this.key = b.clone(); // clone parameter and assign to key variable
    }

    // Parameterless Constructor
    public Add128() {
        Random rand = new Random(); // new random # generator to create random key bytes
        key = new byte[128]; // initialize byte array of size 128
        rand.nextBytes(key); // use random object to populate array with 128 random bytes
    }

    // // return copy of key as array of bytes
    // @Override  
    // public byte[] getKey() {
    //     return key.clone();
    // }

    // encode method
    // accepts String as argument
    @Override
    public byte[] encode(String S) {
       byte[] byteStr = S.getBytes();  // convert string to array of byte representations of each character
       // additive loop. stores sum of byteStr[i] + key[i%key.length] allows for wrapping if
       // String is longer than 128 chars
       for(int i = 0; i < byteStr.length; i++){  
          byteStr[i] =  (byte) (byteStr[i] + key[i%key.length]);
       }
       
       // return encrypted message as array of bytes
       return byteStr.clone();
    }

    // decode method
    // accepts encrypted message as array of bytes
    @Override
    public String decode(byte[] bytes) {
        byte[] byteStr = bytes.clone(); // copy byte array argument in byteStr[]
        
        // Subtractive loop to undoe the encryption. 
        for(int i = 0; i < byteStr.length; i++){
            byteStr[i] = (byte) (byteStr[i] - key[i%key.length]);
        }
        
        // return original message as a String object
        return new String(byteStr);
    }

     // return copy of key as array of bytes
     @Override  
     public byte[] getKey() {
         return key.clone();
     }
    
}