package com.company.huffman;

import java.util.Comparator;

public class wordComparator implements Comparator<word> {
    public int compare(word word1, word word2) {
        if (word1.getFreq() > word2.getFreq())
            return 1;
        else if (word1.getFreq() < word2.getFreq())
            return -1;
        return 0;
    }
}
