package com.company.arithmetic;

import java.io.*;

public class Decode extends Compress {
    static final int MAX_VALUE = 65535;
    static final int HALF = MAX_VALUE / 2 + 1;
    static final int FIRST_QTR = HALF / 2;
    static final int THIRD_QTR = FIRST_QTR * 3;

    int buffer;
    int bits_in_buf;
    boolean end_decoding;
    int low, high, value;
    FileInputStream in;
    FileOutputStream out;

    public Decode() {
        buffer = 0;
        bits_in_buf = 0;
        end_decoding = false;
        low = 0;
        high = MAX_VALUE;
    }

    void load_first_value() throws IOException {
        value = 0;
        for (int i = 1; i <= 16; i++)
            value = 2 * value + get_bit();
    }

    public void decode(String infile, String outfile) throws IOException {
        in = new FileInputStream(infile);
        out = new FileOutputStream(outfile);
        load_first_value();
        while (true) {
            int sym_index = decode_symbol();
            if (sym_index == NO_OF_SYMBOLS || end_decoding)
                break;
            int ch = index_to_char[sym_index];
            out.write(ch);
            update_tables(sym_index);
        }
        System.out.println("Decoding is done");
        in.close();
        out.close();
    }

    int decode_symbol() throws IOException {
        int range = high - low;
        int cum = ((value - low + 1) * cum_freq[0] - 1) / range;
        int symbol_index;
        for (symbol_index = 1; cum_freq[symbol_index] > cum; symbol_index++);
        high = low + (range * cum_freq[symbol_index - 1]) / cum_freq[0];
        low = low + (range * cum_freq[symbol_index]) / cum_freq[0];
        while (true) {
            if (high < HALF) {
            } else if (low >= HALF) {
                value -= HALF;
                low -= HALF;
                high -= HALF;
            } else if (low >= FIRST_QTR && high < THIRD_QTR) {
                value -= FIRST_QTR;
                low -= FIRST_QTR;
                high -= FIRST_QTR;
            } else
                break;
            low *= 2;
            high *= 2;
            value = 2 * value + get_bit();
        }
        return symbol_index;
    }

    int get_bit() throws IOException {
        if (bits_in_buf == 0) {
            buffer = in.read();
            if (buffer == -1) {
                end_decoding = true;
                return -1;
            }
            bits_in_buf = 8;
        }
        int t = buffer & 1;
        buffer >>= 1;
        bits_in_buf--;
        return t;
    }
}