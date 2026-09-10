package de.manuai.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.manuai.ManuAIMod;
import de.manuai.task.TaskManager;
import de.manuai.storage.StorageManager;
import de.manuai.navigation.NavigationManager;
import de.manuai.ai.AIBrain;
import de.manuai.config.ConfigManager;
import de.manuai.util.TickManager;

/**
 * Core orchestrator for all ManuAI systems.
 * Manages initialization, lifecycle, and coordination between components.
 */
public class ManuAICore {
	private static final Logger LOGGER = LoggerFactory.getLogger("ManuAI-Core");

	private ConfigManager configManager;
	private TaskManager taskManager;
	private StorageManager storageManager;
	private NavigationManager navigationManager;
	private AIBrain aiBrain;
	private TickManager tickManager;

	public void initialize() {
		LOGGER.info("Initializing ManuAI Core Components...");
		
		try {
			this.configManager = new ConfigManager();
			LOGGER.info("✓ ConfigManager initialized");
			
			this.storageManager = new StorageManager();
			LOGGER.info("✓ StorageManager initialized");
			
			this.taskManager = new TaskManager();
			LOGGER.info("✓ TaskManager initialized");
			
			this.navigationManager = new NavigationManager();
			LOGGER.info("✓ NavigationManager initialized");
			
			this.aiBrain = new AIBrain(taskManager, storageManager, navigationManager);
			LOGGER.info("✓ AIBrain initialized");
			
			this.tickManager = new TickManager();
			LOGGER.info("✓ TickManager initialized");
			
			LOGGER.info("🚀 All ManuAI Core components initialized successfully!");
		} catch (Exception e) {
			LOGGER.error("Fatal error during ManuAI initialization", e);
			throw new RuntimeException("Failed to initialize ManuAI", e);
		}
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

	public AIBrain getAIBrain() {
		return aiBrain;
	}

	public ConfigManager getConfigManager() {
		return configManager;
	}

	public TickManager getTickManager() {
		return tickManager;
	}
}
