package com.company.huffman;

import java.io.*;
import java.nio.file.Files;
import java.util.Map;


public class file {

    public byte[] readBinaryFile(File file,long start,int size)
            throws IOException
    {

        // Creating an object of FileInputStream to
        // read from a file
        FileInputStream fl = new FileInputStream(file);

        // Skip some bytes as they have been read in another chunk
        fl.skip(start);
        byte[] arr = fl.readNBytes(size);


        // lastly closing an instance of file input stream
        // to avoid memory leakage
        fl.close();

        // Returning above byte array
        return arr;
    }


    public void writeFile(String outputPath, Map<String,String> output){

        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(outputPath,true));
            writer.write(output.size() + "\n");
            for (Map.Entry<String, String> bi : output.entrySet()){
                writer.append(bi.getKey() + "=" + bi.getValue() + "\n");
            }

//            System.out.println("Successfully wrote the map.");
            writer.close();
        } catch (IOException e) {
            System.out.println("An error occurred.");
            System.exit(0);
        }

    }


    public void checkFile(String outputPath){
        try {
            File file = new File(outputPath);
            Files.deleteIfExists(file.toPath());
        } catch (IOException e) {
            System.out.println("An error occurred.");
            System.exit(0);
        }
    }
}
