/*
 *  Substitution Encryption Method 
 *  @author cam314 
 */

import java.util.*;


public class Substitute implements SymCipher {

    // 2 byte arrays to hold key, and transposed key for decoding
    private byte[] key;
    private byte[] decodeKey;

    // Parameterless constructor
    public Substitute() {
        // New ArrayList<Byte> to allow for shuffling with Collections
        ArrayList<Byte> permByte = new ArrayList<Byte>(); 
        key = new byte[256];  // initialize key and decodeKey to size of 256
        decodeKey = new byte[256];

        // fill ArrayList with byte representations of 0-255
        for (int i = 0; i < 256; i++) {
            permByte.add((byte) i);
        }

        // shuffle ArrayList to randomize the key
        Collections.shuffle(permByte);

        // loop to copy elements from ArrayList to key, and transposition to decodeKey
        for (int i = 0; i < 256; i++) {
            key[i] = permByte.get(i);
            decodeKey[key[i] & 0xFF] = (byte) i;
        }

    }

    // Parameterized constructor, accepts byte array as argument/key
    // no need to randomize. Simply loop to copy transposed elements into decodeKey
    public Substitute(byte[] b) {
        if (b.length != 256) {
            throw new IllegalArgumentException("Illegal key parameters:");
        }

        this.key = b.clone();
        decodeKey = new byte[256];

        for (int i = 0; i < 256; i++) {
            decodeKey[b[i] & 0xFF] = (byte) i;
        }
    }

    // return copy fo the key. 
    @Override
    public byte[] getKey() {
        return key.clone();
    }

    // encode method
    // @param String S to represent message sent 
    @Override
    public byte[] encode(String S) {
        byte[] byteStream = S.getBytes();
        byte[] codeStream = new byte[S.length()];
        // loop through key and substitute key elements for message elements
        for (int i = 0; i < byteStream.length; i++) {
            codeStream[i] = key[byteStream[i] & 0xFF];
        }

        // return encrypted message as array of bytes
        return codeStream.clone();
    }

    // decode method
    // uses transposed key array to decrypt message
    @Override
    public String decode(byte[] bytes) {
        byte[] deBytes = new byte[bytes.length];

        // load transposed key array elements into deBytes
        for (int i = 0; i < bytes.length; i++) {
            deBytes[i] = (decodeKey[bytes[i] & 0xFF]);
        }
        
        // return decrypted message as a String object
        return new String(deBytes);
    }

}