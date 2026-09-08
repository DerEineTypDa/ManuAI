package at.woergl.manuai.task;

/**
 * Zustaende eines Tasks - siehe Lastenheft Punkt 5.
 */
public enum TaskState {
	QUEUED,
	PLANNING,
	ACTIVE,
	PAUSED,
	COMPLETED,
	FAILED,
	CANCELLED;

	public boolean isFinished() {
		return this == COMPLETED || this == FAILED || this == CANCELLED;
	}
}
