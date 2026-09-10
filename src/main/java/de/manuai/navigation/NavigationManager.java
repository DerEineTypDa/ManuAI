package de.manuai.navigation;

import net.minecraft.util.math.BlockPos;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Manages pathfinding, movement, and navigation for ManuAI.
 * Detects when stuck and handles stuck recovery.
 */
public class NavigationManager {
	private static final Logger LOGGER = LoggerFactory.getLogger("ManuAI-NavigationManager");

	private BlockPos currentTarget;
	private BlockPos lastPosition;
	private long lastPositionUpdateTime;
	private static final long STUCK_CHECK_INTERVAL_MS = 1000;
	private static final double STUCK_DISTANCE_THRESHOLD = 0.5;
	private int stuckCounter;
	private static final int MAX_STUCK_ATTEMPTS = 5;

	public NavigationManager() {
		this.lastPosition = null;
		this.stuckCounter = 0;
	}

	/**
	 * Set navigation target
	 */
	public void setTarget(BlockPos target) {
		this.currentTarget = target;
		LOGGER.info("Navigation target set to: {}", target);
	}

	/**
	 * Get current target
	 */
	public BlockPos getTarget() {
		return currentTarget;
	}

	/**
	 * Check if stuck
	 */
	public boolean isStuck(BlockPos currentPos) {
		long now = System.currentTimeMillis();
		
		if (lastPosition == null) {
			lastPosition = currentPos;
			lastPositionUpdateTime = now;
			return false;
		}
		
		if (now - lastPositionUpdateTime < STUCK_CHECK_INTERVAL_MS) {
			return false;
		}
		
		double distance = currentPos.getSquaredDistance(lastPosition);
		if (distance < STUCK_DISTANCE_THRESHOLD) {
			stuckCounter++;
			if (stuckCounter >= MAX_STUCK_ATTEMPTS) {
				LOGGER.warn("ManuAI appears to be stuck at {}", currentPos);
				return true;
			}
		} else {
			stuckCounter = 0; // Reset if moving
		}
		
		lastPosition = currentPos;
		lastPositionUpdateTime = now;
		return false;
	}

	/**
	 * Handle stuck situation
	 */
	public void handleStuck() {
		stuckCounter = 0;
		LOGGER.info("Attempting to recover from stuck state");
		// Actual recovery logic will be implemented in entity-specific code
	}

	/**
	 * Clear target
	 */
	public void clearTarget() {
		this.currentTarget = null;
		this.stuckCounter = 0;
		this.lastPosition = null;
	}

	/**
	 * Estimate distance to target
	 */
	public double getDistanceToTarget(BlockPos currentPos) {
		if (currentTarget == null) return -1;
		return currentPos.getSquaredDistance(currentTarget);
	}

	/**
	 * Check if reached target
	 */
	public boolean hasReachedTarget(BlockPos currentPos) {
		if (currentTarget == null) return false;
		return currentPos.getSquaredDistance(currentTarget) <= 4.0; // Within 2 blocks
	}
}
