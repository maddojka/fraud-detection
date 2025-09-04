package ru.soroko.detection.model;


import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
public class Transaction {
    @Id
    @GeneratedValue
    private UUID id;

    private Double amount;
    private String currency;
    private Long userId;
    private Status status;
    private LocalDateTime timestamp;

    public enum Status {
        NEW, SUSPICIOUS, CONFIRMED
    }
}
