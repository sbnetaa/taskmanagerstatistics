package ru.terentyev.taskmanagerstatisticsmain.entities;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TaskRequest {
	
	private Long[] id;
	private String[] title;
	private String[] description;
	private String[] status;
	private String[] priority;
	private Long[] author;
	private String[] authorName;
	private Long[] executor;
	private String[] executorName;
	private String orderBy;
	private String createdBefore;
	private String createdAfter;
	private String editedBefore;
	private String editedAfter;
	private Integer page;
	private Task taskBeforeChanges;
	
	public TaskRequest(){}
}
