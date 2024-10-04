package ru.terentyev.taskmanagerstatisticsmain.services;

import java.io.IOException;

import org.springframework.kafka.support.Acknowledgment;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;

import ru.terentyev.taskmanagerstatisticsmain.entities.Statistics;

public interface StatisticsService {

	Statistics getCommonStatistics();
	Statistics getPersonalStatistics(Long id, boolean byAuthor);
	void changeOnPost(byte[] message, Acknowledgment acknowledgment) throws StreamReadException, DatabindException, IOException;
	void changeOnPatch(byte[] message, Acknowledgment acknowledgment) throws StreamReadException, DatabindException, IOException;
	void changeOnDelete(byte[] message, Acknowledgment acknowledgment) throws StreamReadException, DatabindException, IOException;
}
