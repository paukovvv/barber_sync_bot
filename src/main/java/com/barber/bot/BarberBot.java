package com.barber.bot;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

public class BarberBot extends TelegramLongPollingBot {
    private final String botToken;
    private final String botUsername;

    public BarberBot() {
        this.botToken = System.getenv("BOT_TOKEN");
        this.botUsername = System.getenv("BOT_USERNAME");
    }


    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String userText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();
            System.out.println("Message from user: " + userText + " (Chat ID: " + chatId + ")");


        }
    }

    @Override
    public String getBotUsername() {
        return this.botUsername;
    }

    @Override
    public String getBotToken() {
        return this.botToken;
    }
}
