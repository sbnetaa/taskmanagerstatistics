package ru.terentyev.taskmanagerstatisticsmain.entities;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Task {
	private Long id;
	private String title;
	private String description;
	private Status status;
	private Priority priority;
	private Person author;
	private String authorName;
	private Person executor;
	private String executorName;
	private transient long executorId; 
	private List<Comment> comments;
	private transient int commentsCount;
	private transient int commentsPages;
	private LocalDateTime createdAt;
	private LocalDateTime editedAt;
	private LocalDateTime statusChangedAt;
	
	public Task(){}

	public enum Status{
		AWAITING("В ожидании"),
		PROCESSING("В процессе"),
		COMPLETED("Завершено");
		
		private String translation;
		
		public static Set<Status> getStatusesBySubstring(String[] parts){
			Set<Status> statuses = new HashSet<>();
			for (String part : parts) {
					statuses.add(getStatusBySubstring(part));
			}
			return statuses;
		}
		
		public static Status getStatusBySubstring(String part) {
			for (Status status : Status.values()) {
				if (status.name().toLowerCase().contains(part.toLowerCase())
						|| status.getTranslation().toLowerCase().contains(part.toLowerCase()))
					return status;
			}
			return null;
		}
		
		private Status(String translation) {
			this.translation = translation;
		}

		public String getTranslation() {
			return translation;
		}	
		
		
		
	}
	
	public enum Priority{
		LOW("Низкий"),
		MEDIUM("Средний"),
		HIGH("Высокий");
		
		private String translation;
		
		public static Set<Priority> getPrioritiesBySubstring(String[] parts){
			Set<Priority> priorities = new HashSet<>();
			for (String part : parts) 
				priorities.add(getPriorityBySubstring(part));		
			return priorities;
		}
		
		public static Priority getPriorityBySubstring(String part) {
			for (Priority priority : Priority.values()) {
				if (priority.name().toLowerCase().contains(part.toLowerCase())
						|| priority.getTranslation().toLowerCase().contains(part.toLowerCase()))
					return priority;
			}
			return null;
		}

		private Priority(String translation) {
			this.translation = translation;
		}

		public String getTranslation() {
			return translation;
		}			
	}
}
