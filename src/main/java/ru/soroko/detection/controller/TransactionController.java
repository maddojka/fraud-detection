package ru.soroko.detection.controller;

import ru.soroko.detection.model.Transaction;
import ru.soroko.detection.repository.TransactionRepository;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/transactions")
public class TransactionController {
    
    private final TransactionRepository repository;

    public TransactionController(TransactionRepository repository) {
        this.repository = repository;
    }

    @PostMapping
    public Transaction createTransaction(@RequestBody Transaction transaction) {
        transaction.setStatus(Transaction.Status.NEW);
        transaction.setTimestamp(LocalDateTime.now());
        return repository.save(transaction);
    }
}