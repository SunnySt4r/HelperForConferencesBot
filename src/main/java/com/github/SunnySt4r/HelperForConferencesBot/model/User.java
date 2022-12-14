package com.github.SunnySt4r.HelperForConferencesBot.model;

import com.github.SunnySt4r.HelperForConferencesBot.service.test.Test;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.sql.Timestamp;

@Entity(name = "usersDataTable")
public class User {

    @Id
    private Long chatId;

    private String firstName;
    private String lastName;
    private String userName;
    private Timestamp registeredAt;
    private int currentTest;
    private int currentQuestion;
    private int pointsTest1;
    private int pointsTest2;
    private int pointsTest3;
    private int pointsTest4;
    private int pointsTest5;

    public int getCurrentTest() {
        return currentTest;
    }

    public void setCurrentTest(int currentTest) {
        this.currentTest = currentTest;
    }

    public int getCurrentQuestion() {
        return currentQuestion;
    }

    public void setCurrentQuestion(int currentQuestion) {
        this.currentQuestion = currentQuestion;
    }

    public Integer getPointsTest1() {
        return pointsTest1;
    }

    public void setPointsTest1(Integer pointsTest1) {
        this.pointsTest1 = pointsTest1;
    }

    public Integer getPointsTest2() {
        return pointsTest2;
    }

    public void setPointsTest2(Integer pointsTest2) {
        this.pointsTest2 = pointsTest2;
    }

    public Integer getPointsTest3() {
        return pointsTest3;
    }

    public void setPointsTest3(Integer pointsTest3) {
        this.pointsTest3 = pointsTest3;
    }

    public Integer getPointsTest4() {
        return pointsTest4;
    }

    public void setPointsTest4(Integer pointsTest4) {
        this.pointsTest4 = pointsTest4;
    }

    public Integer getPointsTest5() {
        return pointsTest5;
    }

    public void setPointsTest5(Integer pointsTest5) {
        this.pointsTest5 = pointsTest5;
    }

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Timestamp getRegisteredAt() {
        return registeredAt;
    }

    public void setRegisteredAt(Timestamp registeredAt) {
        this.registeredAt = registeredAt;
    }

    @Override
    public String toString() {
        return "User{" +
                "chatId=" + chatId +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", userName='" + userName + '\'' +
                ", registeredAt=" + registeredAt +
                '}';
    }
}
