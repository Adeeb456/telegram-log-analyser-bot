package bot.log_analyser.telegram.bot.repository;

import bot.log_analyser.telegram.bot.models.LogEntry;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LogRepository extends MongoRepository<LogEntry, String> {
    List<LogEntry> findByChatId(String chatId);

    List<LogEntry> findTop10ByChatIdOrderByTimestampDesc(String chatId);
}
