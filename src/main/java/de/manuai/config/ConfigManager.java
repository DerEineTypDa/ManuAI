package de.manuai.config;

import java.io.*;
import java.nio.file.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Manages ManuAI configuration files and persistent data.
 */
public class ConfigManager {
	private static final Logger LOGGER = LoggerFactory.getLogger("ManuAI-ConfigManager");
	private static final String CONFIG_DIR = "config/manuai";

	public ConfigManager() {
		ensureConfigDirectory();
	}

	private void ensureConfigDirectory() {
		try {
			Path configPath = Paths.get(CONFIG_DIR);
			Files.createDirectories(configPath);
			LOGGER.info("Config directory ready: {}", CONFIG_DIR);
		} catch (IOException e) {
			LOGGER.warn("Failed to create config directory", e);
		}
	}

	/**
	 * Save configuration
	 */
	public void saveConfig(String filename, String content) {
		try {
			Path file = Paths.get(CONFIG_DIR, filename);
			Files.write(file, content.getBytes());
			LOGGER.debug("Config saved: {}", filename);
		} catch (IOException e) {
			LOGGER.error("Failed to save config: {}", filename, e);
		}
	}

	/**
	 * Load configuration
	 */
	public String loadConfig(String filename) {
		try {
			Path file = Paths.get(CONFIG_DIR, filename);
			if (Files.exists(file)) {
				return new String(Files.readAllBytes(file));
			}
		} catch (IOException e) {
			LOGGER.error("Failed to load config: {}", filename, e);
		}
		return null;
	}

	/**
	 * Get config file path
	 */
	public Path getConfigPath(String filename) {
		return Paths.get(CONFIG_DIR, filename);
	}
}
