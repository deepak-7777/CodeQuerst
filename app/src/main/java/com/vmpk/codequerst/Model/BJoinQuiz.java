package com.vmpk.codequerst.Model;

import java.io.Serializable;
import java.util.ArrayList;

public class BJoinQuiz implements Serializable {
    public String question;
    public ArrayList<String> options;
    public int correctIndex;
    public int selectedIndex;

    public BJoinQuiz(String question, ArrayList<String> options, int correctIndex, int selectedIndex) {
        this.question = question;
        this.options = options;
        this.correctIndex = correctIndex;
        this.selectedIndex = selectedIndex;
    }
}
