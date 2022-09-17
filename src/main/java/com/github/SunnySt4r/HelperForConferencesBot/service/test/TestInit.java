package com.github.SunnySt4r.HelperForConferencesBot.service.test;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;

@Component
public class TestInit {
    ArrayList<Test> tests;

    ArrayList<String[][]> testsArray = new ArrayList<>();

    public TestInit(){
        //Test 1
        String[][] test1Arr = {
                {       "1) В какой из сфер существуют успешные проекты на стыке указанной отрасли и экологии?\n" +
                                "а) высокотехнологичная сфера\n" +
                                "б) реклама\n" +
                                "в) энергетика\n" +
                                "г) все перечисленные",
                        "2) Какая активность НЕ относятся к эковолонтерству?\n" +
                                "а) участие в субботнике\n" +
                                "б) организация буккроссинга и свопа растений\n" +
                                "в) организация фестиваля электромобилей\n" +
                                "г) проведение лекции по принципам устойчивого развития",
                        "3) Какая проблема на данный момент успешно решается российскими компаниями и активистами (из указанных в лекции)?\n" +
                                "а) перепотребление естественного (животного) мяса\n" +
                                "б) невозможность переработки одноразовых стаканчиков\n" +
                                "в) загрязнение мирового океана\n" +
                                "г) высокий экологический след крупных корпораций"},
                {"а б в г", "а б в г", "а б в г"},
                {"г", "в", "а"}
        };
        testsArray.add(test1Arr);
        //Test 2
        String[][] test2Arr = {
                {       "1) Если человечество сохранит текущий уровень потребления, сколько планет Земля потребуется нам в 2030 году для комфортного существования?\n" +
                                "а) 1\n" +
                                "б) 2\n" +
                                "в) 3,5\n" +
                                "г) 5",
                        "2) Что произошло в сфере утилизации отходов за последние 10 лет?\n" +
                                "а) Процент перерабатываемых отходов снизился до 1%\n" +
                                "б) Количество свалок увеличилось вдвое\n" +
                                "в) Процент рассортированных отходов увеличился до 80% \n" +
                                "г) Количество свалок незначительно снизилось",
                        "3) Какой негативный эффект оказывает производство пластика на природу?\n" +
                                "а) Значительный парниковый эффект\n" +
                                "б) Загрязнение воздуха, воды и почвы\n" +
                                "в) Угроза жизни многим видам животных из-за распространение частиц микропластика\n" +
                                "г) Все перечисленное"},
                {"а б в г", "а б в г", "а б в г"},
                {"б", "б", "г"}
        };
        testsArray.add(test2Arr);

        tests = new ArrayList<>();
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
            tests.add(new Test(questions));
        }
    }

    public Test getTest(int id){
        if(tests.get(id - 1).isUnlock()){
            return tests.get(id - 1);
        }
        return null;
    }
    public ArrayList<Test> getTests() {
        return tests;
    }
}
