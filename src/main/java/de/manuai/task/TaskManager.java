package de.manuai.task;

import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Manages task lifecycle: creation, queuing, state transitions, and completion.
 */
public class TaskManager {
	private static final Logger LOGGER = LoggerFactory.getLogger("ManuAI-TaskManager");

	private final Queue<Task> taskQueue;
	private Task currentTask;
	private final Map<UUID, Task> completedTasks;
	private final Map<UUID, Task> failedTasks;

	public TaskManager() {
		this.taskQueue = new PriorityQueue<>(Comparator.comparingInt(Task::getPriority).reversed());
		this.completedTasks = new LinkedHashMap<>();
		this.failedTasks = new LinkedHashMap<>();
	}

	/**
	 * Add a new task to the queue
	 */
	public void queueTask(Task task) {
		taskQueue.add(task);
		LOGGER.info("Task queued: {}", task);
	}

	/**
	 * Get the next task to execute (if any)
	 */
	public Task getNextTask() {
		if (currentTask == null || currentTask.getState().equals(Task.TaskState.COMPLETED) 
			|| currentTask.getState().equals(Task.TaskState.FAILED)
			|| currentTask.getState().equals(Task.TaskState.CANCELLED)) {
			currentTask = taskQueue.poll();
			if (currentTask != null) {
				currentTask.setState(Task.TaskState.PLANNING);
				LOGGER.info("Next task: {}", currentTask);
			}
		}
		return currentTask;
	}

	/**
	 * Get the currently active task
	 */
	public Task getCurrentTask() {
		return currentTask;
	}

	/**
	 * Mark current task as completed
	 */
	public void completeCurrentTask() {
		if (currentTask != null) {
			currentTask.setState(Task.TaskState.COMPLETED);
			completedTasks.put(currentTask.getId(), currentTask);
			LOGGER.info("Task completed: {}", currentTask);
			currentTask = null;
		}
	}

	/**
	 * Mark current task as failed
	 */
	public void failCurrentTask(String reason) {
		if (currentTask != null) {
			currentTask.setState(Task.TaskState.FAILED);
			currentTask.setErrorMessage(reason);
			failedTasks.put(currentTask.getId(), currentTask);
			LOGGER.error("Task failed: {} - {}", currentTask, reason);
			currentTask = null;
		}
	}

	/**
	 * Cancel current task
	 */
	public void cancelCurrentTask() {
		if (currentTask != null) {
			currentTask.setState(Task.TaskState.CANCELLED);
			LOGGER.info("Task cancelled: {}", currentTask);
			currentTask = null;
		}
	}

	/**
	 * Pause current task
	 */
	public void pauseCurrentTask() {
		if (currentTask != null && currentTask.getState().equals(Task.TaskState.ACTIVE)) {
			currentTask.setState(Task.TaskState.PAUSED);
			LOGGER.info("Task paused: {}", currentTask);
		}
	}

	/**
	 * Resume current task
	 */
	public void resumeCurrentTask() {
		if (currentTask != null && currentTask.getState().equals(Task.TaskState.PAUSED)) {
			currentTask.setState(Task.TaskState.ACTIVE);
			LOGGER.info("Task resumed: {}", currentTask);
		}
	}

	/**
	 * Get queue size
	 */
	public int getQueueSize() {
		return taskQueue.size();
	}

	/**
	 * Get all tasks
	 */
	public List<Task> getAllTasks() {
		List<Task> all = new ArrayList<>(taskQueue);
		if (currentTask != null) {
			all.add(0, currentTask);
		}
		return all;
	}

	/**
	 * Clear all queued tasks
	 */
	public void clearQueue() {
		taskQueue.clear();
		LOGGER.info("Task queue cleared");
	}
}
