import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class BonusCrackingAES {

    public static void main(String[] args) {
        long startTime = System.nanoTime();

        String[] ranges = {
                "0 9100000000",
                "9100000000 18200000000",
                "18200000000 27300000000",
                "27300000000 36400000000",
                "36400000000 45500000000",
                "45500000000 54600000000",
                "54600000000 63700000000",
                "63700000000 72800000000",
                "72800000000 81900000000",
                "81900000000 91000000000",
                "91000000000 101000000000",
                "101000000000 112000000000",
                "112000000000 123000000000",
                "134000000000 134000000000",
                "134000000000 " + (long)Math.pow(2, 37)
        };

        System.out.println((long)Math.pow(2, 37));

        String string_ciphertext =  "2876200488DDDA80C48D9CACCFFAFECC" +
                                    "08E6DB070027DC60830AFE2E369C2DF7" +
                                    "5F96DDEA07C48788F82DCDAD17005B66" +
                                    "C2E846AFBDE6D4F1998004020A7ABEAE" +
                                    "7806FF0CCF5316DA13DF7622D19B8876";


        byte[] cipherText = new byte[80];
        for (int f = 0; f < cipherText.length; f++) {
            cipherText[f] = (byte) (Integer.parseInt(string_ciphertext.substring(f*2, (f + 1)*2), 16) & 0xff);
        }

        StringBuilder right95Bits = new StringBuilder("11");
        int d = 0;
        while (d < 87) {
            right95Bits.append("0");
            d++;
        }

        right95Bits.append("11");

        try {
            for (long x = Long.parseLong(ranges[Integer.parseInt(args[0])].split(" ")[0]);
                 x <= Long.parseLong(ranges[Integer.parseInt(args[0])].split(" ")[1]); x++) {

                StringBuilder shortKey = new StringBuilder(Long.toBinaryString(x));
                StringBuilder stringKey = new StringBuilder();
                for (int z = 37; z > shortKey.length(); z--)
                    stringKey.append("0");

                stringKey.append(shortKey);
                stringKey.append(right95Bits);

                byte[] key = new byte[16];

                for (int r = 0; r < key.length; r++)
                    key[r] = (byte) (Integer.parseInt(stringKey.toString().substring(r * 8, (r + 1) * 8), 2) & 0xff);

                Object decryptRoundKeys = Rijndael_Algorithm.makeKey(Rijndael_Algorithm.DECRYPT_MODE, key); //
                int numOfCiphertextBlocks = cipherText.length / 16 - 1; // Each AES block has 16 bytes and we need to exclude the IV
                byte[] cleartextBlocks = new byte[numOfCiphertextBlocks * 16];

                byte[] receivedIV = new byte[16];
                for (int i = 0; i < 16; i++) receivedIV[i] = cipherText[i];
                byte[] currentDecryptionBlock = new byte[16];

                decrypt:
                for (int i = 0; i < numOfCiphertextBlocks; i++) {
                    for (int j = 0; j < 16; j++)
                        currentDecryptionBlock[j] = cipherText[(i + 1) * 16 + j]; // Note that the first block is the IV

                    byte[] thisDecryptedBlock = Rijndael_Algorithm.blockDecrypt2(currentDecryptionBlock, 0, decryptRoundKeys);

                    for (int j = 0; j < 16; j++)
                        cleartextBlocks[i * 16 + j] = (byte) (thisDecryptedBlock[j] ^ cipherText[i * 16 + j]);

                    for (int k = 0; k < 16; k++) {
                        if (cleartextBlocks[i*16 + k] < 32 || cleartextBlocks[i*16 + k] >= 127) {
                            break decrypt;
                        }
                    }

                    if (i == numOfCiphertextBlocks - 1) {
                        StringBuilder plaintext = new StringBuilder();

                        for (byte b : cleartextBlocks)
                            plaintext.append(Character.toString((char)b));

                        StringBuilder possibleKey = new StringBuilder();

                        for (byte b : key)
                            possibleKey.append(String.format("%02x ", b));

                        PrintWriter pw = new PrintWriter(new FileWriter("possible.txt", true));
                        pw.println("Plaintext: " + plaintext.toString());
                        pw.println("Key for above: " + possibleKey.toString() + "\n");
                        pw.close();
                    }
                }
            }
        } catch(Exception e) {
            e.printStackTrace();
        }


        long endTime = System.nanoTime() - startTime;

        try {
            PrintWriter pw = new PrintWriter(new FileWriter("possible.txt", true));
            pw.println("Total time taken: " + endTime);
            pw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}