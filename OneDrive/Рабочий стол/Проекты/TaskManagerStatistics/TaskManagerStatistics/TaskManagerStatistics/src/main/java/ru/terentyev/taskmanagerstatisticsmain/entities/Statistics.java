package ru.terentyev.taskmanagerstatisticsmain.entities;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Document(collection = "statistics")
public class Statistics {
	@Id
	@JsonIgnore
	private CompositeKey id; 
	private int total;
	private int urgent;
	private int awaiting;
	private int processing;
	private int completed;
	private int open;
	private int duringDay;
	private int duringWeek;
	private int duringMonth;
	private int completedDuringDay;
	private int completedDuringWeek;
	private int completedDuringMonth;
	
	public Statistics(){}
	public Statistics(CompositeKey id) {
		this.id = id;
	}
}
