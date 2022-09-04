package com.github.SunnySt4r.HelperForConferencesBot.service.test;

import java.util.HashMap;


public class Question {
    String stringQuestion;

    //answers, ifRight
    HashMap<String, Boolean> answers;

    public String getStringQuestion() {
        return stringQuestion;
    }

    public HashMap<String, Boolean> getAnswers() {
        return answers;
    }

    //open stringQuestion
    public Question(String stringQuestion){
        this.stringQuestion = stringQuestion;
    }

    //stringQuestion
    public Question(String stringQuestion, HashMap<String, Boolean> answers){
        this(stringQuestion);
        this.answers = answers;
    }
}
