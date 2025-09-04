package ru.soroko.detection.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.soroko.detection.model.Transaction;

@Repository
public interface TransactionRepository {
    @Modifying
    @Query("UPDATE Transaction t SET t.status = SUSPICIOUS WHERE t.userId = :userId")
    void markSuspicious(@Param("userId") Long userId);

    Transaction save(Transaction transaction);
}
