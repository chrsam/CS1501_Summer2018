/*************************************************************************
 *  Compilation:  javac LZW.java
 *  Execution:    java LZW - < input.txt   (compress)
 *  Execution:    java LZW + < input.txt   (expand)
 *  Dependencies: BinaryIn.java BinaryOut.java
 *
 *  Compress or expand binary input from standard input using LZW.
 *
 *
 *************************************************************************/

public class LZW {
    private static final int R = 256;        // number of input chars
    private static final int L = 4096;       // number of codewords = 2^W
    private static final int W = 12;         // codeword width
    private static final double COMPRESSION_RATIO_THRESHOLD = 1.1;

    public static void compress() { 
        String input = BinaryStdIn.readString();
        TST<Integer> st = new TST<Integer>();
        for (int i = 0; i < R; i++)
            st.put("" + (char) i, i);
        int code = R+1;  // R is codeword for EOF

        int uncompressedDataSize = 0; //Input data
        int compressedDataSize = 0; //Output data
        double compressionRatio = 0.0;
        // double newCompressionRatio = 0.0;
        // double ratioOfCompressionRatios = 0.0; 

        while (input.length() > 0) {
            String s = st.longestPrefixOf(input);  // Find max prefix match s.
            BinaryStdOut.write(st.get(s), W);      // Print s's encoding.
            int t = s.length();

            uncompressedDataSize += (t*16); //Data read is in the length of the string multiplied by 16-bits (size of each char)
            compressedDataSize += W; //Data written is current codeword width

            if (t < input.length() && code < L)    // Add s to symbol table.
                st.put(input.substring(0, t + 1), code++);
                compressionRatio = (double) uncompressedDataSize/compressedDataSize;
            input = input.substring(t);            // Scan past s in input.
            // newCompressionRatio = (double) uncompressedDataSize/compressedDataSize;
            // ratioOfCompressionRatios = oldCompressionRatio/newCompressionRatio;
        }
        System.err.println("Compression Ratio: " + compressionRatio);
        // System.err.println("New Compression Ratio: " + newCompressionRatio);
        // System.err.println("LZW Ratio: " + ratioOfCompressionRatios);
        BinaryStdOut.write(R, W);
        BinaryStdOut.close();
    } 


    public static void expand() {
        String[] st = new String[L];
        int i; // next available codeword value

        // initialize symbol table with all 1-character strings
        for (i = 0; i < R; i++)
            st[i] = "" + (char) i;
        st[i++] = "";                        // (unused) lookahead for EOF

        int codeword = BinaryStdIn.readInt(W);
        String val = st[codeword];

        while (true) {
            BinaryStdOut.write(val);
            codeword = BinaryStdIn.readInt(W);
            if (codeword == R) break;
            String s = st[codeword];
            if (i == codeword) s = val + val.charAt(0);   // special case hack
            if (i < L) st[i++] = val + s.charAt(0);
            val = s;
        }
        BinaryStdOut.close();
    }



    public static void main(String[] args) {
        if      (args[0].equals("-")) compress();
        else if (args[0].equals("+")) expand();
        else throw new RuntimeException("Illegal command line argument");
    }

}