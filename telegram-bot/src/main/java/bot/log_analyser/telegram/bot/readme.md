# Telegram Log Analyser Bot

An AI-powered Telegram bot that analyses application logs, detects root causes, suggests fixes, and maintains a history of analysed logs in real time via Telegram.

---

## Demo

Paste your logs in Telegram and get instant AI-powered root cause analysis and fix suggestions.
**https://t.me/log_analyser_bot**
---

## Features

- AI-powered log analysis using OpenRouter API
- Root cause detection from stack traces and error logs
- Fix suggestions with severity rating (LOW / MEDIUM / HIGH / CRITICAL)
- Log history — view your last 10 analysed logs via /history command
- Persistent storage in MongoDB Atlas
- Non-technical query filtering — bot stays focused on technical issues only
- Graceful error handling for rate limits and API failures

---

## Tech Stack

| Technology         | Usage                        |
|--------------------|------------------------------|
| Java               | Core language                |
| Spring Boot 3.2.5  | Application framework        |
| Telegram Bot API   | Bot interface                |
| OpenRouter AI API  | Log analysis AI model        |
| MongoDB Atlas      | Cloud database               |
| WebFlux WebClient  | Non-blocking HTTP client     |
| Jackson            | JSON parsing                 |

---

## Commands

| Command         | Description                          |
|-----------------|--------------------------------------|
| /start          | Welcome message and usage guide      |
| /history        | View your last 10 analysed logs      |
| paste logs      | Analyse any log or stack trace       |

---

## Getting Started

### Prerequisites
- Java 17 or higher
- Maven
- Telegram Bot Token from @BotFather
- OpenRouter API Key from openrouter.ai
- MongoDB Atlas URI from mongodb.com/atlas

### Installation

1. Clone the repository

git clone https://github.com/Adeeb456/telegram-log-analyser-bot.git
cd telegram-log-analyser-bot

2. Create application-local.properties in src/main/resources/

TELEGRAM_BOT_USERNAME=your_bot_username
TELEGRAM_BOT_TOKEN=your_telegram_bot_token
OPENROUTER_API_KEY=your_openrouter_api_key
MONGODB_URI=your_mongodb_atlas_uri

3. Activate local profile in application.properties

spring.profiles.active=local

4. Run the application

mvn spring-boot:run

---

## Environment Variables

| Variable                | Description                              |
|-------------------------|------------------------------------------|
| TELEGRAM_BOT_USERNAME   | Your Telegram bot username               |
| TELEGRAM_BOT_TOKEN      | Your Telegram bot token from BotFather   |
| OPENROUTER_API_KEY      | Your OpenRouter API key                  |
| MONGODB_URI             | Your MongoDB Atlas connection string     |

---

## AI Response Format

The bot returns a structured analysis for every log submitted:

Title: NullPointerException in UserService

Severity: HIGH

Root Cause:
Object 'user' is null when getRole() is called at line 78,
likely due to failed dependency injection.

Fix:
Add a null check before accessing user fields.
Use Optional<User> to handle empty cases gracefully.

---

## Contributing

Pull requests are welcome. For major changes, please open an issue first to discuss what you would like to change.

---

## Author

Adeeb
GitHub: https://github.com/Adeeb456
LinkedIn: https://www.linkedin.com/adeebdevv

---

If you found this project helpful, please consider giving it a star on GitHub.
