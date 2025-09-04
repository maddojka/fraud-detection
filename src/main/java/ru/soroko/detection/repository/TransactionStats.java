package ru.soroko.detection.repository;

import lombok.Data;
import lombok.NoArgsConstructor;
import ru.soroko.detection.model.Transaction;

@Data
@NoArgsConstructor
public class TransactionStats {
    private long count;
    private double totalAmount;
    private double averageAmount;
    
    public TransactionStats update(Transaction tx) {
        count++;
        totalAmount += tx.getAmount();
        averageAmount = totalAmount / count;
        return this;
    }
}