package de.manuai.storage;

import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.world.World;
import net.minecraft.block.BlockState;
import net.minecraft.block.ChestBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Scans the world for item frames and chests, and creates storage mappings.
 * Detects glowing item frames and maps them to adjacent chests.
 */
public class StorageScanner {
	private static final Logger LOGGER = LoggerFactory.getLogger("ManuAI-StorageScanner");
	private static final int SCAN_RADIUS = 32;

	private final StorageManager storageManager;
	private final World world;
	private BlockPos scanCenter;

	public StorageScanner(StorageManager storageManager, World world) {
		this.storageManager = storageManager;
		this.world = world;
	}

	/**
	 * Start scanning from a center position
	 */
	public void scanFromPosition(BlockPos center) {
		this.scanCenter = center;
		LOGGER.info("Starting storage scan from {}", center);
		
		storageManager.clearStorage();
		
		// Get all item frames in the scan radius
		Box scanBox = new Box(center).expand(SCAN_RADIUS);
		List<ItemFrameEntity> itemFrames = world.getEntitiesByClass(ItemFrameEntity.class, scanBox, frame -> true);
		
		LOGGER.info("Found {} item frames", itemFrames.size());
		
		int registeredCount = 0;
		for (ItemFrameEntity frame : itemFrames) {
			BlockPos framePos = frame.getBlockPos();
			String itemType = getItemFrameLabel(frame);
			
			if (itemType != null && !itemType.isEmpty()) {
				BlockPos chestPos = findAdjacentChest(framePos);
				if (chestPos != null) {
					storageManager.registerChest(itemType, chestPos);
					registeredCount++;
					LOGGER.debug("Mapped {} to chest at {}", itemType, chestPos);
				}
			}
		}
		
		storageManager.markStorageInitialized();
		LOGGER.info("✓ Storage scan complete. Registered {} chest mappings", registeredCount);
	}

	/**
	 * Get the item type from an item frame
	 */
	private String getItemFrameLabel(ItemFrameEntity frame) {
		if (frame.getHeldItemStack().isEmpty()) {
			return null;
		}
		
		// Use item registry name as label
		String itemName = frame.getHeldItemStack().getItem().getName(frame.getHeldItemStack()).getString();
		return itemName.toLowerCase().replace(" ", "_");
	}

	/**
	 * Find adjacent chest to an item frame
	 */
	private BlockPos findAdjacentChest(BlockPos framePos) {
		// Check all 6 adjacent blocks
		BlockPos[] adjacentPositions = {
			framePos.up(),
			framePos.down(),
			framePos.north(),
			framePos.south(),
			framePos.east(),
			framePos.west()
		};
		
		for (BlockPos pos : adjacentPositions) {
			BlockState blockState = world.getBlockState(pos);
			if (blockState.getBlock() instanceof ChestBlock) {
				return pos;
			}
		}
		
		return null;
	}

	/**
	 * Scan and update inventory counts for all known chests
	 */
	public void updateAllChestCounts() {
		// This would require opening chests and reading their contents
		// For now, this is a placeholder for the chest reading system
		LOGGER.debug("Updating chest inventory counts");
	}
}
