package com.github.SunnySt4r.HelperForConferencesBot.service.test;

public class Test {

    Question[] questions;

    public Test(Question[] questions){
        this.questions = questions;
    }

    public Question getQuestion(int id) {
        return questions[id - 1];
    }
}

