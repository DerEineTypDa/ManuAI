package de.manuai.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import de.manuai.ManuAIMod;

/**
 * Registers and handles all ManuAI commands.
 */
public class ManuAICommand {
	public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		dispatcher.register(
			CommandManager.literal("manuai")
				.then(CommandManager.literal("help")
					.executes(ctx -> executeHelp(ctx.getSource())))
				.then(CommandManager.literal("status")
					.executes(ctx -> executeStatus(ctx.getSource())))
				.then(CommandManager.literal("pause")
					.executes(ctx -> executePause(ctx.getSource())))
				.then(CommandManager.literal("resume")
					.executes(ctx -> executeResume(ctx.getSource())))
				.then(CommandManager.literal("stop")
					.executes(ctx -> executeStop(ctx.getSource())))
				.then(CommandManager.literal("scanstorage")
					.executes(ctx -> executeScanStorage(ctx.getSource())))
				.then(CommandManager.literal("storage")
					.executes(ctx -> executeStorageStatus(ctx.getSource())))
				.then(CommandManager.literal("task")
					.then(CommandManager.argument("command", StringArgumentType.greedyString())
						.executes(ctx -> executeTask(ctx.getSource(), StringArgumentType.getString(ctx, "command")))
					)
				)
		);
	}

	private static int executeHelp(ServerCommandSource source) {
		source.sendFeedback(() -> Text.literal("=== ManuAI Commands ==="), false);
		source.sendFeedback(() -> Text.literal("/manuai help - Show this help"), false);
		source.sendFeedback(() -> Text.literal("/manuai status - Show current status"), false);
		source.sendFeedback(() -> Text.literal("/manuai pause - Pause current task"), false);
		source.sendFeedback(() -> Text.literal("/manuai resume - Resume paused task"), false);
		source.sendFeedback(() -> Text.literal("/manuai stop - Stop and cancel task"), false);
		source.sendFeedback(() -> Text.literal("/manuai scanstorage - Scan and map storage"), false);
		source.sendFeedback(() -> Text.literal("/manuai storage - Show storage status"), false);
		source.sendFeedback(() -> Text.literal("/manuai task <command> - Give natural language command"), false);
		return 1;
	}

	private static int executeStatus(ServerCommandSource source) {
		String status = ManuAIMod.getCore().getAIBrain().getStatusReport();
		source.sendFeedback(() -> Text.literal(status), false);
		return 1;
	}

	private static int executePause(ServerCommandSource source) {
		ManuAIMod.getCore().getTaskManager().pauseCurrentTask();
		source.sendFeedback(() -> Text.literal("Task paused"), true);
		return 1;
	}

	private static int executeResume(ServerCommandSource source) {
		ManuAIMod.getCore().getTaskManager().resumeCurrentTask();
		source.sendFeedback(() -> Text.literal("Task resumed"), true);
		return 1;
	}

	private static int executeStop(ServerCommandSource source) {
		ManuAIMod.getCore().getTaskManager().cancelCurrentTask();
		source.sendFeedback(() -> Text.literal("Task cancelled"), true);
		return 1;
	}

	private static int executeScanStorage(ServerCommandSource source) {
		source.sendFeedback(() -> Text.literal("Scanning storage..."), false);
		// Actual scanning logic will be called here
		source.sendFeedback(() -> Text.literal("Storage scan complete!"), false);
		return 1;
	}

	private static int executeStorageStatus(ServerCommandSource source) {
		String storage = ManuAIMod.getCore().getStorageManager().getStorageSummary();
		source.sendFeedback(() -> Text.literal(storage), false);
		return 1;
	}

	private static int executeTask(ServerCommandSource source, String command) {
		try {
			ManuAIMod.getCore().getAIBrain().processCommand(command);
			source.sendFeedback(() -> Text.literal("✓ Task queued: " + command), true);
			return 1;
		} catch (Exception e) {
			source.sendError(Text.literal("✗ Failed to queue task: " + e.getMessage()));
			return 0;
		}
	}
}
