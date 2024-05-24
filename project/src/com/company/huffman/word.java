package com.company.huffman;

public class word {
    private String word;
    private int freq;
    private word left;
    private word right;

    public word() {
        this.word = null;
        this.freq = 0;
        this.left = null;
        this.right = null;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public int getFreq() {
        return freq;
    }

    public void setFreq(int freq) {
        this.freq = freq;
    }

    public word getLeft() {
        return left;
    }

    public void setLeft(word left) {
        this.left = left;
    }

    public word getRight() {
        return right;
    }

    public void setRight(word right) {
        this.right = right;
    }

}
