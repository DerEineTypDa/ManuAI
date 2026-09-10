package de.manuai.ai;

import java.util.*;
import de.manuai.task.Task;

/**
 * Represents a detailed execution plan for a task.
 * Contains step-by-step instructions and resource requirements.
 */
public class TaskPlan {
	private final Task task;
	private final List<PlanStep> steps;
	private int currentStepIndex;

	public TaskPlan(Task task) {
		this.task = task;
		this.steps = new ArrayList<>();
		this.currentStepIndex = 0;
	}

	public void addStep(PlanStep step) {
		steps.add(step);
	}

	public PlanStep getCurrentStep() {
		if (currentStepIndex < steps.size()) {
			return steps.get(currentStepIndex);
		}
		return null;
	}

	public void advanceStep() {
		currentStepIndex++;
	}

	public boolean isComplete() {
		return currentStepIndex >= steps.size();
	}

	public Task getTask() {
		return task;
	}

	public List<PlanStep> getSteps() {
		return new ArrayList<>(steps);
	}

	/**
	 * Represents a single step in a task plan
	 */
	public static class PlanStep {
		public enum StepType {
			CHECK_STORAGE,
			SEARCH_RESOURCE,
			NAVIGATE_TO,
			MINE_RESOURCE,
			CRAFT_ITEM,
			STORE_ITEM,
			PLANT_SAPLING,
			RETURN_TO_STORAGE,
			COMPLETE
		}

		private final StepType type;
		private final String description;
		private final Map<String, Object> params;

		public PlanStep(StepType type, String description) {
			this.type = type;
			this.description = description;
			this.params = new HashMap<>();
		}

		public StepType getType() {
			return type;
		}

		public String getDescription() {
			return description;
		}

		public void setParam(String key, Object value) {
			params.put(key, value);
		}

		public Object getParam(String key) {
			return params.get(key);
		}
	}
}
