package bot.log_analyser.telegram.bot.bots;

import bot.log_analyser.telegram.bot.rate_limiter.RateLimiter;
import bot.log_analyser.telegram.bot.services.LogService;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Arrays;
import java.util.List;

@Component
public class DebuggerBot extends TelegramLongPollingBot {

    @Autowired
    private CommandHandler commandHandler;

    @Autowired
    private LogService logService;

    @Autowired
    private RateLimiter rateLimiter;

    @Value("${telegram.bot.username}")
    private String username;

    @Value("${telegram.bot.token}")
    private String token;

    @Override
    public String getBotUsername() {
        return username;
    }

    @Override
    public String getBotToken() {
        return token;
    }

    @Override
    public void onUpdateReceived(Update update) {

        if (!update.hasMessage() || !update.getMessage().hasText()) return;

        String userMessage = update.getMessage().getText();
        String chatId = update.getMessage().getChatId().toString();

        SendMessage message = new SendMessage();
        message.setChatId(chatId);

        try {
            String response = handleMessage(userMessage, chatId);
            message.setText(response);
        } catch (Exception e) {
            e.printStackTrace();
            message.setText(handleException(e));
        }

        executeSafely(message);
    }

    // Core routing logic
    private String handleMessage(String userMessage, String chatId) throws JsonProcessingException {

        if (userMessage.equals("/start")) {
            return commandHandler.handleStart();
        }

        if (userMessage.equals("/help")) {
            return commandHandler.handleOther();
        }

        if (userMessage.equals("/history")) {
            return logService.getHistory(chatId);
        }

        if (userMessage.startsWith("/debug")) {

            if (!rateLimiter.isAllowed(chatId)) {
                return "⚠️ Too many requests. Try again after some time.";
            }

            return commandHandler.handleDebug(userMessage, chatId);
        }

        return "Unknown command. Try choosing from command menu! ";
    }

    // Centralized exception handling
    private String handleException(Exception e) {

        String msg = e.getMessage();

        if (msg != null && msg.contains("429")) {
            return "⚠️ AI is busy right now. Try again shortly.";
        }

        return "⚠️ Something went wrong. Please try again later.";
    }

    // Safe execute wrapper
    private void executeSafely(SendMessage message) {
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @PostConstruct
    public void init() {

        List<BotCommand> commands = Arrays.asList(
                new BotCommand("start", "Start the bot"),
                new BotCommand("debug", "Analyze logs"),
                new BotCommand("history", "Show history"),
                new BotCommand("help", "Get help")
        );

        try {
            execute(new SetMyCommands(commands, new BotCommandScopeDefault(), null));
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}