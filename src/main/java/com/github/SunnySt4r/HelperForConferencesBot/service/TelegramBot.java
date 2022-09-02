package com.github.SunnySt4r.HelperForConferencesBot.service;

import com.github.SunnySt4r.HelperForConferencesBot.config.BotConfig;
import com.github.SunnySt4r.HelperForConferencesBot.model.User;
import com.github.SunnySt4r.HelperForConferencesBot.model.UserRepository;
import com.github.SunnySt4r.HelperForConferencesBot.service.test.Test;
import com.github.SunnySt4r.HelperForConferencesBot.service.test.TestInit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.sql.Timestamp;
import java.util.Map;

@Component
@Slf4j
public class TelegramBot extends TelegramLongPollingBot {

    @Autowired
    private UserRepository userRepository;
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
                    registerUser(update.getMessage());
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

    private void registerUser(Message message) {
        if(userRepository.findById(message.getChatId()).isEmpty()){
            var chatId = message.getChatId();
            var chat = message.getChat();

            User user = new User();
            user.setChatId(chatId);
            user.setFirstName(chat.getFirstName());
            user.setLastName(chat.getLastName());
            user.setUserName(chat.getUserName());
            user.setRegisteredAt(new Timestamp(System.currentTimeMillis()));

            userRepository.save(user);
            log.info("user saved: " + user);
        }
    }

    private void startCommandReceived(long chatId, String name){
        String answer = "Hi, @" + name + ", nice to meet you!";
        log.info("Replied to user @" + name);
        sendMessage(chatId, answer);
    }

    private void sendMessage(long chatId, String textToSend) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText(textToSend);
        sendMessage.setChatId(String.valueOf(chatId));
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            log.error("Error occurred: " + e.getMessage());
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
}
