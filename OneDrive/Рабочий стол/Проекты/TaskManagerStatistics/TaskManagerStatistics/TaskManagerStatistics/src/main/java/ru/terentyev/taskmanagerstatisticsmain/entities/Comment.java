package ru.terentyev.taskmanagerstatisticsmain.entities;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Comment {
	private Long id;
	private String body;
	private Task task;
	private transient long taskId;
	private Person author;
	private LocalDateTime createdAt;
	private LocalDateTime editedAt;
	
	public Comment(){}
}

