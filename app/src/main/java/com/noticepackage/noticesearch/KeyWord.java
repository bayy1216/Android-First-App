package com.noticepackage.noticesearch;

import java.io.Serializable;

public class KeyWord implements Serializable {
    String word;

    public KeyWord(String word){
        this.word=word;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }
}
