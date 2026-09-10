package de.manuai.task;

import java.util.UUID;

/**
 * Represents a single task for ManuAI to execute.
 * Tasks have multiple states and can be paused, resumed, or cancelled.
 */
public class Task {
	public enum TaskState {
		QUEUED,      // Waiting to be planned
		PLANNING,    // Being analyzed and planned
		ACTIVE,      // Currently executing
		PAUSED,      // Temporarily suspended
		COMPLETED,   // Successfully finished
		FAILED,      // Execution failed
		CANCELLED    // Cancelled by user
	}

	private final UUID id;
	private final String description;
	private final String rawNaturalLanguage;
	private TaskState state;
	private long createdAt;
	private long startedAt;
	private long completedAt;
	private String errorMessage;
	private float progress;
	private int priority;

	public Task(String description, String rawNaturalLanguage) {
		this.id = UUID.randomUUID();
		this.description = description;
		this.rawNaturalLanguage = rawNaturalLanguage;
		this.state = TaskState.QUEUED;
		this.createdAt = System.currentTimeMillis();
		this.progress = 0.0f;
		this.priority = 0;
	}

	// Getters and setters
	public UUID getId() {
		return id;
	}

	public String getDescription() {
		return description;
	}

	public String getRawNaturalLanguage() {
		return rawNaturalLanguage;
	}

	public TaskState getState() {
		return state;
	}

	public void setState(TaskState state) {
		this.state = state;
		if (state == TaskState.ACTIVE && startedAt == 0) {
			this.startedAt = System.currentTimeMillis();
		}
		if (state == TaskState.COMPLETED || state == TaskState.FAILED || state == TaskState.CANCELLED) {
			this.completedAt = System.currentTimeMillis();
		}
	}

	public float getProgress() {
		return progress;
	}

	public void setProgress(float progress) {
		this.progress = Math.max(0, Math.min(1, progress));
	}

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public long getDurationMs() {
		if (startedAt == 0) return 0;
		long end = completedAt > 0 ? completedAt : System.currentTimeMillis();
		return end - startedAt;
	}

	@Override
	public String toString() {
		return String.format("Task{id=%s, desc=%s, state=%s, progress=%.0f%%}", 
			id, description, state, progress * 100);
	}
}
