package com.github.SunnySt4r.HelperForConferencesBot.service.test;

public class Test {

    Question[] questions;
    private boolean unlock;

    public boolean isUnlock() {
        return unlock;
    }

    public void setUnlock(boolean unlock) {
        this.unlock = unlock;
    }

    public Test(Question[] questions){
        this.questions = questions;
        unlock = false;
    }

    public Question getQuestion(int id) {
        return questions[id - 1];
    }
}

