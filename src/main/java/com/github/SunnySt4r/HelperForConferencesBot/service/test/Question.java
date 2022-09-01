package com.github.SunnySt4r.HelperForConferencesBot.service.test;

import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
public class Question {
    String question;

    //answers, ifRight
    HashMap<String, Boolean> answers;

    //open question
    public Question(String question){
        this.question = question;
    }

    //question
    public Question(String question,  HashMap<String, Boolean> answers){
        this(question);
        this.answers = answers;
    }
}
