package bot.log_analyser.telegram.bot.bots;

import bot.log_analyser.telegram.bot.services.AIService;
import bot.log_analyser.telegram.bot.services.LogService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CommandHandler {
    @Autowired
    private AIService aiService;
    @Autowired
    private LogService logService;

    public String handleStart() {
        return "Welcome to AI Log Analyser Bot 🤖\nSend me any error log!";
    }

    public String handleOther() {
        return """
🤖 AI Log Analyzer Bot Help

Here’s how to use me:

/debug <your logs> - Analyze error logs  
/history - View your last logs  

📌 Example:
/debug NullPointerException at line 45

I’ll analyze it and suggest a fix 🚀
""";
    }
    public String handleDebug(String userMessage, String chatId) throws JsonProcessingException {
        String logs = userMessage.replace("/debug", "").trim();

        if (logs.isEmpty()) {
            return "Please provide logs after /debug";
        }
        try {
            String response = aiService.analyseLogs(logs);

            System.out.println(response);

            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response);

            String queryType = root.path("type").asText();

            if (queryType.equalsIgnoreCase("non_technical")) {
                return root.path("message").asText();
            } else {

                logService.save(chatId, userMessage, response);

                String rootCause = root.path("root_cause").asText();
                String fix = root.path("fix").asText();
                String severity = root.path("severity").asText();

                return """            
                                    
                        🧠 Root Cause:
                        
                        %s
                                    
                        ⚠️ Severity:
                        
                         %s
                                    
                                    
                        🔧 Fix:
                        
                        %s
                        """.formatted(rootCause, severity, fix);
            }
        }
        catch (RuntimeException e){
            e.printStackTrace();
            return "Server is handling too many requests! Try again after some time!";
        }
    }

    public String handleHistory(String chatId) throws JsonProcessingException {
        return logService.getHistory(chatId);
    }
}
