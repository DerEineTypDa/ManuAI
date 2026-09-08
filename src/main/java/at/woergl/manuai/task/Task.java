package at.woergl.manuai.task;

import java.util.UUID;

/**
 * Ein einzelner Auftrag fuer ManuAI, z.B. "10 Stacks Eichenholz holen".
 *
 * Enthaelt bewusst nur Daten (kein Verhalten) - die eigentliche
 * Ausfuehrungslogik (Planung, Navigation, Mining, Einlagern) lebt
 * spaeter in eigenen Systemen, die dieses Objekt als Zustand nutzen.
 * Das haelt die Klasse klein und testbar (siehe Lastenheft Punkt 40/57).
 */
public class Task {

	private final UUID id = UUID.randomUUID();
	private final TaskType type;
	private final int amountRequested; // in einzelnen Items, nicht Stacks

	private TaskState state = TaskState.QUEUED;
	private int priority = 5; // 1 = hoechste Prioritaet, 10 = niedrigste

	// Planungsergebnis (wird vom TaskPlanner gefuellt, siehe Lastenheft Punkt 6)
	private int amountInStorage = 0;
	private int amountMissing = 0;
	private int amountCollectedSoFar = 0;

	private String lastStatusMessageDe = "";

	public Task(TaskType type, int amountRequested) {
		this.type = type;
		this.amountRequested = amountRequested;
	}

	public UUID getId() {
		return id;
	}

	public TaskType getType() {
		return type;
	}

	public int getAmountRequested() {
		return amountRequested;
	}

	public TaskState getState() {
		return state;
	}

	public void setState(TaskState state) {
		this.state = state;
	}

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	public int getAmountInStorage() {
		return amountInStorage;
	}

	public void setAmountInStorage(int amountInStorage) {
		this.amountInStorage = amountInStorage;
	}

	public int getAmountMissing() {
		return amountMissing;
	}

	public void setAmountMissing(int amountMissing) {
		this.amountMissing = amountMissing;
	}

	public int getAmountCollectedSoFar() {
		return amountCollectedSoFar;
	}

	public void addCollected(int amount) {
		this.amountCollectedSoFar += amount;
	}

	public boolean isComplete() {
		return amountCollectedSoFar >= amountMissing;
	}

	public String getLastStatusMessageDe() {
		return lastStatusMessageDe;
	}

	public void setLastStatusMessageDe(String msg) {
		this.lastStatusMessageDe = msg;
	}

	/**
	 * Kurze, menschenlesbare Zusammenfassung fuer /manuai status.
	 */
	public String describeDe() {
		return String.format(
				"%s: %d/%d benoetigt (Lager: %d, fehlend: %d) - %s",
				type.getDisplayNameDe(),
				amountCollectedSoFar,
				amountRequested,
				amountInStorage,
				amountMissing,
				state
		);
	}
}
