package com.barber.bot;

import com.barber.model.Client;
import com.barber.service.VisionExtractorService;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;

public class BarberBot extends TelegramLongPollingBot {
    private final String botToken;
    private final String botUsername;
    private final long allowedChatId;
    private final VisionExtractorService visionService;

    public BarberBot() {
        this.botToken = System.getenv("BOT_TOKEN");
        this.botUsername = System.getenv("BOT_USERNAME");
        String adminIdStr = System.getenv("ALLOWED_CHAT_ID");
        if (adminIdStr != null && !adminIdStr.isBlank()) {
            this.allowedChatId = Long.parseLong(adminIdStr);
        } else {
            this.allowedChatId = 0L;
            System.err.println("WARNING: ALLOWED_CHAT_ID is not configured!");
        }
        this.visionService = new VisionExtractorService();
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (!update.hasMessage()) {
            return;
        }

        Message message = update.getMessage();
        long chatId = message.getChatId();
        if (chatId != this.allowedChatId) {
            return;
        }

        if (message.hasText()) {
            String userText = message.getText();
            System.out.println("Message from user: " + userText + " (Chat ID: " + chatId + ")");
        } else if (message.hasPhoto()) {
            List<PhotoSize> photos = message.getPhoto();
            PhotoSize bestPhoto = photos.stream()
                    .max(Comparator.comparingInt(p -> p.getWidth() * p.getHeight()))
                    .orElse(null);
            if (bestPhoto == null) {
                return;
            }

            try {
                org.telegram.telegrambots.meta.api.objects.File file = execute(new GetFile(bestPhoto.getFileId()));
                java.io.File downloadedFile = downloadFile(file);
                System.out.println("Downloaded photo to: " + downloadedFile.getAbsolutePath());

                Client client = visionService.extractClientFromImage(downloadedFile);
                downloadedFile.delete();

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
                String formattedTime = client.getAppointmentTime() != null
                        ? client.getAppointmentTime().format(formatter)
                        : "Не вказано";

                String replyText = String.format(
                        "✅ Розпізнано запис:\n" +
                                "👤 Ім'я: %s\n" +
                                "📞 Телефон: %s\n" +
                                "📧 Email: %s\n" +
                                "🕒 Час: %s",
                        client.getName(),
                        client.getPhone().isEmpty() ? "—" : client.getPhone(),
                        client.getEmail().isEmpty() ? "—" : client.getEmail(),
                        formattedTime
                );

                SendMessage reply = new SendMessage();
                reply.setChatId(String.valueOf(chatId));
                reply.setText(replyText);
                execute(reply);

            } catch (Exception e) {
                e.printStackTrace();
            }
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