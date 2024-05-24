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

public class LZWCompression {

    public int numOfbits = 0;
    public String str = "";

    public int convertByteToInt(Byte byt) {
        int ret = byt;
        if (ret < 0)
            ret += 256;
        return ret;
    }

    public Byte convertStringToByte(String in) {
        int len = in.length();
        byte res = 0;
        for (int i = 0; i < len; i++) {
            res *= 2.;
            if (in.charAt(i) == '1')
                res++;
        }
        for (; len < 8; len++)
            res *= 2.;
        return (Byte) res;
    }

    public String convertIntToString(int input) {
        String res = (input == 0)?"0":"";
        while (input != 0) {
            if ((input % 2) == 1)
                res = "1" + res;
            else
                res = "0" + res;
            input /= 2;
        }
        while (res.length() != numOfbits)
            res = "0" + res;
        return res;
    }

    public void processingFile(String filePath) {
        Map<String, Integer> dictionary = new HashMap<String, Integer>();
        int dictionarySize = 256;
        int mapSize = 256;
        String current = "";
        File file = new File(filePath);

        for (int i = 0; i < 256; i++)
            dictionary.put("" + (char) i, i);

        try {
            FileInputStream fileInput = new FileInputStream(file);
            DataInputStream dataIn = new DataInputStream(fileInput);
            int charac;
            while (true) {
                try {
                    charac = convertByteToInt(dataIn.readByte());
                    String combined = current + (char) charac;
                    if (dictionary.containsKey(combined))
                        current = combined;
                    else {
                        if (mapSize < 100000) {
                            dictionary.put(combined, dictionarySize++);
                            mapSize += combined.length();
                        }
                        current = "" + (char) charac;
                    }
                } catch (EOFException eof) {
                    break;
                }
            }
            fileInput.close();
            dataIn.close();
        } catch (IOException e) {
            System.out.println("IO exception = " + e);
        }
        if (dictionarySize > 1) {
            numOfbits = 0;
            long i = 1;
            while (i < dictionarySize) {
                i *= 2;
                numOfbits++;
            }
        } else
            numOfbits = 1;
    }

    public void compress(String inFilePath , String outFilePath) {
        Map<String, Integer> dictionary = new HashMap<String, Integer>();
        int dictionarySize = 256;
        int mapSize = 256;
        String current = "";
        File fileIn = new File(inFilePath);
        File fileOut = new File(outFilePath);
        str = "";

        for (int i = 0; i < 256; i++)
            dictionary.put("" + (char) i, i);
        try {
            FileInputStream fileInput = new FileInputStream(fileIn);
            DataInputStream dataIn = new DataInputStream(fileInput);
            FileOutputStream fileOutput = new FileOutputStream(fileOut);
            DataOutputStream dataOut = new DataOutputStream(fileOutput);

            dataOut.writeInt(numOfbits);
            int chara;
            while (true) {
                try {
                    chara = convertByteToInt(dataIn.readByte());
                    String combined = current + (char) chara;
                    if (dictionary.containsKey(combined))
                        current = combined;
                    else {
                        // convert int to string of 0 & 1
                        str += convertIntToString(dictionary.get(current));
                        while (str.length() >= 8) {
                            dataOut.write(convertStringToByte(str.substring(0, 8)));
                            str = str.substring(8, str.length());
                        }
                        if (mapSize < 100000) {
                            dictionary.put(combined, dictionarySize++);
                            mapSize += combined.length();
                        }
                        current = "" + (char) chara;
                    }
                } catch (EOFException eof) {
                    System.out.println("End of File");
                    break;
                }
            }

            if (!current.equals("")) {
                str += convertIntToString(dictionary.get(current));
                while (str.length() >= 8) {
                    dataOut.write(convertStringToByte(str.substring(0, 8)));
                    str = str.substring(8, str.length());
                }
                if (str.length() >= 1) {
                    dataOut.write(convertStringToByte(str));
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

    public void begin(String inFilePath , String outFilePath) {
        processingFile(inFilePath);
        compress(inFilePath , outFilePath);
    }

}
