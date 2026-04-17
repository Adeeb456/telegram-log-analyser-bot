package bot.log_analyser.telegram.bot.rate_limiter;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class RateLimiter {

    private final Map<String, List<Long>> userRequests = new HashMap<>();

    private static final int LIMIT = 3; // max requests
    private static final long WINDOW = 60 * 1000; // 60 sec

    public boolean isAllowed(String chatId) {

        long now = System.currentTimeMillis();

        userRequests.putIfAbsent(chatId, new ArrayList<>());

        List<Long> timestamps = userRequests.get(chatId);

        // remove old timestamps
        timestamps.removeIf(time -> time < now - WINDOW);

        // block if limit reached
        if (timestamps.size() >= LIMIT) {
            return false;
        }

        // allow request
        timestamps.add(now);
        return true;
    }
}