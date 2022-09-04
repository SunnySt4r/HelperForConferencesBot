package com.github.SunnySt4r.HelperForConferencesBot.service.test;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Component
public class TestInit {

    public static HashMap<Test, Boolean> tests;

    ArrayList<String[][]> testsArray = new ArrayList<>();

    public TestInit(){
        //Test 1
        String[][] test1Arr = {
                {"1. 1+1", "1. 2+2", "1. 3+3", "1. 4+4", "1. 5+5"},
                {"1 2 3 4", "2 4 6 8", "1 2 6 4", "8 1 2 3", "1 10 2 3"},
                {"2", "4", "6", "8", "10"}
        };
        testsArray.add(test1Arr);
        //Test 2
        String[][] test2Arr = {
                {"2. 1+1", "2. 2+2", "2. 3+3", "2. 4+4", "2. 5+5"},
                {"1 2 3 4", "2 4 6 8", "1 2 6 4", "8 1 2 3", "1 10 2 3"},
                {"2", "4", "6", "8", "10"}
        };
        testsArray.add(test2Arr);
        //Test 3
        String[][] test3Arr = {
                {"3. 1+1", "3. 2+2", "3. 3+3", "3. 4+4", "3. 5+5"},
                {"1 2 3 4", "2 4 6 8", "1 2 6 4", "8 1 2 3", "1 10 2 3"},
                {"2", "4", "6", "8", "10"}
        };
        testsArray.add(test3Arr);

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

    public Test getTest(int id){
        int count = 1;
        for(Map.Entry<Test, Boolean> entry : tests.entrySet()){
            if(count == id && entry.getValue()){
                return entry.getKey();
            }
            count++;
        }
        return null;
    }
}
