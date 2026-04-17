package bot.log_analyser.telegram.bot.config;

import bot.log_analyser.telegram.bot.bots.DebuggerBot;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Configuration
public class BotConfig {

    @Bean
    public TelegramBotsApi telegramBotsApi(DebuggerBot bot) throws Exception {
        TelegramBotsApi api = new TelegramBotsApi(DefaultBotSession.class);
        api.registerBot(bot);

//        // Initially only /start
//        SetMyCommands initialCommands = new SetMyCommands(
//                Collections.singletonList(new BotCommand("start", "Start the bot")),
//                new BotCommandScopeDefault(),
//                null
//        );
//        bot.execute(initialCommands);
        return api;
    }
}