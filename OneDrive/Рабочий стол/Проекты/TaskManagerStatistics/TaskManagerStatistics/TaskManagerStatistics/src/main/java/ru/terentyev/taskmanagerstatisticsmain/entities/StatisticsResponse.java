package ru.terentyev.taskmanagerstatisticsmain.entities;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_ABSENT)
public class StatisticsResponse {
	
	private Statistics commonStatistics;
	private Statistics authorStatistics;
	private Statistics executorStatistics;
}
