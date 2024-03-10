package com.example.flag.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class CSVServiceTest {

    @Mock
    private MockTranslationService translationService;

    @Mock
    private MockScoringService scoringService;

    @Mock
    private Logger logger;

    @InjectMocks
    private CSVService csvService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(translationService.translateToEnglish(anyString())).thenAnswer(i -> i.getArguments()[0]);
        when(scoringService.calculateScore(anyString())).thenReturn(0.5f);
    }

    @Test
    void testParseCSVWithValidData() {
        String csvData = "user_id,message\n1,Hello\n2,Hi";
        String expectedResult = "user_id,total_messages,avg_score\n1,1,0.50\n2,1,0.50\n";

        String result = csvService.parseCSV(csvData);

        assertEquals(expectedResult, result);
        verify(translationService, times(2)).translateToEnglish(anyString());
        verify(scoringService, times(2)).calculateScore(anyString());
    }

    @Test
    void testParseCSVWithEmptyData() {
        String csvData = "";
        String expectedResult = "user_id,total_messages,avg_score\n";

        String result = csvService.parseCSV(csvData);

        assertEquals(expectedResult, result);
        verifyNoInteractions(translationService);
        verifyNoInteractions(scoringService);
    }

    @Test
    void testParseCSVWithInvalidFormat() {
        String csvData = "This is not a CSV format";
        String expectedResult = "user_id,total_messages,avg_score\n";

        String result = csvService.parseCSV(csvData);

        assertEquals(expectedResult, result);
        verifyNoInteractions(translationService);
        verifyNoInteractions(scoringService);
    }

    @Test
    void testParseCSVWithMalformedLine() {
        String csvData = "user_id,message\n1,Hello\nMalformed Line";
        String expectedResult = "user_id,total_messages,avg_score\n1,1,0.50\n";

        String result = csvService.parseCSV(csvData);

        assertTrue(result.startsWith(expectedResult));
    }

    @Test
    void testParseCSVWithNetworkLatency() {
        MockitoAnnotations.openMocks(this);

        // Simulate network latency specifically for this test
        when(translationService.translateToEnglish(anyString()))
                .thenAnswer(invocation -> {
                    Thread.sleep(50); // Simulating network latency
                    return "Translated: " + invocation.getArguments()[0];
                });

        when(scoringService.calculateScore(anyString()))
                .thenAnswer(invocation -> {
                    Thread.sleep(200); // Simulating network latency
                    return 0.5f;
                });

        String csvData = "user_id,message\n1,Hello\n2,Hi";
        String expectedResult = "user_id,total_messages,avg_score\n1,1,0.50\n2,1,0.50\n";

        long startTime = System.currentTimeMillis();
        String result = csvService.parseCSV(csvData);
        long endTime = System.currentTimeMillis();

        assertEquals(expectedResult, result);
        assertTrue((endTime - startTime) >= 200, "Expected latency not observed");

        verify(translationService, times(2)).translateToEnglish(anyString());
        verify(scoringService, times(2)).calculateScore(anyString());
    }

    @Test
    void testPerformanceWithLargeData() {
        CSVService csvService = new CSVService();
        String largeCsvData = generateLargeCsvData(1000000); // Generate 1 million entries

        long startTime = System.currentTimeMillis();
        csvService.parseCSV(largeCsvData); // Replace with your method to process the CSV data
        long endTime = System.currentTimeMillis();

        long duration = endTime - startTime;
        System.out.println("Processing time: " + duration + " ms");

        // Define your acceptable processing time threshold
        long acceptableDuration = 10000; // Example threshold of 10 seconds
        assertTrue(duration < acceptableDuration, "Processing time exceeded acceptable duration.");
    }

    @Test
    void testTotalMessagesAndAvgScore() {

        // Simulate specific scores for testing
        when(scoringService.calculateScore(anyString()))
                .thenReturn(0.5f, 0.7f, 0.6f); // Returns these scores in sequence

        String csvData = "user_id,message\n1,Hello\n1,Hi\n2,Hey";
        String result = csvService.parseCSV(csvData);

        String expectedLineForUser1 = "1,2,0.60"; // User 1 has 2 messages, average score 0.60
        String expectedLineForUser2 = "2,1,0.60"; // User 2 has 1 message, average score 0.60

        assertTrue(result.contains(expectedLineForUser1), "Total messages or avg score for user 1 is incorrect.");
        assertTrue(result.contains(expectedLineForUser2), "Total messages or avg score for user 2 is incorrect.");
    }

    private String generateLargeCsvData(int numberOfLines) {
        StringBuilder sb = new StringBuilder("user_id,message\n");
        for (int i = 1; i <= numberOfLines; i++) {
            sb.append(i).append(",Sample message ").append(i).append("\n");
        }
        return sb.toString();
    }
}
