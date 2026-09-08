package at.woergl.manuai.task;

import at.woergl.manuai.ManuAI;
import at.woergl.manuai.storage.StorageManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Zentrale Verwaltung aller Tasks (siehe Lastenheft Punkt 5+6).
 *
 * Aktueller Ausbaustand (Fundament-Phase):
 *  - Tasks koennen erstellt, priorisiert, pausiert und abgebrochen werden
 *  - Bei Aufnahme eines Tasks wird der Lagerbestand geprueft (Planung)
 *  - Die eigentliche Ausfuehrung (Navigation/Mining/Einlagern) ist noch
 *    NICHT implementiert - das folgt in der naechsten Ausbaustufe, sobald
 *    Navigation + Mining + die ManuAI-Entity-Logik stehen. tick() ist
 *    bewusst als Erweiterungspunkt vorbereitet.
 *
 * Singleton, weil es pro Welt/Server genau einen TaskManager geben soll.
 * (Fuer Multi-World-Support liesse sich das spaeter auf ServerWorld-Ebene
 * verschieben, ist fuer den Einzelspieler-Anwendungsfall aber nicht noetig.)
 */
public class TaskManager {

	private static TaskManager instance;

	private final List<Task> tasks = new ArrayList<>();
	private boolean paused = false;

	private TaskManager() {
	}

	public static void init() {
		if (instance == null) {
			instance = new TaskManager();
		}
	}

	public static TaskManager getInstance() {
		if (instance == null) {
			init();
		}
		return instance;
	}

	/**
	 * Neuen Task anlegen und sofort planen (Lagerbestand pruefen).
	 * Entspricht Lastenheft Punkt 6: Was wird benoetigt, was ist im Lager,
	 * was fehlt tatsaechlich.
	 */
	public Task addTask(ServerLevel level, TaskType type, int amountRequested) {
		Task task = new Task(type, amountRequested);
		task.setState(TaskState.PLANNING);

		// Echter Lagerbestand aus den Chest-Inventaren (Lastenheft Punkt 6/11),
		// nicht nur "ist eine Chest zugeordnet ja/nein". Wenn noch nie gescannt
		// wurde, liefert das korrekt 0 zurueck (keine Chests bekannt).
		int inStorage = StorageManager.getInstance().getStoredAmount(level, type);
		int missing = Math.max(0, amountRequested - inStorage);

		task.setAmountInStorage(inStorage);
		task.setAmountMissing(missing);

		if (missing == 0) {
			task.setLastStatusMessageDe("Bereits genug im Lager vorhanden ("
					+ inStorage + "/" + amountRequested + "). Entnahme statt Sammeln noetig.");
		} else {
			task.setLastStatusMessageDe("Benoetigt: " + amountRequested
					+ ", im Lager: " + inStorage + ", fehlend: " + missing);
		}

		task.setState(TaskState.QUEUED);
		tasks.add(task);
		sortByPriority();

		ManuAI.LOGGER.info("Neuer Task erstellt: {}", task.describeDe());
		return task;
	}

	public List<Task> getTasks() {
		return List.copyOf(tasks);
	}

	public Optional<Task> getActiveTask() {
		return tasks.stream().filter(t -> t.getState() == TaskState.ACTIVE).findFirst();
	}

	public Optional<Task> findTask(UUID id) {
		return tasks.stream().filter(t -> t.getId().equals(id)).findFirst();
	}

	public boolean cancelTask(UUID id) {
		Optional<Task> task = findTask(id);
		task.ifPresent(t -> t.setState(TaskState.CANCELLED));
		return task.isPresent();
	}

	public void pauseAll() {
		paused = true;
	}

	public void resumeAll() {
		paused = false;
	}

	public boolean isPaused() {
		return paused;
	}

	private void sortByPriority() {
		tasks.sort(Comparator.comparingInt(Task::getPriority));
	}

	/**
	 * Wird periodisch vom Server-Tick aufgerufen (nicht jeden Tick,
	 * siehe ManuAI.onServerTick). Hier wird spaeter:
	 *  - der naechste QUEUED-Task aktiviert
	 *  - die ManuAI-Entity angewiesen, den aktiven Task auszufuehren
	 *  - erledigte/fehlgeschlagene Tasks aufgeraeumt
	 *
	 * Aktuell: nur die Grundlogik zum Aktivieren des naechsten Tasks,
	 * damit das Fundament testbar ist ohne dass schon eine Entity existiert.
	 */
	public void tick(MinecraftServer server) {
		if (paused) {
			return;
		}

		tasks.removeIf(t -> t.getState() == TaskState.CANCELLED);

		boolean hasActive = tasks.stream().anyMatch(t -> t.getState() == TaskState.ACTIVE);
		if (!hasActive) {
			tasks.stream()
					.filter(t -> t.getState() == TaskState.QUEUED)
					.findFirst()
					.ifPresent(next -> {
						next.setState(TaskState.ACTIVE);
						ManuAI.LOGGER.info("Task aktiviert: {}", next.describeDe());
						// TODO (naechste Ausbaustufe): ManuAIEntity anweisen,
						// diesen Task tatsaechlich auszufuehren (Navigation/Mining).
					});
		}
	}
}
