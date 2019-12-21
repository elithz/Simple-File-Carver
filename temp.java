import java.io.*;
import java.util.*;
import java.net.URLConnection;
import java.lang.*;

public class Main {
    static ArrayList<Integer> jpg_collection_header = new ArrayList<Integer>();
    static ArrayList<Integer> jpg_collection_footer = new ArrayList<Integer>();
    static ArrayList<Integer> jpg_collection_content = new ArrayList<Integer>();
    static ArrayList<Integer> pdf_collection_header = new ArrayList<Integer>();
    static ArrayList<Integer> pdf_collection_footer = new ArrayList<Integer>();
    static ArrayList<Integer> pdf_collection_content = new ArrayList<Integer>();
    static ArrayList<Integer> word_collection_header = new ArrayList<Integer>();
    static ArrayList<Integer> word_collection_footer = new ArrayList<Integer>();
    static ArrayList<Integer> word_collection_content = new ArrayList<Integer>();

    static ArrayList<Integer> jpg_pdf_overlap = new ArrayList<Integer>();
    static ArrayList<Integer> jpg_word_overlap = new ArrayList<Integer>();
    static ArrayList<Integer> pdf_word_overlap = new ArrayList<Integer>();
    static ArrayList<Integer> unClassified = new ArrayList<Integer>();

    public static void main(String[] argv) throws Exception {

        // boolean jpeg_header_found = false;
        // boolean jpeg_footer_found = false;
        // iterate the sector files
        for (int i = 0; i < 899; i++) {
            String sector_name = "BLOCK0" + String.format("%03d", i);
            FileInputStream fin = new FileInputStream(sector_name);

            int len;
            byte data[] = new byte[512];
            StringBuilder sb = new StringBuilder();
            // Read bytes until EOF is encountered.
            do {
                len = fin.read(data);
                for (int j = 0; j < len; j++) {
                    sb.append(String.format("%02x", data[j]));
                }
            } while (len != -1);
            fin.close();

            String str = sb.toString();

            // remove tail 0s in a sector
            for (int n = str.length() - 1; n >= 0; n--) {
                if (str.charAt(n) == '0')
                    str = str.substring(0, n);
                else
                    break;
            }
            // add spaces
            String val = "2";
            str = str.replaceAll("(.{" + val + "})", "$0 ").trim();
            // System.out.println(str);

            // System.out.println(str);

            int spaceCount = 0;
            int pdfFormatCont = 0;
            int zzcount = 0;
            int zocount = 0;
            int jpgzzCount = 0;

            // if there are a lot of spaces, it is very likely a word file
            for (int j = 0; j < str.length(); j += 3)
                if (j <= str.length() - 2)
                    if (str.charAt(j) == '2' && str.charAt(j + 1) == '0')
                        spaceCount++;

            // if patern like f0000000000 65535 is found, then it is likely pdf
            for (int j = 0; j < str.length(); j += 3)
                if (j <= str.length() - 2)
                    if (str.charAt(j) == '3' && str.charAt(j + 1) == '0')
                        pdfFormatCont++;

            // jpg patern like 01 00 repeatly show up
            for (int j = 0; j < str.length(); j += 3)
                if (j <= str.length() - 5) {
                    if (str.charAt(j) == '0' && str.charAt(j + 1) == '1' && str.charAt(j + 3) == '0'
                            && str.charAt(j + 4) == '0')
                        zocount++;
                } else
                    break;

            // jpg paturn like 00 00 releatly show up
            for (int j = 0; j < str.length(); j += 3)
                if (j <= str.length() - 5) {
                    if (str.charAt(j) == '0' && str.charAt(j + 1) == '0' && str.charAt(j + 3) == '0'
                            && str.charAt(j + 4) == '0')
                        jpgzzCount++;
                } else
                    break;

            // word patern 00 00 00 00 repeatly show up
            for (int j = 0; j < str.length(); j += 3)
                if (j <= str.length() - 8) {
                    if (str.charAt(j) == '0' && str.charAt(j + 1) == '0' && str.charAt(j + 3) == '0'
                            && str.charAt(j + 4) == '0' && str.charAt(j + 6) == '0' && str.charAt(j + 7) == '0')
                        zzcount++;
                } else
                    break;

            // // if patern like FFFFFFFFFFFFFF is found, then it is likely jpg
            // for (int j = 0; j < str.length(); j++)
            // if (str.charAt(j) == 'f')
            // jpegFFcount++;

            if (i == 892)
                System.out.println(zzcount);

            /**
             * 
             * JPEG file collection finder
             * 
             * 
             */
            if (str.contains("ff d8 ff")) {
                if (str.indexOf("ff d8 ff") == 0) {
                    jpg_collection_header.add(i);

                }
            }
            if (str.contains("ff d9")) {
                if (str.lastIndexOf("ff d9") == str.length() - 5) {
                    jpg_collection_footer.add(i);

                }
            }

            if ((str.contains("ff c0") || str.contains("ff c1") || str.contains("ff c2") || str.contains("ff c3")
                    || str.contains("ff c4") || str.contains("ff c5") || str.contains("ff c6") || str.contains("ff c7")
                    || str.contains("ff c8") || str.contains("ff c9") || str.contains("ff ca") || str.contains("ff cb")
                    || str.contains("ff cd") || str.contains("ff ce") || str.contains("ff cf") || str.contains("ff db")
                    || str.contains("ff dd") || str.contains("ff da") || str.contains("ff fe") || str.contains("ff d0")
                    || str.contains("ff d1") || str.contains("ff d2") || str.contains("ff d3") || str.contains("ff d4")
                    || str.contains("ff d5") || str.contains("ff 00") || str.contains("ff d6") || str.contains("ff d7")
                    || zocount >= 8 || jpgzzCount >= 50) && !str.contains("6f 62 6a 3c 3c")
                    && !str.contains("65 6e 64 73 74 72 65 61 6d") && !str.contains("65 6e 64 6f 62 6a")
                    && !str.contains("3e 3e 73 74 72 65 61 6d") && !str.contains("d0 cf 11 e0 a1 b1 1a e1")
                    && !str.contains(
                            "80 01 89 3c 01 30 00 00 00 00 00 00 00 00 01 00 00 00 01 00 00 00 00 00 00 00 00 00")
                    && zzcount < 20) {
                jpg_collection_content.add(i);
            }

            /**
             * 
             * 
             * PDF file collection finder
             * 
             */

            if (str.contains("25 50 44 46")) {
                if (str.indexOf("25 50 44 46") == 0) {
                    pdf_collection_header.add(i);
                }
            }
            if (str.contains("0a 25 25 45 4f 46") || str.contains("0a 25 25 45 4f 46 0a")
                    || str.contains("0d 0a 25 25 45 4f 46 0d 0a") || str.contains("0d 25 25 45 4f 46 0d")) {
                if (str.lastIndexOf("0a 25 25 45 4f 46") == str.length() - 17) {
                    pdf_collection_footer.add(i);
                } else if (str.lastIndexOf("0a 25 25 45 4f 46 0a") == str.length() - 20) {
                    pdf_collection_footer.add(i);
                } else if (str.lastIndexOf("0d 0a 25 25 45 4f 46 0d 0a") == str.length() - 26) {
                    pdf_collection_footer.add(i);
                } else if (str.lastIndexOf("0d 25 25 45 4f 46 0d") == str.length() - 20) {
                    pdf_collection_footer.add(i);
                }
            }
            if (str.contains("6f 62 6a 3c 3c") || str.contains("65 6e 64 73 74 72 65 61 6d")
                    || str.contains("65 6e 64 6f 62 6a") || str.contains("3e 3e 73 74 72 65 61 6d")
                    || pdfFormatCont >= 100 || str.contains("30 30 30 30 30 30 30 30 30")) {
                pdf_collection_content.add(i);
            }
            /**
             * 
             *
             * WORD file collection finder
             * 
             * 
             */
            if (str.contains("d0 cf 11 e0 a1 b1 1a e1")) {
                if (str.indexOf("d0 cf 11 e0 a1 b1 1a e1") == 0) {
                    word_collection_header.add(i);
                }
            }
            if (str.contains("57 6f 72 64 2e 44 6f 63 75 6d 65 6e 74 2e")) {
                // if (str.lastIndexOf("742e") == str.length() - 4) {
                word_collection_footer.add(i);
                // }
            }

            if ((str.contains("80 01 89 3c 01 30 00 00 00 00 00 00 00 00 01 00 00 00 01 00 00 00 00 00 00 00 00 00")
                    || spaceCount >= 10 || zzcount >= 20 || str.length() == 0) && !str.contains("6f 62 6a 3c 3c")
                    && !str.contains("65 6e 64 73 74 72 65 61 6d") && !str.contains("65 6e 64 6f 62 6a")
                    && !str.contains("3e 3e 73 74 72 65 61 6d") && pdfFormatCont < 100
                    && !str.contains("30 30 30 30 30 30 30 30 30")) {
                word_collection_content.add(i);
            }

        }

        if (!jpg_collection_content.contains(jpg_collection_header.get(0)))
            jpg_collection_content.add(jpg_collection_header.get(0));

        if (!jpg_collection_content.contains(jpg_collection_footer.get(0)))
            jpg_collection_content.add(jpg_collection_footer.get(0));

        if (!pdf_collection_content.contains(pdf_collection_header.get(0)))
            pdf_collection_content.add(pdf_collection_header.get(0));

        if (!pdf_collection_content.contains(pdf_collection_footer.get(0)))
            pdf_collection_content.add(pdf_collection_footer.get(0));

        if (!word_collection_content.contains(word_collection_header.get(0)))
            word_collection_content.add(word_collection_header.get(0));

        if (!word_collection_content.contains(word_collection_footer.get(0)))
            word_collection_content.add(word_collection_footer.get(0));

        // check content overlap
        for (int n = 0; n < jpg_collection_content.size(); n++)
            if (pdf_collection_content.contains(jpg_collection_content.get(n)))
                jpg_pdf_overlap.add(jpg_collection_content.get(n));

        for (int n = 0; n < jpg_collection_content.size(); n++)
            if (word_collection_content.contains(jpg_collection_content.get(n)))
                jpg_word_overlap.add(jpg_collection_content.get(n));

        for (int n = 0; n < pdf_collection_content.size(); n++)
            if (word_collection_content.contains(pdf_collection_content.get(n)))
                pdf_word_overlap.add(pdf_collection_content.get(n));

        // list unclassified
        for (int n = 0; n <= 898; n++)
            if (!jpg_collection_content.contains(n) && !pdf_collection_content.contains(n)
                    && !word_collection_content.contains(n)) {
                unClassified.add(n);
                pdf_collection_content.add(n);
            }

        /**
         * print jpeg sectors information
         */
        for (int i = 0; i < jpg_collection_header.size(); i++)
            System.out.println(
                    "Files contain JPEG header at the begining: " + jpg_collection_header.get(i).toString() + " ");
        for (int i = 0; i < jpg_collection_footer.size(); i++)
            System.out.println(
                    "Files contain JPEG footer at the ending: " + jpg_collection_footer.get(i).toString() + " ");
        System.out.println("Files contain JPEG contents: ");

        System.out.print(jpg_collection_content);
        System.out.println(" ");
        System.out.println("Number of Files contain JPEG contents: " + jpg_collection_content.size());
        /**
         * print pdf sectors information
         */
        for (int i = 0; i < pdf_collection_header.size(); i++)
            System.out.println(
                    "Files contain PDF header at the begining: " + pdf_collection_header.get(i).toString() + " ");
        for (int i = 0; i < pdf_collection_footer.size(); i++)
            System.out.println(
                    "Files contain PDF footer at the ending: " + pdf_collection_footer.get(i).toString() + " ");
        System.out.println("Files contain PDF contents: ");

        System.out.print(pdf_collection_content);
        System.out.println(" ");
        System.out.println("Number of Files contain PDF contents: " + pdf_collection_content.size());
        /**
         * print word sectors information
         */
        for (int i = 0; i < word_collection_header.size(); i++)
            System.out.println(
                    "Files contain WORD header at the begining: " + word_collection_header.get(i).toString() + " ");
        for (int i = 0; i < word_collection_footer.size(); i++)
            System.out.println(
                    "Files contain WORD footer at the ending: " + word_collection_footer.get(i).toString() + " ");

        System.out.println("Files contain WORD contents: ");
        System.out.print(word_collection_content);
        System.out.println(" ");
        System.out.println("Number of Files contain WORD contents: " + word_collection_content.size());

        System.out.println("overlap jpg and pdf: ");
        System.out.println(jpg_pdf_overlap);
        System.out.println("overlap jpg and word: ");
        System.out.println(jpg_word_overlap);
        System.out.println("overlap pdf and word: ");
        System.out.println(pdf_word_overlap);
        System.out.println("unclassified: ");
        System.out.println(unClassified);

        // JPEG recovery
        // jpg_collection_content.add(jpg_collection_footer.get(0));
        String FILEPATH = "file.jpeg";
        File file = new File(FILEPATH);
        List<Byte> bytes = new ArrayList<Byte>();
        for (int i = 0; i < 899; i++) {
            if (jpg_collection_content.contains(i)) {
                String sector_name = "BLOCK0" + String.format("%03d", i);
                FileInputStream fin = new FileInputStream(sector_name);

                int len;
                byte data[] = new byte[512];
                // Read bytes until EOF is encountered.
                do {
                    len = fin.read(data);
                    for (int j = 0; j < len; j++) {
                        bytes.add(data[j]);
                    }
                } while (len != -1);
                fin.close();
            }
        }

        try {
            Byte[] soundBytes = bytes.toArray(new Byte[bytes.size()]);
            byte[] finalbytes = new byte[bytes.size()];
            int j = 0;
            // Unboxing Byte values. (Byte[] to byte[])
            for (Byte b : soundBytes)
                finalbytes[j++] = b.byteValue();
            OutputStream os = new FileOutputStream(file);
            os.write(finalbytes);
            System.out.println("Successfully" + " byte inserted");

            // Close the file
            os.close();
        }

        catch (Exception e) {
            System.out.println("Exception: " + e);
        }

    }
}