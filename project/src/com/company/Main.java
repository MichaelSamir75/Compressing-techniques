package com.company;
import com.company.LZW.LZWCompression;
import com.company.LZW.LZWDecompression;
import com.company.arithmetic.Decode;
import com.company.arithmetic.Encode;
import com.company.huffman.compression;
import com.company.huffman.decompression2;

import java.io.IOException;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Choose Algorithm: \n 1. Huffman \n 2. Arithmetic \n 3. LZW");
        String algorithm = scanner.nextLine();
        System.out.println("Choose the operation you want to perform: \n 1. Compress \n 2. Decompress");
        String operation = scanner.nextLine();
        System.out.println("Enter the input file path: ");
        String inputFilePath = scanner.nextLine();
        String outputFilePath = "";
        if(!algorithm.equals("1")){
            System.out.println("Enter the output file path: ");
            outputFilePath = scanner.nextLine();
        }
        scanner.close();

        if(algorithm.equals("1")){
            if (operation.equals("1")){
                long start = System.currentTimeMillis();
                compression compression = new compression();
                compression.launch(inputFilePath, 1);
                long end = System.currentTimeMillis();
                System.out.println("Compress time: " + (double) (end - start) / 1000.0 + " Seconds");
            }else if(operation.equals("2")){
                long start = System.currentTimeMillis();
                decompression2 decompression = new decompression2();
                decompression.launch(inputFilePath);
                long end = System.currentTimeMillis();
                System.out.println("Decompress time: " + (double) (end - start) / 1000.0 + " Seconds");
            }
        }else if(algorithm.equals("2")){
            if(operation.equals("1")){
                long start = System.currentTimeMillis();
                Encode encoder = new Encode();
                encoder.encode(inputFilePath, outputFilePath);
                long end = System.currentTimeMillis();
                System.out.println("Compress time: " + (double) (end - start) / 1000.0 + " Seconds");
            }else if(operation.equals("2")){
                long start = System.currentTimeMillis();
                Decode decoder = new Decode();
                decoder.decode(inputFilePath, outputFilePath);
                long end = System.currentTimeMillis();
                System.out.println("Deompress time: " + (double) (end - start) / 1000.0 + " Seconds");
            }
        }else if (algorithm.equals("3")){
            if(operation.equals("1")){
                LZWCompression compress = new LZWCompression();
                long startCompressionTime = System.currentTimeMillis();
                compress.begin(inputFilePath, outputFilePath);
                long endCompressionTime = System.currentTimeMillis();
                long compressionTime = endCompressionTime - startCompressionTime;
                System.out.println("File compressed successfully @ " + outputFilePath);
                System.out.println("Compression time: " + (compressionTime / 1000) + " seconds");
            }else if(operation.equals("2")){
                LZWDecompression decompress = new LZWDecompression();
                long startDecompressionTime = System.currentTimeMillis();
                decompress.begin(inputFilePath, outputFilePath);
                long endDecompressionTime = System.currentTimeMillis();
                long decompressionTime = endDecompressionTime - startDecompressionTime;
                System.out.println("File decompressed successfully @ " + outputFilePath);
                System.out.println("Decompression time: " + (decompressionTime / 1000) + " seconds");
            }
        }
    }
}
