import java.io.*;
import java.util.*;

public class reader {
    public static void main(String[] argv) throws Exception {
        // String FILEPATH = "file.jpeg";
        // File file = new File(FILEPATH);
        // List<Byte> bytes = new ArrayList<Byte>();
        //for (int i = 785; i <= 898; i++) {
            // String sector_name = "BLOCK0" + String.format("%03d", i);
            String sector_name = "BLOCK0516";
            FileInputStream fin = new FileInputStream(sector_name);

            int len;
            byte data[] = new byte[16];
            //System.out.println((int) file.length());
            // Read bytes until EOF is encountered.
            do {
                len = fin.read(data);
                for (int j = 0; j < len; j++) {
                    System.out.printf("%02X ", data[j]);
                    //bytes.add(data[j]);
                }
            } while (len != -1);

        //}

        
        // try {
        //     Byte[] soundBytes = bytes.toArray(new Byte[bytes.size()]);
        //     byte[] finalbytes = new byte[bytes.size()];
        //     int j = 0;
        //     // Unboxing Byte values. (Byte[] to byte[])
        //     for (Byte b : soundBytes)
        //         finalbytes[j++] = b.byteValue();
        //     OutputStream os = new FileOutputStream(file);
        //     os.write(finalbytes);
        //     System.out.println("Successfully" + " byte inserted");

        //     // Close the file
        //     os.close();
        // }

        // catch (Exception e) {
        //     System.out.println("Exception: " + e);
        // }

    }
}