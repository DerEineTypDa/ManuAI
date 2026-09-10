package de.manuai.storage;

import java.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.item.ItemStack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Manages storage: chest locations, item frames, and inventory tracking.
 * Maps items to storage chests using item frame detection.
 */
public class StorageManager {
	private static final Logger LOGGER = LoggerFactory.getLogger("ManuAI-StorageManager");

	private final Map<String, List<BlockPos>> itemToChests;
	private final Map<BlockPos, String> chestToItem;
	private final Map<BlockPos, Integer> chestInventoryCounts;
	private BlockPos storageBasePosition;
	private boolean storageInitialized;

	public StorageManager() {
		this.itemToChests = new HashMap<>();
		this.chestToItem = new HashMap<>();
		this.chestInventoryCounts = new HashMap<>();
		this.storageInitialized = false;
	}

	/**
	 * Register a chest for a specific item
	 */
	public void registerChest(String itemType, BlockPos chestPos) {
		itemToChests.computeIfAbsent(itemType, k -> new ArrayList<>()).add(chestPos);
		chestToItem.put(chestPos, itemType);
		chestInventoryCounts.put(chestPos, 0);
		LOGGER.debug("Registered chest for {}: {}", itemType, chestPos);
	}

	/**
	 * Update chest inventory count
	 */
	public void updateChestCount(BlockPos chestPos, int count) {
		chestInventoryCounts.put(chestPos, count);
	}

	/**
	 * Get all chests for an item type
	 */
	public List<BlockPos> getChestsForItem(String itemType) {
		return itemToChests.getOrDefault(itemType, Collections.emptyList());
	}

	/**
	 * Get item type stored in a specific chest
	 */
	public String getItemTypeInChest(BlockPos chestPos) {
		return chestToItem.get(chestPos);
	}

	/**
	 * Get total stored amount of an item
	 */
	public int getTotalStoredAmount(String itemType) {
		return getChestsForItem(itemType).stream()
			.mapToInt(pos -> chestInventoryCounts.getOrDefault(pos, 0))
			.sum();
	}

	/**
	 * Get the most available chest for an item type
	 */
	public BlockPos getMostAvailableChest(String itemType) {
		return getChestsForItem(itemType).stream()
			.max(Comparator.comparingInt(pos -> chestInventoryCounts.getOrDefault(pos, 0)))
			.orElse(null);
	}

	/**
	 * Set storage base position (where item frames begin)
	 */
	public void setStorageBasePosition(BlockPos pos) {
		this.storageBasePosition = pos;
		LOGGER.info("Storage base position set to: {}", pos);
	}

	public BlockPos getStorageBasePosition() {
		return storageBasePosition;
	}

	/**
	 * Mark storage as initialized and ready
	 */
	public void markStorageInitialized() {
		this.storageInitialized = true;
		LOGGER.info("Storage system initialized. Registered items: {}", itemToChests.size());
	}

	public boolean isStorageInitialized() {
		return storageInitialized;
	}

	/**
	 * Clear all storage mappings
	 */
	public void clearStorage() {
		itemToChests.clear();
		chestToItem.clear();
		chestInventoryCounts.clear();
		storageInitialized = false;
		LOGGER.info("Storage system cleared");
	}

	/**
	 * Get storage summary
	 */
	public String getStorageSummary() {
		StringBuilder sb = new StringBuilder("=== MANUAI STORAGE ===\n");
		for (String item : itemToChests.keySet()) {
			List<BlockPos> chests = itemToChests.get(item);
			int total = getTotalStoredAmount(item);
			sb.append(String.format("%s: %d chests, %d items\n", item, chests.size(), total));
		}
		return sb.toString();
	}
}
