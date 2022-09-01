package com.github.SunnySt4r.HelperForConferencesBot.service.test;

import org.springframework.stereotype.Component;

@Component
public class Test {
    Question[] questions;

    public Test(Question[] questions){
        this.questions = questions;
    }
}

