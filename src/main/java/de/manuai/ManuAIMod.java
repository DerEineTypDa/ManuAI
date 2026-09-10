package de.manuai;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.manuai.command.ManuAICommand;
import de.manuai.core.ManuAICore;

public class ManuAIMod implements ModInitializer {
	public static final String MOD_ID = "manuai";
	public static final String MOD_NAME = "ManuAI";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);

	private static ManuAICore core;

	@Override
	public void onInitialize() {
		LOGGER.info("🤖 Initializing ManuAI v0.1.0");
		
		core = new ManuAICore();
		core.initialize();
		
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
			ManuAICommand.register(dispatcher);
		});
		
		LOGGER.info("✅ ManuAI initialized successfully");
	}

	public static ManuAICore getCore() {
		return core;
	}
}
