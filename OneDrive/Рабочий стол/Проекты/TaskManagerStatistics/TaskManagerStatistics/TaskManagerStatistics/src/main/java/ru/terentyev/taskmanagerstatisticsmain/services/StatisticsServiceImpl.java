package ru.terentyev.taskmanagerstatisticsmain.services;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;

import ru.terentyev.taskmanagerstatisticsmain.entities.CompositeKey;
import ru.terentyev.taskmanagerstatisticsmain.entities.Statistics;
import ru.terentyev.taskmanagerstatisticsmain.entities.Task;
import ru.terentyev.taskmanagerstatisticsmain.entities.TaskRequest;
import ru.terentyev.taskmanagerstatisticsmain.entities.Task.Priority;
import ru.terentyev.taskmanagerstatisticsmain.entities.Task.Status;
import ru.terentyev.taskmanagerstatisticsmain.repositories.StatisticsRepository;


@Service
@Transactional(readOnly = true)
public class StatisticsServiceImpl implements StatisticsService {
	
	// TODO задач выполнено за сутки, неделю, месяц
	// TODO открытых задач
	// TODO Exception Enum не найден единственный подходящий статус
	
	private StatisticsRepository statisticsRepository;
	private ObjectMapper objectMapper;
	
	
	@Autowired
	public StatisticsServiceImpl(StatisticsRepository statisticsRepository, ObjectMapper objectMapper) {
		super();
		this.statisticsRepository = statisticsRepository;
		this.objectMapper = objectMapper;
	}

	@Override
	@Transactional
	public Statistics getCommonStatistics() {
		return statisticsRepository.findById(new CompositeKey(0, null)).orElse(statisticsRepository.save(new Statistics(new CompositeKey(0, null))));
	}
	
	@Override
	@Transactional
	public Statistics getPersonalStatistics(Long id, boolean byAuthor) {
		CompositeKey key = new CompositeKey(id, byAuthor);
		return statisticsRepository.findById(key).orElse(statisticsRepository.save(new Statistics(key)));
	}
	
	public List<Statistics> prepareStatistics(Task task) {
        return new ArrayList<>(List.of(getCommonStatistics()
        		, getPersonalStatistics(task.getAuthor().getId(), true)
        		, getPersonalStatistics(task.getExecutor().getId(), false)));
	}
	
	@Override
	@Transactional(readOnly = false)
    @KafkaListener(topics = "post", groupId = "my-group")
    public void changeOnPost(byte[] message, Acknowledgment acknowledgment) throws StreamReadException, DatabindException, IOException {
		TaskRequest taskRequest = objectMapper.readValue(message, TaskRequest.class);
		Task task = taskRequest.getTaskBeforeChanges();
		List<Statistics> statistics = prepareStatistics(task);
        
        statistics.stream().forEach(s -> {
        	s.setDuringDay(s.getDuringDay() + 1);
        	s.setDuringWeek(s.getDuringWeek() + 1);
        	s.setDuringMonth(s.getDuringMonth() + 1);
        });
        
        statistics.stream().forEach(s -> s.setTotal(s.getTotal() + 1));
        
        	if (task.getStatus() == Task.Status.AWAITING) {
        		statistics.stream().forEach(s -> s.setAwaiting(s.getAwaiting() + 1));
        		if (task.getPriority() == Task.Priority.HIGH) 
        			statistics.stream().forEach(s -> s.setUrgent(s.getUrgent() + 1));     		
        	}
        	
        	if (task.getStatus() == Task.Status.PROCESSING) 
        		statistics.stream().forEach(s -> s.setProcessing(s.getProcessing() + 1));
        	
        	
        	if (task.getStatus() == Task.Status.COMPLETED) 
        		statistics.stream().forEach(s -> {
        			s.setCompleted(s.getCompleted() + 1);
        			s.setCompletedDuringDay(s.getCompletedDuringDay() + 1);
        			s.setCompletedDuringWeek(s.getCompletedDuringWeek() + 1);
        			s.setCompletedDuringMonth(s.getCompletedDuringMonth() + 1);
        			});
        	else
        		statistics.stream().forEach(s -> s.setOpen(s.getOpen() + 1));
        	     	
        statistics.stream().forEach(s -> statisticsRepository.save(s));
        acknowledgment.acknowledge();
    }
    
	@Override
	@Transactional(readOnly = false)
    @KafkaListener(topics = "patch", groupId = "my-group")
    public void changeOnPatch(byte[] message, Acknowledgment acknowledgment) throws StreamReadException, DatabindException, IOException {
		TaskRequest taskRequest = objectMapper.readValue(message, TaskRequest.class);
		Task task = taskRequest.getTaskBeforeChanges();
		List<Statistics> statistics = prepareStatistics(task);
    	
        	Status statusFromRequest = Task.Status.getStatusBySubstring(taskRequest.getStatus()[0]);
        	Status statusBeforePatch = task.getStatus();
        	if (statusFromRequest != statusBeforePatch) {
        		if (statusFromRequest == Task.Status.AWAITING)
        			statistics.stream().forEach(s -> s.setAwaiting(s.getAwaiting() + 1)); 
        		if (statusFromRequest == Task.Status.PROCESSING)
        			statistics.stream().forEach(s -> s.setProcessing(s.getProcessing() + 1));
        		if (statusFromRequest == Task.Status.COMPLETED) {
        			statistics.stream().forEach(s -> {
        				s.setCompleted(s.getCompleted() + 1);
        				s.setCompletedDuringDay(s.getCompletedDuringDay() + 1);
        				s.setCompletedDuringWeek(s.getCompletedDuringWeek() + 1);
        				s.setCompletedDuringMonth(s.getCompletedDuringMonth() + 1);
        				s.setOpen(s.getOpen() - 1);
        			});
        		}
        		
        		if (statusBeforePatch == Task.Status.AWAITING)
        			statistics.stream().forEach(s -> s.setAwaiting(s.getAwaiting() - 1));;
        		if (statusBeforePatch == Task.Status.PROCESSING)
        			statistics.stream().forEach(s -> s.setProcessing(s.getProcessing() - 1));;
        		if (statusBeforePatch == Task.Status.COMPLETED)
        			statistics.stream().forEach(s -> {
        				s.setCompleted(s.getCompleted() - 1);
        				s.setCompletedDuringDay(s.getCompletedDuringDay() - 1);
        				s.setCompletedDuringWeek(s.getCompletedDuringWeek() - 1);
        				s.setCompletedDuringMonth(s.getCompletedDuringMonth() - 1);
        				s.setOpen(s.getOpen() + 1);
        			});
        	}
        	
        	Priority priorityFromRequest = Task.Priority.getPriorityBySubstring(taskRequest.getPriority()[0]);
        	Priority priorityBeforePatch = taskRequest.getTaskBeforeChanges().getPriority();
        	
        	if ((priorityFromRequest == Task.Priority.HIGH && statusFromRequest == Task.Status.AWAITING) 
        			&& (priorityFromRequest != priorityBeforePatch || statusFromRequest != statusBeforePatch))
        		statistics.stream().forEach(s -> s.setUrgent(s.getUrgent() + 1));
        	
        	if ((priorityBeforePatch == Task.Priority.HIGH && statusBeforePatch == Task.Status.AWAITING) 
        			&& (priorityBeforePatch != priorityFromRequest || statusBeforePatch != statusFromRequest))
        		statistics.stream().forEach(s -> s.setUrgent(s.getUrgent() - 1));
        
        statistics.stream().forEach(s -> statisticsRepository.save(s));
        acknowledgment.acknowledge();
    }

	@Override
	@Transactional(readOnly = false)
	@KafkaListener(topics = "delete", groupId = "my-group")
	public void changeOnDelete(byte[] message, Acknowledgment acknowledgment) throws StreamReadException, DatabindException, IOException {
		TaskRequest taskRequest = objectMapper.readValue(message, TaskRequest.class);
		Task task = taskRequest.getTaskBeforeChanges();
		List<Statistics> statistics = prepareStatistics(task);
		
		statistics.stream().forEach(s -> s.setTotal(s.getTotal() - 1));
	
			Status status = task.getStatus();
			if (status == Task.Status.AWAITING) {
				if (task.getPriority() == Task.Priority.HIGH)
					statistics.stream().forEach(s -> s.setUrgent(s.getUrgent() - 1));
				statistics.stream().forEach(s -> s.setAwaiting(s.getAwaiting() - 1));
			}
			
			if (status == Task.Status.PROCESSING)
				statistics.stream().forEach(s -> s.setProcessing(s.getProcessing() - 1));
			if (status == Task.Status.COMPLETED)
				statistics.stream().forEach(s -> s.setCompleted(s.getCompleted() - 1));
			else
				statistics.stream().forEach(s -> s.setOpen(s.getOpen() - 1));		
			
			LocalDateTime now = LocalDateTime.now();
			
			if (status == Task.Status.COMPLETED 
					&& task.getStatusChangedAt().isAfter(now.minusMonths(1))) {
				statistics.stream().forEach(s -> s.setCompletedDuringMonth(s.getCompletedDuringMonth() - 1));
				if (task.getCreatedAt().isAfter(now.minusMonths(1))) {
					statistics.stream().forEach(s -> s.setDuringMonth(s.getDuringMonth() - 1));
					if (task.getCreatedAt().isAfter(now.minusWeeks(1))) {
						statistics.stream().forEach(s -> s.setDuringWeek(s.getDuringWeek() - 1));
						if (task.getCreatedAt().isAfter(now.minusDays(1)))
							statistics.stream().forEach(s -> s.setDuringDay(s.getDuringDay() - 1));
					}
				}
				
				if (task.getStatusChangedAt().isAfter(now.minusWeeks(1))) {
					statistics.stream().forEach(s -> s.setCompletedDuringWeek(s.getCompletedDuringWeek() - 1));
					if (task.getStatusChangedAt().isAfter(now.minusDays(1))) 
						statistics.stream().forEach(s -> s.setCompletedDuringDay(s.getCompletedDuringDay() - 1));
				}
			}	
		
		statistics.stream().forEach(s -> statisticsRepository.save(s));
		acknowledgment.acknowledge();
	}
}
