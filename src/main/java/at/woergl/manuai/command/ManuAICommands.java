package at.woergl.manuai.command;

import at.woergl.manuai.storage.StorageManager;
import at.woergl.manuai.task.Task;
import at.woergl.manuai.task.TaskManager;
import at.woergl.manuai.task.TaskType;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

/**
 * Implementiert die in Lastenheft Punkt 37 geforderten Commands:
 * /manuai help, status, stop, pause, resume, setstorage, scanstorage,
 * storage, task ...
 *
 * Alle Commands sind server-seitig (CommandSourceStack), da sie auf
 * Weltzustand (Chests, Entities) zugreifen muessen.
 */
public final class ManuAICommands {

	private ManuAICommands() {
	}

	public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
		dispatcher.register(Commands.literal("manuai")
				.then(Commands.literal("help").executes(ManuAICommands::help))
				.then(Commands.literal("status").executes(ManuAICommands::status))
				.then(Commands.literal("pause").executes(ManuAICommands::pause))
				.then(Commands.literal("resume").executes(ManuAICommands::resume))
				.then(Commands.literal("stop").executes(ManuAICommands::stop))
				.then(Commands.literal("scanstorage")
						.executes(ManuAICommands::scanStorage)
						.then(Commands.argument("radius", IntegerArgumentType.integer(1, 64))
								.executes(ManuAICommands::scanStorageWithRadius)))
				.then(Commands.literal("storage").executes(ManuAICommands::storage))
				.then(Commands.literal("task")
						.then(Commands.argument("resource", StringArgumentType.word())
								.then(Commands.argument("amount", IntegerArgumentType.integer(1))
										.executes(ManuAICommands::createTask))))
		);
	}

	private static int help(CommandContext<CommandSourceStack> ctx) {
		ctx.getSource().sendSuccess(() -> Component.literal(
				"""
				ManuAI Befehle:
				/manuai status - aktuelle Tasks anzeigen
				/manuai task <ressource> <menge> - neuen Task erstellen (z.B. oak_log 640)
				/manuai scanstorage [radius] - Lager in der Naehe scannen (Standard: 16 Bloecke)
				/manuai storage - Lagerbestand anzeigen
				/manuai pause / resume / stop - Taskausfuehrung steuern
				"""), false);
		return 1;
	}

	private static int status(CommandContext<CommandSourceStack> ctx) {
		var tasks = TaskManager.getInstance().getTasks();
		if (tasks.isEmpty()) {
			ctx.getSource().sendSuccess(() -> Component.literal("Keine aktiven Tasks."), false);
			return 1;
		}
		StringBuilder sb = new StringBuilder("ManuAI Tasks:\n");
		for (Task t : tasks) {
			sb.append(" - ").append(t.describeDe()).append("\n");
		}
		String out = sb.toString();
		ctx.getSource().sendSuccess(() -> Component.literal(out), false);
		return 1;
	}

	private static int pause(CommandContext<CommandSourceStack> ctx) {
		TaskManager.getInstance().pauseAll();
		ctx.getSource().sendSuccess(() -> Component.literal("ManuAI pausiert."), true);
		return 1;
	}

	private static int resume(CommandContext<CommandSourceStack> ctx) {
		TaskManager.getInstance().resumeAll();
		ctx.getSource().sendSuccess(() -> Component.literal("ManuAI setzt fort."), true);
		return 1;
	}

	private static int stop(CommandContext<CommandSourceStack> ctx) {
		TaskManager.getInstance().getActiveTask().ifPresent(t ->
				TaskManager.getInstance().cancelTask(t.getId()));
		ctx.getSource().sendSuccess(() -> Component.literal("Aktueller Task abgebrochen."), true);
		return 1;
	}

	private static int scanStorage(CommandContext<CommandSourceStack> ctx) {
		return doScan(ctx, 16);
	}

	private static int scanStorageWithRadius(CommandContext<CommandSourceStack> ctx) {
		return doScan(ctx, IntegerArgumentType.getInteger(ctx, "radius"));
	}

	private static int doScan(CommandContext<CommandSourceStack> ctx, int radius) {
		ServerPlayer player = ctx.getSource().getPlayer();
		if (player == null) {
			ctx.getSource().sendFailure(Component.literal("Dieser Befehl muss von einem Spieler ausgefuehrt werden."));
			return 0;
		}
		ServerLevel level = ctx.getSource().getLevel();
		BlockPos center = player.blockPosition();

		int found = StorageManager.getInstance().scan(level, center, radius);
		ctx.getSource().sendSuccess(() -> Component.literal(
				"Lager gescannt (Radius " + radius + "). " + found + " Item-Frame/Chest-Zuordnungen gefunden."), true);
		return 1;
	}

	private static int storage(CommandContext<CommandSourceStack> ctx) {
		ServerLevel level = ctx.getSource().getLevel();
		String out = StorageManager.getInstance().describeDe(level);
		ctx.getSource().sendSuccess(() -> Component.literal(out), false);
		return 1;
	}

	private static int createTask(CommandContext<CommandSourceStack> ctx) {
		String resourceArg = StringArgumentType.getString(ctx, "resource");
		int amount = IntegerArgumentType.getInteger(ctx, "amount");

		TaskType type = TaskType.fromKeyword(resourceArg);
		if (type == null) {
			try {
				type = TaskType.valueOf(resourceArg.toUpperCase());
			} catch (IllegalArgumentException ignored) {
				// bleibt null
			}
		}

		if (type == null) {
			final String arg = resourceArg;
			ctx.getSource().sendFailure(Component.literal(
					"Unbekannte Ressource '" + arg + "'. Bekannt: oak_log, spruce_log, birch_log, cobblestone, iron_ore, coal, oak_sapling"));
			return 0;
		}

		ServerLevel level = ctx.getSource().getLevel();
		Task task = TaskManager.getInstance().addTask(level, type, amount);
		ctx.getSource().sendSuccess(() -> Component.literal(
				"Task erstellt: " + task.describeDe()), true);
		return 1;
	}
}
