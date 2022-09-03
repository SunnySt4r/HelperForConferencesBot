package com.github.SunnySt4r.HelperForConferencesBot.service.test;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;

@Component
public class TestInit {

    public static HashMap<Test, Boolean> tests;

    ArrayList<String[][]> testsArray = new ArrayList<>();

    public TestInit(){
        //Test 1
        String[][] test1Arr = {
                {"Что это такое?", "Кто это такой?"},
                {"земля планета вода воздух", "ты я он она"},
                {"вода", "я"}
        };
        testsArray.add(test1Arr);
        //Test 2
        String[][] test2Arr = {
                {"Мда?"},
                {"да нет возможно точно"},
                {"точно"}
        };
        testsArray.add(test2Arr);
        tests = new HashMap<>();
        for (String[][] strings : testsArray) {
            Question[] questions = new Question[strings[0].length];
            for (int j = 0; j < strings[0].length; j++) {
                HashMap<String, Boolean> answers = new HashMap<>();
                String[] arrAnswers = strings[1][j].split("\\s");
                for (String answ : arrAnswers) {
                    answers.put(answ, answ.equals(strings[2][j]));
                }
                questions[j] = new Question(strings[0][j], answers);
            }
            tests.put(new Test(questions), false);
        }
    }
}
