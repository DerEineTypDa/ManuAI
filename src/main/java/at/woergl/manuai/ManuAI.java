package at.woergl.manuai;

import at.woergl.manuai.ai.ModEntityTypes;
import at.woergl.manuai.command.ManuAICommands;
import at.woergl.manuai.storage.StorageManager;
import at.woergl.manuai.task.TaskManager;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ManuAI - Autonomer Survival-Mitspieler fuer Minecraft Java 26.2 (Fabric).
 *
 * Diese Klasse ist der Einstiegspunkt der Mod. Sie:
 *  - registriert den ManuAI-EntityType
 *  - registriert alle /manuai-Commands
 *  - haengt sich in den Server-Tick ein, damit TaskManager und
 *    StorageManager regelmaessig (nicht jeden Tick!) arbeiten koennen
 *
 * WICHTIG (Stand: Fundament-Phase):
 * Dies ist Schritt 1 von vielen laut Prioritaetenliste im Lastenheft
 * (Punkt 56): Mod startet + ManuAI existiert als echter Mob + Task-System
 * ist vorhanden. Navigation, Mining, Crafting, Item-Frame-Erkennung usw.
 * werden in den naechsten Ausbaustufen ergaenzt.
 */
public class ManuAI implements ModInitializer {

	public static final String MOD_ID = "manuai";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	// Nur alle X Ticks etwas Teures tun (siehe Lastenheft Punkt 43 - Performance).
	private static final int TASK_TICK_INTERVAL = 10; // alle 0.5s
	private int tickCounter = 0;

	@Override
	public void onInitialize() {
		LOGGER.info("ManuAI wird initialisiert...");

		ModEntityTypes.registerModEntityTypes();
		ModEntityTypes.registerAttributes();

		StorageManager.init();
		TaskManager.init();

		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) ->
				ManuAICommands.register(dispatcher));

		ServerTickEvents.END_SERVER_TICK.register(this::onServerTick);

		LOGGER.info("ManuAI erfolgreich geladen.");
	}

	private void onServerTick(net.minecraft.server.MinecraftServer server) {
		tickCounter++;
		if (tickCounter < TASK_TICK_INTERVAL) {
			return;
		}
		tickCounter = 0;
		TaskManager.getInstance().tick(server);
	}
}
