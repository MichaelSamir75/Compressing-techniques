package com.company.huffman;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;


public class decompression2 {

    private String outputPath;

    public void launch(String compressedPath) throws IOException {
        outputPath = getOutputPath(compressedPath);

        file file = new file();
        file.checkFile(outputPath);

        File f = new File(compressedPath);
        if(f.length() ==0) {
            FileWriter fw =new FileWriter(outputPath);
            fw.close();
            System.out.println("Successfully decompressed the file.");
            return;
        }


        FileInputStream fl = new FileInputStream(compressedPath);
        InputStreamReader isr = new InputStreamReader(fl, StandardCharsets.ISO_8859_1);
        File outputFile = new File(outputPath);
        FileOutputStream outputStream = new FileOutputStream(outputFile,true);

        // get the map
        Map<String,String> codeword = new HashMap<>();
        int counter = getMap(compressedPath,codeword);
        isr.skip(counter);
//        System.out.println("Successfully read the map.");



        String binary = "";
        String toWrite = "";
        Vector<String> toOutput = new Vector<>();



        for(long i=counter;i<f.length()-2;i++){
            int decimal = isr.read();
            String binaryString = Integer.toBinaryString(decimal);
            StringBuilder temp = new StringBuilder();
            int diff = 8 - binaryString.length();
            while (diff > 0) {
                temp.append("0");
                diff--;
            }
            binary += temp+binaryString;


            for (int j = 0; j < binary.length(); j++) {
                toWrite += binary.charAt(j);
                if (codeword.containsKey(toWrite)) {
                    String tmp = codeword.get(toWrite);
                    String[] arr = tmp.split(",");
                    Collections.addAll(toOutput, arr);
                    toWrite = "";
                }
            }
            binary = "";

            if(toOutput.size() > 2000000){
                for (String s : toOutput) {
                    outputStream.write(Byte.parseByte(s));
                }
                toOutput.clear();
            }
        }


        // check the last element
        int decimalLastElement = isr.read();
        int lastElementSize = isr.read();
        String binaryString = Integer.toBinaryString(decimalLastElement);
        StringBuilder temp = new StringBuilder();
        int diff = lastElementSize - binaryString.length();
        while (diff > 0) {
            temp.append("0");
            diff--;
        }
        binary += temp+binaryString;
        for(int j=0;j<binary.length();j++){
            toWrite+= binary.charAt(j);
            if(codeword.containsKey(toWrite)) {
                String tmp = codeword.get(toWrite);
                String[] arr = tmp.split(",");
                Collections.addAll(toOutput, arr);
                toWrite="";
            }
        }
        for (String s : toOutput) {
            outputStream.write(Byte.parseByte(s));
        }

        outputStream.close();
        fl.close();
        System.out.println("Successfully decompressed the file.");
    }

    private int getMap(String compressedPath,Map<String,String> codeword){
        int counter =0;
        try {
            BufferedReader reader = new BufferedReader(new FileReader(compressedPath));
            String data = reader.readLine();
            int size = Integer.parseInt(data);
            counter+=Integer.toString(size).length()+1;
            for(int i=0;i<size;i++){
                data = reader.readLine();
                counter+=data.length()+1;
                String[] str = data.split("=");
                String[] strings = str[0].split("/");
                Long l = Long.parseLong(strings[0]);
                String bi = Long.toBinaryString(l);
                int diff = Integer.parseInt(strings[1]) - bi.length();
                String tmp = "";
                while (diff > 0){
                    tmp+="0";
                    diff--;
                }

                codeword.put(tmp+bi,str[1]);
            }
            reader.close();
        } catch (Exception e) {
            System.out.println("An error occurred in reading the files.");
            e.printStackTrace();
            System.exit(0);
        }
        return counter;
    }


    private String getOutputPath(String compressedPath){
        String[] str = compressedPath.split("\\\\");
        String outputPath = "";
        for(int i=0;i<str.length-1;i++) outputPath+=str[i] + "\\";
        String[] str2 = str[str.length-1].split("\\.");
        outputPath+= "extracted.";
        for(int i=0;i< str2.length-1;i++){
            outputPath+=str2[i];
            if(i != str2.length-2) outputPath += ".";
        }
        return outputPath;
    }
}
