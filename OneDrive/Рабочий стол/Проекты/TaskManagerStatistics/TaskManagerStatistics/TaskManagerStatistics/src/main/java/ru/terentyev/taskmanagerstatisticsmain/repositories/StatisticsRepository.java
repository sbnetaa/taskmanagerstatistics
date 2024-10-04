package ru.terentyev.taskmanagerstatisticsmain.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import ru.terentyev.taskmanagerstatisticsmain.entities.CompositeKey;
import ru.terentyev.taskmanagerstatisticsmain.entities.Statistics;


@Repository
public interface StatisticsRepository extends MongoRepository<Statistics, CompositeKey> {
}
