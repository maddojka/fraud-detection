package ru.soroko.detection.service;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.*;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.annotation.EnableKafkaStreams;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.support.serializer.JsonSerde;
import org.springframework.stereotype.Service;
import ru.soroko.detection.model.Transaction;

import java.time.Duration;

@EnableKafkaStreams
@Service
public class FraudDetectionService {
    private static final String INPUT_TOPIC = "fraud_db_server.public.transaction";
    private static final Duration WINDOW_SIZE = Duration.ofMinutes(5);
    private static final String OUTPUT_TOPIC = "fraud-alerts";

    @Bean
    public KStream<Windowed<String>, String> kStream(StreamsBuilder builder) {

        // 2. Десериализация транзакций
        JsonSerde<Transaction> transactionSerde = new JsonSerde<>(Transaction.class);
        KStream<String, Transaction> stream = builder.stream(
                INPUT_TOPIC,
                Consumed.with(Serdes.String(), transactionSerde)
        );

        // 3. Фильтрация и обработка
        KTable<Windowed<String>, Long> counts = stream
                .filter((key, tx) -> tx.getStatus() == Transaction.Status.NEW)
                .selectKey((key, tx) -> tx.getUserId().toString())
                .groupByKey(Grouped.with(Serdes.String(), transactionSerde))
                .windowedBy(TimeWindows.ofSizeWithNoGrace(WINDOW_SIZE))
                .count();

        // 4. Генерация алертов
        counts.toStream()
                .filter((windowedKey, count) -> count >= 3)
                .map((windowedKey, count) ->
                        KeyValue.pair(
                                windowedKey.key(),
                                "Fraud detected: " + count + " User: " + windowedKey.key()
                        )
                )
                .to(OUTPUT_TOPIC, Produced.with(Serdes.String(), Serdes.String()));

        return counts.toStream().mapValues(Object::toString);
    }

    @Bean
    public NewTopic fraudAlertsTopic() {
        return TopicBuilder.name("fraud-alerts")
                .partitions(3)
                .replicas(3)  // Для отказоустойчивости
                .build();
    }
}