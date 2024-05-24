package com.company.LZW;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class LZWDecompression {

    public int numOfbits = 0;
    public String byteToString[] = new String[256];
    public String big1 = "";

    public int convertByteToInt(Byte byt) {
        int ret = byt;
        if (ret < 0)
            ret += 256;
        return ret;
    }

    public int convertStringToInt(String s) {
        int ret = 0, i;
        for (i = 0; i < s.length(); i++) {
            ret *= 2;
            if (s.charAt(i) == '1')
                ret++;
        }
        return ret;
    }

    public void decompress(String compressedPath , String outPath) {
        Map<Integer, String> dictionary = new HashMap<Integer, String>();
        int dictionarySize = 256 , mapSize = 256 , k;
        File fileIn =  new File(compressedPath);
        File fileOut = new File(outPath);
        String ts;

        for (int i = 0; i < 256; i++)
            dictionary.put(i, "" + (char) i);

        try {
            FileInputStream fileInput = new FileInputStream(fileIn);
            DataInputStream dataIn = new DataInputStream(fileInput);
            FileOutputStream fileOutput = new FileOutputStream(fileOut);
            DataOutputStream dataOut = new DataOutputStream(fileOutput);
            numOfbits = dataIn.readInt(); // get number of bytes in this file

            while (true) {
                try {
                    big1 += byteToString[convertByteToInt(dataIn.readByte())];
                    if (big1.length() >= numOfbits)
                        break;
                } catch (EOFException eof) {
                    System.out.println("End of File");
                    break;
                }
            }
            if (big1.length() >= numOfbits) {
                k = convertStringToInt(big1.substring(0, numOfbits));
                big1 = big1.substring(numOfbits, big1.length());
            } else {
                dataIn.close();
                dataOut.close();
                return;
            }
            String w = "" + (char) k;
            dataOut.writeBytes(w);
            while (true) {
                try {
                    while (big1.length() < numOfbits)
                        big1 += byteToString[convertByteToInt(dataIn.readByte())];

                    k = convertStringToInt(big1.substring(0, numOfbits));
                    big1 = big1.substring(numOfbits, big1.length());

                    String entry = "";
                    if (dictionary.containsKey(k))
                        entry = dictionary.get(k);
                    else if (k == dictionarySize)
                        entry = w + w.charAt(0);

                    dataOut.writeBytes(entry);
                    if (mapSize < 100000) {
                        ts = w + entry.charAt(0);
                        dictionary.put(dictionarySize++, ts);
                        mapSize += ts.length();
                    }
                    w = entry;
                } catch (EOFException eof) {
                    System.out.println("End of File");
                    break;
                }
            }
            dataIn.close();
            dataOut.close();
            fileInput.close();
            fileOutput.close();
        } catch (IOException e) {
            System.out.println("IO exception = " + e);
        }
    }

    public void begin(String compressedPath , String outputPath) {
        String tmp;
        byteToString[0] = "00000000";
        // create array to represent int as string of 0's & 1's
        for (int i=1 , j; i < 256; i++) {
            tmp = "";
            j = i;
            byteToString[i] = "";
            while (j != 0) {
                if ((j % 2) == 1)
                    byteToString[i] += "1";
                else
                    byteToString[i] += "0";
                j /= 2;
            }
            for (j = byteToString[i].length() - 1; j >= 0; j--)
                tmp += byteToString[i].charAt(j);
            while (tmp.length() < 8)
                tmp = "0" + tmp;
            byteToString[i] = tmp;
        }
        decompress(compressedPath , outputPath);
    }

    public static void main(String[] args) {

    }

}