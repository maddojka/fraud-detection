package ru.soroko.detection.service;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import ru.soroko.detection.repository.TransactionRepository;

@Service
public class FraudProcessor {

    private final TransactionRepository repository;

    public FraudProcessor(TransactionRepository repository) {
        this.repository = repository;
    }

    @KafkaListener(topics = "fraud-alerts", groupId = "fraud-processor")
    public void handleFraudAlert(String message) {
        System.out.println("Received fraud alert: " + message);
        
        // Парсинг userID из сообщения
        if (message.startsWith("Fraud detected")) {
            String[] parts = message.split(" ");
            Long userId = Long.parseLong(parts[2]);
            repository.markSuspicious(userId);
        }
    }
}