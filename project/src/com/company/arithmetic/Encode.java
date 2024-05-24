package com.company.arithmetic;

import java.io.*;

public class Encode extends Compress {
    static final int MAX_VALUE = 65535;
    static final int HALF = MAX_VALUE / 2 + 1;
    static final int FIRST_QTR = HALF / 2;
    static final int THIRD_QTR = FIRST_QTR * 3;

    int buffer;
    int bits_in_buf;
    int low, high, opposite_bits;
    FileInputStream in;
    FileOutputStream out;

    public Encode() {
        buffer = 0;
        bits_in_buf = 0;
        low = 0;
        high = MAX_VALUE;
        opposite_bits = 0;
    }

    public void encode(String infile, String outfile) throws IOException {
        in = new FileInputStream(infile);
        out = new FileOutputStream(outfile);
        while (true) {
            int ch = in.read();
            if (ch == -1) {
                System.out.println("Encoding is done");
                break;
            }
            int symbol = char_to_index[ch];
            encode_symbol(symbol);
            update_tables(symbol);
        }
        encode_symbol(NO_OF_SYMBOLS);
        end_encoding();
        in.close();
        out.close();
    }

    void encode_symbol(int symbol) throws IOException {
        int range = high - low;
        high = low + (range * cum_freq[symbol - 1]) / cum_freq[0];
        low = low + (range * cum_freq[symbol]) / cum_freq[0];
        while (true) {
            if (high < HALF)
                output_bits(0);
            else if (low >= HALF) {
                output_bits(1);
                low -= HALF;
                high -= HALF;
            } else if (low >= FIRST_QTR && high < THIRD_QTR) {
                opposite_bits++;
                low -= FIRST_QTR;
                high -= FIRST_QTR;
            } else
                break;
            low *= 2;
            high *= 2;
        }
    }

    void end_encoding() throws IOException {
        opposite_bits++;
        if (low < FIRST_QTR)
            output_bits(0);
        else
            output_bits(1);
        out.write(buffer >> bits_in_buf);
    }

    void output_bits(int bit) throws IOException {
        write_bit(bit);
        while (opposite_bits > 0) {
            write_bit(1 - bit);
            opposite_bits--;
        }
    }

    void write_bit(int bit) throws IOException {
        buffer >>= 1;
        if (bit != 0) buffer |= 0x80;
        bits_in_buf++;
        if (bits_in_buf == 8) {
            out.write(buffer);
            bits_in_buf = 0;
        }
    }
}