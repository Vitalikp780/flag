package com.example.flag.services;

import com.example.flag.models.UserMessage;
import com.example.flag.models.UserStats;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CSVService {

    @Autowired
    private MockTranslationService translationService;
    @Autowired
    private MockScoringService scoringService;

    private static final Logger logger = LoggerFactory.getLogger(CSVService.class);

    public String parseCSV(String csvData) {
        try {
            String adjustedCsvData = adjustCsvData(csvData);
            List<UserMessage> messages = extractMessages(adjustedCsvData);
            Map<String, UserStats> userStatsMap = processMessages(messages);
            return prepareCSVOutput(userStatsMap);
        } catch (Exception e) {
            logger.error("Unexpected error processing CSV data", e);
            return "Error processing CSV data";
        }
    }

    private String adjustCsvData(String csvData) {
        return csvData.replace("\\n", "\n");
    }

    private List<UserMessage> extractMessages(String csvData) {
        List<UserMessage> messages = new ArrayList<>();
        String[] lines = csvData.split("\n");
        boolean isFirstLine = true;

        for (String line : lines) {
            if (isFirstLine) {
                isFirstLine = false;
                continue;
            }
            try {
                String[] data = line.split(",");
                if (data.length >= 2) {
                    messages.add(new UserMessage(data[0].trim(), data[1].trim()));
                } else {
                    throw new IllegalArgumentException("Malformed CSV line: " + line);
                }
            } catch (IllegalArgumentException e) {
                logger.error("Malformed CSV line encountered: {}", line, e);
            }
        }
        return messages;
    }

    private Map<String, UserStats> processMessages(List<UserMessage> messages) {
        Map<String, UserStats> userStatsMap = new HashMap<>();
        messages.forEach(message -> processMessage(userStatsMap, message));
        return userStatsMap;
    }

    private void processMessage(Map<String, UserStats> userStatsMap, UserMessage message) {
        String translatedMessage = translationService.translateToEnglish(message.getMessage());
        float score = scoringService.calculateScore(translatedMessage);
        userStatsMap.computeIfAbsent(message.getUserID(), k -> new UserStats())
                .addMessage(score);
    }

    private String prepareCSVOutput(Map<String, UserStats> userStatsMap) {
        StringBuilder csvOutput = new StringBuilder("user_id,total_messages,avg_score\n");
        userStatsMap.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(entry -> csvOutput.append(formatCsvLine(entry.getKey(), entry.getValue())));
        return csvOutput.toString();
    }

    private String formatCsvLine(String userId, UserStats stats) {
        return String.format("%s,%d,%.2f\n", userId, stats.getCount(), stats.getAverageScore());
    }
}
