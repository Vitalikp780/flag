package com.example.flag.services;

import org.springframework.stereotype.Service;

@Service
public class MockTranslationService {

    public String translateToEnglish(String message) {
        return "Translated: " + message;
    }
}