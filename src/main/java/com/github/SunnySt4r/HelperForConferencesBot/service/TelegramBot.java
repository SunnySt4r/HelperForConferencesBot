package com.github.SunnySt4r.HelperForConferencesBot.service;

import com.github.SunnySt4r.HelperForConferencesBot.config.BotConfig;
import com.github.SunnySt4r.HelperForConferencesBot.service.test.Test;
import com.github.SunnySt4r.HelperForConferencesBot.service.test.TestInit;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Map;

@Component
public class TelegramBot extends TelegramLongPollingBot {

    final BotConfig botConfig;
    TestInit testInit = new TestInit();

    public TelegramBot(BotConfig botConfig){
        this.botConfig = botConfig;
    }

    @Override
    public String getBotUsername() {
        return botConfig.getBotName();
    }

    @Override
    public String getBotToken() {
        return botConfig.getToken();
    }

    @Override
    public void onUpdateReceived(Update update) {
        if(update.hasMessage() && update.getMessage().hasText()){
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();

            switch (messageText){
                case "/start":
                    addToListOfChatIds(chatId);
                    startCommandReceived(chatId, update.getMessage().getChat().getUserName());
                    break;
                case "/test":
                    checkTests(chatId);
                    break;
                case "/test1":
                    System.out.println("/test1");
                    break;
                case "/test2":
                    System.out.println("/test2");
                    break;
                default:
                    sendMessage(chatId, "Sorry");
                    break;
            }
        }
    }

    private void startCommandReceived(long chatId, String name){
        String answer = "Hi, @" + name + ", nice to meet you!";

        sendMessage(chatId, answer);
    }

    private void sendMessage(long chatId, String textToSend) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText(textToSend);
        sendMessage.setChatId(String.valueOf(chatId));
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            //todo logs
        }
    }

    private void checkTests(long chatId){
        StringBuilder textToSend = new StringBuilder();
        boolean atLeastOne = false;
        int count = 1;
        for(Map.Entry<Test, Boolean> entry: testInit.getTests().entrySet()){
            if(entry.getValue()){
                textToSend.append("\n\nВам доступен тест: /test").append(count);
                atLeastOne = true;
            }
            count++;
        }
        if(!atLeastOne){
            textToSend.append("Вам пока не доступны тесты. Они будут доступны после лекций.");
        }
        sendMessage(chatId, textToSend.toString());
    }

    private void addToListOfChatIds(long chatId){
        //todo add chatid into file and parse them into arraylist
    }
}
