import java.math.BigInteger;

public class CrackingAES {

    public static void main(String[] args) {
        byte[] iv = {(byte) 0x00, (byte) 0xCF, (byte) 0x6C, (byte) 0x49, (byte) 0xA3, (byte) 0xD0,
                (byte) 0xD9, (byte) 0x30, (byte) 0x66, (byte) 0xE9, (byte) 0x89, (byte) 0xB6,
                (byte) 0x4F, (byte) 0xD2, (byte) 0x04, (byte) 0x5C};
        for (byte b : iv) {
            System.out.print(b);
        }
        System.out.println();

        String[] ciphertexts = {"B0293836986B1A624B8EC39EAE45EEC1",
                "483AB36FA32851234A995D4F01CF13AF",
                "ECAFAB471F24A03C7D921D73131AFC6D",
                "A841C957BCAF565715FCE5355E1FAA03"
        };

        StringBuilder right95Bits = new StringBuilder("11");
        int d = 0;
        while (d < 91) {
            right95Bits.append("0");
            d++;
        }

        right95Bits.append("11");



//        for (long x = 0; x < Math.pow(2, 33); x++) {
        for (long x = 0; x < 1; x++) {
            StringBuilder shortKey = new StringBuilder(Long.toBinaryString(x));
            StringBuilder key = new StringBuilder();
            for (int z = 33; z > shortKey.length(); z--)
                key.append("0");

            key.append(shortKey);
            key.append(right95Bits);


            System.out.println(key.toString());

            long dec = Long.parseLong(key.toString(), 2);
            String hex = Long.toString(dec, 16);

            System.out.println(hex);

            byte[] bytekey = new BigInteger(key.toString(), 128).toByteArray();
            for (byte b : bytekey) {
                System.out.print(b);
            }
            System.out.println();



//            Object decryptRoundKeys = Rijndael_Algorithm.makeKey (Rijndael_Algorithm.DECRYPT_MODE, key); //
//            int numOfCiphertextBlocks = ciphertexts.length / 16 - 1; // Each AES block has 16 bytes and we need to exclude the IV
//            byte[] cleartextBlocks = new byte[numOfCiphertextBlocks * 16];
//
//            byte[] receivedIV = new byte[16];
//            for (int i = 0; i < 16; i++) receivedIV[i] = ciphertexts[i];
//            byte[] currentDecryptionBlock = new byte[16];
//
//            for (int i=0; i < numOfCiphertextBlocks; i++) {
//                for (int j=0; j < 16; j++) currentDecryptionBlock [j] = ciphertexts[(i+1)*16 + j]; // Note that the first block is the IV
//
//                byte[] thisDecryptedBlock = Rijndael_Algorithm.blockDecrypt2 (currentDecryptionBlock, 0, decryptRoundKeys);
//
//                for (int j=0; j < 16; j++) cleartextBlocks[i*16+j] =  (byte) (thisDecryptedBlock[j] ^ ciphertexts[i*16 + j]);
//            }
//
//            for (int z = 0; z < ciphertexts.length; z++) {
//                if (z == 0) {
//
//                }
//            }

        }

    }

    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }
}