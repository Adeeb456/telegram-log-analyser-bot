package bot.log_analyser.telegram.bot.services;

import bot.log_analyser.telegram.bot.models.LogEntry;
import bot.log_analyser.telegram.bot.repository.LogRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LogService {

    @Autowired
    private LogRepository logRepository;

    public String getHistory(String chatId) throws JsonProcessingException {
        List<LogEntry> logs = logRepository.findTop10ByChatIdOrderByTimestampDesc(chatId);

        System.out.println(logs);

        if (logs.isEmpty()) {
            return "📭 No history found. Paste your logs to get started!";
        }

        StringBuilder sb = new StringBuilder("📜 Your Last 10 Logs:\n\n");

        int count = 1;
        for (LogEntry log : logs) {

            String response = log.getAiResponse();
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode root = objectMapper.readTree(response);
            String title = root.path("title").asText();
            String fix = root.path("fix").asText();

            sb.append(count).append(". ").append(title).append("\n");
            sb.append(fix).append("\n");
            sb.append("─────────────────\n");
            count++;
        }

        return sb.toString();
    }

    public void save(String chatId, String rawLog, String aiResponse) throws JsonProcessingException {

        LogEntry logEntry = new LogEntry();

        logEntry.setChatId(chatId);
        logEntry.setRawLog(rawLog);

        //AI response need to be formatted

        logEntry.setAiResponse(aiResponse);

        String summary = rawLog.split("\n")[0];
        logEntry.setSummary(summary);

        logEntry.setTimestamp(System.currentTimeMillis());

        System.out.println(logEntry);

        logRepository.save(logEntry);
    }
}
