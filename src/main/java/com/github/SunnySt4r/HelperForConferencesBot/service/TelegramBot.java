package com.github.SunnySt4r.HelperForConferencesBot.service;

import com.github.SunnySt4r.HelperForConferencesBot.config.BotConfig;
import com.github.SunnySt4r.HelperForConferencesBot.model.User;
import com.github.SunnySt4r.HelperForConferencesBot.model.UserRepository;
import com.github.SunnySt4r.HelperForConferencesBot.service.test.Question;
import com.github.SunnySt4r.HelperForConferencesBot.service.test.Test;
import com.github.SunnySt4r.HelperForConferencesBot.service.test.TestInit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class TelegramBot extends TelegramLongPollingBot {

    @Autowired
    private UserRepository userRepository;
    final BotConfig botConfig;
    TestInit testInit = new TestInit();

    List<BotCommand> botCommandList = new ArrayList<>();

    public TelegramBot(BotConfig botConfig){
        this.botConfig = botConfig;
        botCommandList.add(new BotCommand("/start", "Старт бота"));
        botCommandList.add(new BotCommand("/tests", "Посмотреть доступные тесты"));
        createCommandsMenu();
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
            boolean admin = false;
            if(update.getMessage().getChatId() == 733063854){
                // sendToAll www.youtube...
                if(messageText.split("\\s")[0].equals("/sendToAllUsers")){
                    admin = true;
                    if(messageText.split("\\s")[1].equals("L")){
                        sendToAllUsers(messageText.split("\\s")[2]);
                        unlockTest();
                    }else{
                        sendToAllUsers(messageText.split("\\s")[1]);
                    }
                }
                //unlock tests if bot restart
                if(messageText.split("\\s")[0].equals("/unlockTests")){
                    admin = true;
                    int numberOfTests = Integer.parseInt(messageText.split("\\s")[1]);
                    for(int i=0; i<numberOfTests; i++){
                        unlockTest();
                    }
                }
            }
            switch (messageText){
                case "/start":
                    registerUser(update.getMessage());
                    startCommandReceived(chatId, update.getMessage().getChat().getUserName());
                    break;
                case "/tests":
                    checkTests(chatId);
                    break;
                case "/test\\[12345]":
                    System.out.println(messageText);
                    int indexTest = Integer.parseInt(String.valueOf(messageText.charAt(5)));
                    if(indexTest <= TestInit.tests.size()){
                        sendTestToUser(chatId, indexTest);
                    }else{
                        sendMessage(chatId, "Теста с таким номером не существует.");
                    }
                    break;
                case "/top":
                    sendTop(chatId);
                    break;
                default:
                    if(!admin){
                        sendMessage(chatId, "Извините, но данное сообщение не является командой данного бота." +
                                " Пожалуйста, посмотрите команды в меню бота или пропишите \"/\"");
                    }
                    break;
            }
        }
    }

    private void sendTop(long chatId) {
        //todo top
    }

    private void sendTestToUser(long chatId, int indexTest) {
        int count = 1;
        for(Map.Entry<Test, Boolean> entry: TestInit.tests.entrySet()){
            if(count == indexTest && entry.getValue()){
                for(Question question : entry.getKey().getQuestions()){
                    //todo tests
                }
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

    private void createCommandsMenu(){
        try {
            this.execute(new SetMyCommands(botCommandList, new BotCommandScopeDefault(), null));
        }catch (TelegramApiException e){
            log.error("Error: " + e.getMessage());
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

    public void sendToAllUsers(String textToSend){
        for(User user:userRepository.findAll()){
            sendMessage(user.getChatId(), textToSend);
        }
    }

    private void unlockTest(){
        boolean allUnlock = true;
        for(Map.Entry<Test, Boolean> entry: TestInit.tests.entrySet()){
            if(!entry.getValue()){
                allUnlock = false;
                TestInit.tests.put(entry.getKey(), true);
                break;
            }
        }
        log.info("AllUnlock: " + allUnlock);
        log.info("tests: " + TestInit.tests);
    }

    private void checkTests(long chatId){
        StringBuilder textToSend = new StringBuilder();
        boolean atLeastOne = false;
        int count = 1;
        for(Map.Entry<Test, Boolean> entry: TestInit.tests.entrySet()){
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
