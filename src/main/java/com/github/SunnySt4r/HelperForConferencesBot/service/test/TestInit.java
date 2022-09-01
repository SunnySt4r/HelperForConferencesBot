package com.github.SunnySt4r.HelperForConferencesBot.service.test;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;

@Component
public class TestInit {
    HashMap<Test, Boolean> tests;

    //Test 1
    String[][] test1Arr = {
            {"Что это такое?", "Кто это такой?"},
            {"земля планета вода воздух", "ты я он она"},
            {"вода", "я"}
    };
    //Test 2
    String[][] test2Arr = {
            {"Мда?"},
            {"да нет возможно точно"},
            {"точно"}
    };

    ArrayList<String[][]> testsArray = new ArrayList<>();


    public TestInit(){
        testsArray.add(test1Arr);
        testsArray.add(test2Arr);
        tests = new HashMap<>();
        for(int i=0; i<testsArray.size(); i++){
            Question[] questions = new Question[testsArray.get(i)[0].length];
            for(int j=0; j<testsArray.get(i)[0].length; j++){
                HashMap<String, Boolean> answers = new HashMap<>();
//                String[] arrAnswers = testsArray.get(i)[j].split("\\s");
//                for()
            }
            tests.put(new Test(questions), false);
        }
    }
}
