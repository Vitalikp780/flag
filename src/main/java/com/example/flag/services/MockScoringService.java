package com.example.flag.services;

import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class MockScoringService {
    private final Random random = new Random();
    public float calculateScore(String message) {
        return random.nextFloat();
    }
}