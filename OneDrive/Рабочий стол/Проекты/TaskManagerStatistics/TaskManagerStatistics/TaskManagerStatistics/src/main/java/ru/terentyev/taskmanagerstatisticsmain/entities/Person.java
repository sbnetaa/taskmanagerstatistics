package ru.terentyev.taskmanagerstatisticsmain.entities;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Person {
	private Long id;
	private String name;
	private String email;
	private String password;
	private transient String passwordConfirm;
	private List<Task> createdTasks;
	private List<Task> executableTasks;
	private LocalDateTime registrationDate;
	private List<Comment> comments;

	public Person(){}
}
