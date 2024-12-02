/******************************************************************************
 *  Compilation:  javac BitmapCompressor.java
 *  Execution:    java BitmapCompressor - < input.bin   (compress)
 *  Execution:    java BitmapCompressor + < input.bin   (expand)
 *  Dependencies: BinaryIn.java BinaryOut.java
 *  Data files:   q32x48.bin
 *                q64x96.bin
 *                mystery.bin
 *
 *  Compress or expand binary input from standard input.
 *
 *  % java DumpBinary 0 < mystery.bin
 *  8000 bits
 *
 *  % java BitmapCompressor - < mystery.bin | java DumpBinary 0
 *  1240 bits
 ******************************************************************************/

/**
 *  The {@code BitmapCompressor} class provides static methods for compressing
 *  and expanding a binary bitmap input.
 *
 *  @author Robert Sedgewick
 *  @author Kevin Wayne
 *  @author Zach Blick
 *  @author Landon Moceri
 */
public class BitmapCompressor {

    static int MAX_COUNT = 255;
    static int BYTE_ALIGNMENT_SIZE = 8;
    /**
     * Reads a sequence of bits from standard input, compresses them,
     * and writes the results to standard output.
     */
    public static void compress() {
        // Start with the last bit being false
        boolean lastBit = false;
        int count = 0;

        // While there are still bits to read
        while (!BinaryStdIn.isEmpty()) {
            // Read the next bit
            boolean bit = BinaryStdIn.readBoolean();
            // If the bit is different from the last bit or the count exceeds 255
            if (bit != lastBit || count == MAX_COUNT) {
                // Continue writing 255 bits until the count is less than 255
                if (count > MAX_COUNT) {
                    // Write 255 bits
                    BinaryStdOut.write(MAX_COUNT, BYTE_ALIGNMENT_SIZE);
                    // Write 0 bits to the next byte to indicate continuation
                    BinaryStdOut.write(0, BYTE_ALIGNMENT_SIZE);
                    // Decrement the count by 255
                    count -= MAX_COUNT;
                }
                // Write the count of bits
                BinaryStdOut.write(count, BYTE_ALIGNMENT_SIZE);
                // Reset the count
                count = 0;
                // The last bit is now the current bit
                lastBit = bit;
            }
            // Increment the count
            count++;
        }

        // Handle any remaining counted bits using the same logic as before
        if (count > 0) {
            while (count > MAX_COUNT) {
                BinaryStdOut.write(MAX_COUNT, BYTE_ALIGNMENT_SIZE);
                BinaryStdOut.write(0, BYTE_ALIGNMENT_SIZE); // Write a 0 count to indicate continuation
                count -= MAX_COUNT;
            }
            BinaryStdOut.write(count, BYTE_ALIGNMENT_SIZE);
        }
        BinaryStdOut.close();
    }

    /**
     * Reads a sequence of bits from standard input, decodes it,
     * and writes the results to standard output.
     */
    public static void expand() {
        // Start again with the last bit being false
        boolean bit = false;
        // While there are still bits to read
        while (!BinaryStdIn.isEmpty()) {
            // Read the next count of bits
            int count = BinaryStdIn.readInt(8);
            // If the count is 255, write 255 of the current bit and continue
            while (count == 255) {
                for (int i = 0; i < 255; i++) {
                    BinaryStdOut.write(bit);
                }
                // Read the next count of bits
                count = BinaryStdIn.readInt(8);
            }

            // Write the count of bits
            for (int i = 0; i < count; i++) {
                BinaryStdOut.write(bit);
            }
            // Flip the bit
            bit = !bit;
        }
        BinaryStdOut.close();
    }

    /**
     * When executed at the command-line, run {@code compress()} if the command-line
     * argument is "-" and {@code expand()} if it is "+".
     *
     * @param args the command-line arguments
     */
    public static void main(String[] args) {
        if      (args[0].equals("-")) compress();
        else if (args[0].equals("+")) expand();
        else throw new IllegalArgumentException("Illegal command line argument");
    }
}