package bot.log_analyser.telegram.bot;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class TelegramBotApplication {

	// NOT static
	@Value("${TELEGRAM_BOT_USERNAME}")
	private String telegramBotUsername;

	public static void main(String[] args) {
		SpringApplication.run(TelegramBotApplication.class, args);
	}

	// This runs AFTER Spring has injected all values
	@PostConstruct
	public void init() {
		System.out.println("TELEGRAM_BOT_USERNAME = " + telegramBotUsername);
	}

}
