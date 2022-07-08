package com.noticepackage.noticesearch;

import java.io.Serializable;

public class KeyWord implements Serializable {
    String word;
    String date;

    public KeyWord(String word,String date){
        this.word=word;
        this.date=date;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word,String date) {
        this.word = word;
        this.date=date;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
