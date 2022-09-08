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
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
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
                //clear points of tests in database
                if(messageText.equals("/clear")){
                    admin = true;
                    clearAllPoints();
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
                case "/test1":
                case "/test2":
                case "/test3":
                case "/test4":
                case "/test5":
                    System.out.println(messageText);
                    int indexTest = Integer.parseInt(String.valueOf(messageText.charAt(5)));
                    if(userRepository.findById(update.getMessage().getChatId()).isEmpty()){
                        sendMessage(update.getMessage().getChatId(), "Извините, но вы не прописывали команду /start");
                    }else {
                        Test currentTest = testInit.getTest(indexTest);
                        if(currentTest != null){
                            User user = userRepository.findById(update.getMessage().getChatId()).get();
                            if(user.getCurrentQuestion() + user.getCurrentTest() == 0){
                                user.setCurrentTest(indexTest);
                                user.setCurrentQuestion(1);
                                userRepository.save(user);
                                sendQuestion(user, null);
                            }else{
                                sendMessage(chatId, "Вы уже запустили прохождение одного теста.");
                            }
                        }else {
                            sendMessage(chatId, "Извините, но данный тест ещё не доступен." +
                                    " Подождите пока выйдет лекция на эту тему.");
                        }
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
        } else if (update.hasCallbackQuery()) {
            String callbackData = update.getCallbackQuery().getData();
            long messageId = update.getCallbackQuery().getMessage().getMessageId();
            long chatId = update.getCallbackQuery().getMessage().getChatId();
            if(userRepository.findById(chatId).isEmpty()){
                sendMessage(chatId, "Извините, но вы не прописывали команду /start");
            }else{
                User user = userRepository.findById(chatId).get();
                if(callbackData.equals("true")){
                    addPoint(user);
                }
                int currentQuestion = user.getCurrentQuestion() + 1;
                if(currentQuestion == 6){
                    EditMessageText editMessage = new EditMessageText();
                    editMessage.setText("Вы набрали за этот тест " + getPoints(user) + " баллов.");
                    editMessage.setChatId(user.getChatId());
                    editMessage.setMessageId(Integer.parseInt(String.valueOf(messageId)));
                    user.setCurrentQuestion(0);
                    user.setCurrentTest(0);
                    userRepository.save(user);
                    try {
                        execute(editMessage);
                    } catch (TelegramApiException e) {
                        log.error("Error occurred: " + e.getMessage());
                    }
                }else{
                    user.setCurrentQuestion(currentQuestion);
                    userRepository.save(user);
                    sendQuestion(user, messageId);
                }
            }
        }
    }

    private void clearAllPoints() {
        for(User user : userRepository.findAll()){
            user.setPointsTest1(0);
            user.setPointsTest2(0);
            user.setPointsTest3(0);
            user.setPointsTest4(0);
            user.setPointsTest5(0);
            user.setCurrentTest(0);
            user.setCurrentQuestion(0);
            userRepository.save(user);
        }
    }

    private void addPoint(User user) {
        int currentTest = user.getCurrentTest();
        switch (currentTest){
            case 1:
                if(user.getPointsTest1()<5){
                    user.setPointsTest1(user.getPointsTest1() + 1);
                }
                break;
            case 2:
                if(user.getPointsTest2()<5){
                    user.setPointsTest2(user.getPointsTest2() + 1);
                }
                break;
            case 3:
                if(user.getPointsTest3()<5){
                    user.setPointsTest3(user.getPointsTest3() + 1);
                }
                break;
            case 4:
                if(user.getPointsTest4()<5){
                    user.setPointsTest4(user.getPointsTest4() + 1);
                }
                break;
            case 5:
                if(user.getPointsTest5()<5){
                    user.setPointsTest5(user.getPointsTest5() + 1);
                }
                break;
        }
    }

    private int getPoints(User user){
        int currentTest = user.getCurrentTest();
        return switch (currentTest) {
            case 1 -> user.getPointsTest1();
            case 2 -> user.getPointsTest2();
            case 3 -> user.getPointsTest3();
            case 4 -> user.getPointsTest4();
            case 5 -> user.getPointsTest5();
            default -> -1;
        };
    }
    private void setPoints(User user, int points){
        int currentTest = user.getCurrentTest();
        switch (currentTest) {
            case 1 -> user.setPointsTest1(points);
            case 2 -> user.setPointsTest2(points);
            case 3 -> user.setPointsTest3(points);
            case 4 -> user.setPointsTest4(points);
            case 5 -> user.setPointsTest5(points);
        }
    }

    private void sendQuestion(User user, Long messageId) {
        Question question = testInit.getTest(user.getCurrentTest()).getQuestion(user.getCurrentQuestion());
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        List<InlineKeyboardButton> row1 = new ArrayList<>();
        List<InlineKeyboardButton> row2 = new ArrayList<>();
        int count = 1;
        for(Map.Entry<String, Boolean> entry : question.getAnswers().entrySet()){
            var button = new InlineKeyboardButton();
            button.setText(entry.getKey());
            button.setCallbackData((entry.getValue())? "true" : "false");
            if(count<=2){
                row1.add(button);
            }else{
                row2.add(button);
            }
            count++;
        }
        rowsInline.add(row1);
        rowsInline.add(row2);
        inlineKeyboardMarkup.setKeyboard(rowsInline);
        if(messageId==null){
            setPoints(user, 0);
            userRepository.save(user);
            SendMessage sendMessage = new SendMessage();
            sendMessage.setText(question.getStringQuestion());
            sendMessage.setChatId(user.getChatId());
            sendMessage.setReplyMarkup(inlineKeyboardMarkup);
            try {
                execute(sendMessage);
            } catch (TelegramApiException e) {
                log.error("Error occurred: " + e.getMessage());
            }
        }else{
            EditMessageText editMessage = new EditMessageText();
            editMessage.setText(question.getStringQuestion());
            editMessage.setChatId(user.getChatId());
            editMessage.setMessageId(Integer.parseInt(String.valueOf(messageId)));
            editMessage.setReplyMarkup(inlineKeyboardMarkup);
            try {
                execute(editMessage);
            } catch (TelegramApiException e) {
                log.error("Error occurred: " + e.getMessage());
            }
        }
    }

    private void sendTop(long chatId) {
        //todo top
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

    //todo delete/help /setting
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
        for(int i=0; i<testInit.getTests().size(); i++){
            if(!testInit.getTests().get(i).isUnlock()){
                testInit.getTests().get(i).setUnlock(true);
                break;
            }
        }
        log.info("tests: " + testInit.getTests());
    }

    private void checkTests(long chatId){
        StringBuilder textToSend = new StringBuilder();
        boolean atLeastOne = false;
        for(Test test : testInit.getTests()){
            if(test.isUnlock()){
                textToSend.append("\n\nВам доступен тест: /test").append(testInit.getTests().indexOf(test) + 1);
                atLeastOne = true;
            }
        }
        if(!atLeastOne){
            textToSend.append("Вам пока не доступны тесты. Они будут доступны после лекций.");
        }
        sendMessage(chatId, textToSend.toString());
    }
}