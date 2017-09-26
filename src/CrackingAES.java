import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class CrackingAES {

    public static void main(String[] args) {
        long startTime = System.nanoTime();

        String[] ranges = {
                "0 1000000000",
                "1000000000 2000000000",
                "2000000000 3000000000",
                "3000000000 4000000000",
                "4000000000 5000000000",
                "5000000000 6000000000",
                "6000000000 7000000000",
                "7000000000 " + Math.pow(2, 33),
        };

        byte[] iv = {(byte) 0x00, (byte) 0xCF, (byte) 0x6C, (byte) 0x49, (byte) 0xA3, (byte) 0xD0,
                (byte) 0xD9, (byte) 0x30, (byte) 0x66, (byte) 0xE9, (byte) 0x89, (byte) 0xB6,
                (byte) 0x4F, (byte) 0xD2, (byte) 0x04, (byte) 0x5C};


        String string_ciphertext = "00CF6C49A3D0D93066E989B64FD2045CB02938" +
                "36986B1A624B8EC39EAE45EEC1483AB36FA32" +
                "851234A995D4F01CF13AFECAFAB471F24A03" +
                "C7D921D73131AFC6DA841C957BCAF565715FCE5355E1FAA03";


        byte[] cipherText = new byte[70];
        for (int f = 0; f < cipherText.length; f++) {
            cipherText[f] = (byte) (Integer.parseInt(string_ciphertext.substring(f*2, (f + 1)*2), 16) & 0xff);
        }

        StringBuilder right95Bits = new StringBuilder("11");
        int d = 0;
        while (d < 91) {
            right95Bits.append("0");
            d++;
        }

        right95Bits.append("11");

        try {
            for (long x = Long.parseLong(ranges[Integer.parseInt(args[0])].split(" ")[0]);
                 x <= Long.parseLong(ranges[Integer.parseInt(args[0])].split(" ")[1]); x++) {

                StringBuilder shortKey = new StringBuilder(Long.toBinaryString(x));
                StringBuilder stringKey = new StringBuilder();
                for (int z = 33; z > shortKey.length(); z--)
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
                        for (int z = 0; z < cleartextBlocks.length; z++) {
                            plaintext.append(Character.toString((char)cleartextBlocks[z]));
                        }
                        StringBuilder possibleKey = new StringBuilder();
                        for (int z = 0; z < key.length; z++) {
                            possibleKey.append(String.format("%02x ", key[z]));
                        }
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