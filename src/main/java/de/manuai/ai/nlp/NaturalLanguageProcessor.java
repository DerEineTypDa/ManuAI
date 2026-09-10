package de.manuai.ai.nlp;

import java.util.regex.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.manuai.task.Task;

/**
 * Parses natural language commands into structured tasks.
 * Supports German and English commands.
 * Examples:
 * - "Hol 10 Stacks Eichenholz"
 * - "Bring mir 3 Stacks Cobblestone"
 * - "Besorg alles für ein Haus"
 */
public class NaturalLanguageProcessor {
	private static final Logger LOGGER = LoggerFactory.getLogger("ManuAI-NLP");

	public Task parseCommand(String input) throws IllegalArgumentException {
		input = input.trim();
		LOGGER.debug("Parsing NLP command: {}", input);

		// German patterns
		if (input.toLowerCase().startsWith("hol ")) {
			return parseGathering(input.substring(4));
		}
		if (input.toLowerCase().startsWith("bring ")) {
			return parseDelivery(input.substring(6));
		}
		if (input.toLowerCase().startsWith("besorg ")) {
			return parseAcquire(input.substring(7));
		}
		if (input.toLowerCase().startsWith("pflanz")) {
			return parseReplanting(input.substring(6));
		}
		if (input.toLowerCase().startsWith("pause")) {
			return createSimpleTask("PAUSE", input);
		}
		if (input.toLowerCase().startsWith("weiter") || input.toLowerCase().startsWith("mach weiter")) {
			return createSimpleTask("RESUME", input);
		}
		if (input.toLowerCase().startsWith("stopp") || input.toLowerCase().startsWith("abbrechen")) {
			return createSimpleTask("CANCEL", input);
		}
		if (input.toLowerCase().startsWith("status") || input.toLowerCase().startsWith("was machst")) {
			return createSimpleTask("STATUS", input);
		}

		// English patterns
		if (input.toLowerCase().startsWith("get ")) {
			return parseGathering(input.substring(4));
		}
		if (input.toLowerCase().startsWith("fetch ")) {
			return parseGathering(input.substring(6));
		}
		if (input.toLowerCase().startsWith("collect ")) {
			return parseGathering(input.substring(8));
		}

		throw new IllegalArgumentException("Unknown command format: " + input);
	}

	private Task parseGathering(String rest) {
		// Pattern: "10 Stacks Eichenholz" or "10 oak logs" or "Eichenholz"
		Pattern pattern = Pattern.compile("(\\d+)\\s+(stacks?|blocks?|items?)\\s+(.+)", Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(rest);

		int amount = 1;
		String itemType = "wood";

		if (matcher.find()) {
			amount = Integer.parseInt(matcher.group(1));
			String unit = matcher.group(2).toLowerCase();
			itemType = matcher.group(3).toLowerCase();

			// Convert to item count if needed
			if (unit.contains("stack")) {
				amount *= 64;
			}
		} else {
			// Just item type
			itemType = rest.toLowerCase();
		}

		String description = String.format("Gather %d x %s", amount, itemType);
		Task task = new Task(description, rest);
		task.setPriority(1);
		return task;
	}

	private Task parseDelivery(String rest) {
		// Pattern: "mir 3 Stacks Eichen" or "Holz zum Lager"
		Pattern pattern = Pattern.compile("(mir|dem lager|der kiste)?\\s*(\\d+)?\\s*(stacks?)?\\s*(.+)", Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(rest);

		String description = "Deliver items";
		if (matcher.find()) {
			String target = matcher.group(1) != null ? matcher.group(1) : "storage";
			String amount = matcher.group(2) != null ? matcher.group(2) : "1";
			String itemType = matcher.group(4);
			description = String.format("Deliver %s x %s to %s", amount, itemType, target);
		}

		Task task = new Task(description, rest);
		task.setPriority(2);
		return task;
	}

	private Task parseAcquire(String rest) {
		// Pattern: similar to gathering
		Pattern pattern = Pattern.compile("(\\d+)?\\s*(stacks?)?\\s*(.+)", Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(rest);

		String description = "Acquire resources";
		if (matcher.find()) {
			String amount = matcher.group(1) != null ? matcher.group(1) : "1";
			String itemType = matcher.group(3);
			description = String.format("Acquire %s x %s", amount, itemType);
		}

		Task task = new Task(description, rest);
		task.setPriority(1);
		return task;
	}

	private Task parseReplanting(String rest) {
		String description = "Replant saplings";
		Task task = new Task(description, rest);
		task.setPriority(0);
		return task;
	}

	private Task createSimpleTask(String type, String raw) {
		Task task = new Task(type, raw);
		task.setPriority(10); // High priority for control commands
		return task;
	}
}
