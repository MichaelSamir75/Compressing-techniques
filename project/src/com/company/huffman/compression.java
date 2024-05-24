package com.company.huffman;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;


public class compression {

    private String compressedPath;

    public void launch(String inputPath,int n) throws IOException {
        file file = new file();

        // get the path of the compressed file
        compressedPath = getCompressedPath(inputPath,n);
        file.checkFile(compressedPath);


        Map<String,Integer> freq = new HashMap<String,Integer>();

        // read the input file as chunks, each is array of bytes
        // then get the freq map of the elements of the file
        Vector<byte[]> totalFile = getFreq(inputPath,n,freq);

        // get the queue
        PriorityQueue<word> priorityQueue = createQueue(freq);
        word root = huffman(priorityQueue);

        // get the dictionary
        Map<String, String> codeword = new HashMap<String, String>();
        Map<String, String> decompress = new HashMap<String, String>();

        // check if the folder is empty, has only one char or more
        if(freq.size() > 1){
            createBinary(root, "", codeword, decompress);
        }
        else if(freq.size() == 1){
            for (Map.Entry<String, Integer> fr : freq.entrySet()){
                codeword.put(fr.getKey(), "0");
                decompress.put("0/1", fr.getKey());
            }
        }
        else{
            FileWriter fw =new FileWriter(compressedPath);
            fw.close();
            System.out.println("Successfully compressed the file.");
            return;
        }

        file.writeFile(compressedPath,decompress);
        encode(totalFile,n,codeword);

        File inputFile = new File(inputPath);
        File compressed = new File(compressedPath);
        if(inputFile.length() != 0) System.out.println("Compression ratio = " + (double) compressed.length()/inputFile.length());
        else System.out.println("Compression ratio = 0");

    }

    // read the input in chunks and get the freq of it
    private Vector<byte[]> getFreq(String inputPath,int n,Map<String,Integer> freq) throws IOException {
        file file = new file();
        File f = new File(inputPath);
        byte[] input;
        Vector<byte[]> totalFile = new Vector<>();
        long start = 0;
        if(f.length()> 20480){
            long size = f.length();

            // get int can be divided by n
            int dividedByN = (int) Math.floor(20480/n);
            int res = dividedByN*n;
            int chunks = (int) Math.ceil((double) size/res);
            while (chunks > 1){
                input = file.readBinaryFile(new File(inputPath),start,res);
                totalFile.add(input);
                calcFreq(input,n,freq);
                size-=res;
                start+=res;
                chunks--;
            }
            input = file.readBinaryFile(new File(inputPath),start,(int)size);
        }
        else{
            input = file.readBinaryFile(new File(inputPath),start,(int)f.length());
        }
        totalFile.add(input);
        calcFreq(input,n,freq);

        return totalFile;
    }

    // calculate the frequency of each n bytes
    private void calcFreq(byte[] input,int n,Map<String,Integer> freq){
        for(int i=0;i<input.length;i++){
            int counter = 0;
            String s = "";
            while (counter < n && i < input.length){
                s += Byte.toString(input[i++]);
                counter++;
                if(counter != n) s+=",";
            }
            i--;

            freq.merge(s,1,Integer::sum);
        }
    }

    // create the priority queue
    private PriorityQueue<word> createQueue(Map<String,Integer> freq){

        PriorityQueue<word> priorityQueue = new PriorityQueue<>(new wordComparator());

        for (Map.Entry<String, Integer> fr : freq.entrySet()){
            word word = new word();
            word.setWord(fr.getKey());
            word.setFreq(fr.getValue());
            priorityQueue.add(word);
        }
        return priorityQueue;
    }

    // the basic huffman code
    private word huffman(PriorityQueue<word> priorityQueue){
        int n = priorityQueue.size();

        for(int i=0;i<n-1;i++){
            word word = new word();
            word x = priorityQueue.poll();
            word y = priorityQueue.poll();
            word.setLeft(x);
            word.setRight(y);
            word.setFreq(x.getFreq()+y.getFreq());
            priorityQueue.add(word);
        }
        return priorityQueue.poll();
    }

    // get the codeword of each n bytes
    private void createBinary(word current, String binary, Map<String, String> codeword, Map<String, String> decompress){
        if(current.getLeft() == null){
            codeword.put(current.getWord(),binary);
            Long l = Long.parseLong(binary,2);
            String s = l + "/" + binary.length();
            decompress.put(s, current.getWord());
            return;
        }
        createBinary(current.getLeft(), binary + "0",codeword,decompress);
        createBinary(current.getRight(), binary + "1",codeword,decompress);
    }

    // this function is used to compress the input file assuming the basic unit is n bytes
    private void encode(Vector<byte[]> input, int n, Map<String,String> codeword) throws IOException {
        PrintWriter fo = new PrintWriter(new OutputStreamWriter(new FileOutputStream(compressedPath,true), StandardCharsets.ISO_8859_1));

        String encodedInput = "";
        String binary = "";

        for(int k=0;k<input.size();k++){
            for (int i = 0; i < input.get(k).length; i++) {
                int counter = 0;
                String word = "";
                while (counter < n && i < input.get(k).length){
                    word += Byte.toString(input.get(k)[i++]);
                    counter++;
                    if(counter != n) word+=",";
                }
                i--;
                String code = codeword.get(word);
                binary += code;

                while (binary.length() >= 8) {
                    // take only 8 bit and convert it to 1 byte
                    String first8 = binary.substring(0, 8);
                    int b = Integer.parseInt(first8, 2);
                    char c = (char) b;
                    // write the char to the compressed file
                    fo.print(c);
                    binary = binary.substring(8);
                }

            }
        }

        // check if there is remaining bits
        if (!binary.isEmpty()) {
            int b = Integer.parseInt(binary, 2);
            char c = (char) b;
            encodedInput += c;
            encodedInput += (char) binary.length();
        } else {
            encodedInput += (char) 8;
        }

        fo.print(encodedInput);
        fo.close();
        System.out.println("Successfully compressed the file.");

    }


    // this function is used to parse the input file path to get the output file path
    private String getCompressedPath(String inputPath,int n){
        String[] str = inputPath.split("\\\\");
        String compressedPath = "";
        for(int i=0;i<str.length-1;i++) compressedPath+=str[i] + "\\";
        compressedPath+=str[str.length-1] + ".hc";
        return compressedPath;
    }
}
