package de.manuai.ai;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.manuai.task.Task;
import de.manuai.task.TaskManager;
import de.manuai.storage.StorageManager;
import de.manuai.navigation.NavigationManager;
import de.manuai.ai.nlp.NaturalLanguageProcessor;

/**
 * The AI brain of ManuAI - handles decision making, task planning,
 * and overall strategic thinking.
 */
public class AIBrain {
	private static final Logger LOGGER = LoggerFactory.getLogger("ManuAI-AIBrain");

	private final TaskManager taskManager;
	private final StorageManager storageManager;
	private final NavigationManager navigationManager;
	private final NaturalLanguageProcessor nlpProcessor;

	public AIBrain(TaskManager taskManager, StorageManager storageManager, NavigationManager navigationManager) {
		this.taskManager = taskManager;
		this.storageManager = storageManager;
		this.navigationManager = navigationManager;
		this.nlpProcessor = new NaturalLanguageProcessor();
	}

	/**
	 * Process a natural language command and queue it as a task
	 */
	public void processCommand(String command) {
		LOGGER.info("Processing command: {}", command);
		
		try {
			Task task = nlpProcessor.parseCommand(command);
			taskManager.queueTask(task);
			LOGGER.info("Command queued as task: {}", task);
		} catch (Exception e) {
			LOGGER.error("Failed to process command: {}", command, e);
		}
	}

	/**
	 * Plan the current task based on world state and resources
	 */
	public TaskPlan planCurrentTask() {
		Task task = taskManager.getCurrentTask();
		if (task == null) {
			return null;
		}

		LOGGER.info("Planning task: {}", task);
		
		TaskPlan plan = new TaskPlan(task);
		// Detailed planning will be implemented based on task type
		
		task.setState(Task.TaskState.ACTIVE);
		return plan;
	}

	/**
	 * Get current task status for display
	 */
	public String getStatusReport() {
		Task current = taskManager.getCurrentTask();
		if (current == null) {
			return "🤖 ManuAI: Idle (waiting for tasks)";
		}
		
		return String.format("🤖 ManuAI\nTask: %s\nProgress: %.0f%%\nState: %s",
			current.getDescription(),
			current.getProgress() * 100,
			current.getState());
	}

	public TaskManager getTaskManager() {
		return taskManager;
	}

	public StorageManager getStorageManager() {
		return storageManager;
	}

	public NavigationManager getNavigationManager() {
		return navigationManager;
	}
}
