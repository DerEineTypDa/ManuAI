package at.woergl.manuai.storage;

import at.woergl.manuai.ManuAI;
import at.woergl.manuai.task.TaskType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.phys.AABB;

import java.util.*;

/**
 * Verwaltet das Item-Frame-basierte Kistenlager (Lastenheft Punkt 7-17).
 *
 * FUNDAMENT-STAND:
 *  - /manuai scanstorage durchsucht einen Radius um den Spieler nach
 *    Item Frames, findet die dahinterliegende Chest und baut daraus
 *    ein Mapping Item -> Liste von Chest-Positionen.
 *  - getStoredAmount() liest die tatsaechlichen Chest-Inhalte aus
 *    (nicht nur das Item-Frame-Symbol) und summiert sie.
 *
 * NOCH NICHT IMPLEMENTIERT (naechste Ausbaustufen):
 *  - Persistenz ueber Weltneustart hinweg (Lastenheft Punkt 42, SavedData)
 *  - Erkennung "Chest voll" / automatischer Wechsel auf naechste Chest
 *    beim Einlagern (das Grundgeruest dafuer - Liste pro Item - ist da)
 *  - Glow Item Frames, Double Chests werden aktuell wie normale Chests
 *    behandelt (Minecraft liefert bei DoubleChest ueber den BlockEntity-
 *    Zugriff meist schon die kombinierte Sicht, das wird beim ersten
 *    echten Testlauf verifiziert)
 */
public class StorageManager {

	private static StorageManager instance;

	// Item-ID (z.B. "minecraft:oak_log") -> Liste von Chest-Positionen
	private final Map<String, List<BlockPos>> storageMapping = new HashMap<>();

	private StorageManager() {
	}

	public static void init() {
		if (instance == null) {
			instance = new StorageManager();
		}
	}

	public static StorageManager getInstance() {
		if (instance == null) {
			init();
		}
		return instance;
	}

	/**
	 * Durchsucht einen Wuerfel mit `radius` um `center` nach Item Frames,
	 * ermittelt die jeweils dahinterliegende Chest und baut das Mapping neu auf.
	 *
	 * @return Anzahl der gefundenen Item-Frame/Chest-Zuordnungen
	 */
	public int scan(ServerLevel level, BlockPos center, int radius) {
		storageMapping.clear();

		AABB searchBox = new AABB(center).inflate(radius);
		List<ItemFrame> frames = level.getEntitiesOfClass(ItemFrame.class, searchBox);

		int found = 0;
		for (ItemFrame frame : frames) {
			ItemStack held = frame.getItem();
			if (held.isEmpty()) {
				continue; // leerer Rahmen -> keine Zuordnung moeglich
			}

			BlockPos framePos = frame.blockPosition();
			Direction facing = frame.getDirection();
			// Der Rahmen haengt VOR dem Block, an dem er befestigt ist ->
			// der befestigte Block liegt in der Gegenrichtung der Blickrichtung.
			BlockPos attachedPos = framePos.relative(facing.getOpposite());

			BlockEntity be = level.getBlockEntity(attachedPos);
			if (!(be instanceof ChestBlockEntity)) {
				continue; // kein Chest direkt hinter dem Rahmen -> ignorieren
			}

			Identifier itemId = net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(held.getItem());
			String key = itemId.toString();

			storageMapping.computeIfAbsent(key, k -> new ArrayList<>()).add(attachedPos.immutable());
			found++;
		}

		ManuAI.LOGGER.info("Lager gescannt: {} Item-Frame/Chest-Zuordnungen gefunden.", found);
		return found;
	}

	public Map<String, List<BlockPos>> getMapping() {
		return Collections.unmodifiableMap(storageMapping);
	}

	public List<BlockPos> getChestsFor(TaskType type) {
		return storageMapping.getOrDefault(type.getItemId(), List.of());
	}

	/**
	 * Summiert den tatsaechlichen Item-Bestand ueber alle zugeordneten Chests.
	 * Braucht Zugriff auf die Welt, um die Chest-Inventare live zu lesen.
	 */
	public int getStoredAmount(ServerLevel level, TaskType type) {
		int total = 0;
		for (BlockPos pos : getChestsFor(type)) {
			BlockEntity be = level.getBlockEntity(pos);
			if (be instanceof ChestBlockEntity chest) {
				for (int i = 0; i < chest.getContainerSize(); i++) {
					ItemStack stack = chest.getItem(i);
					if (!stack.isEmpty() && itemMatches(stack.getItem(), type)) {
						total += stack.getCount();
					}
				}
			}
		}
		return total;
	}

	private boolean itemMatches(Item item, TaskType type) {
		Identifier id = net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(item);
		return id.toString().equals(type.getItemId());
	}

	/**
	 * Menschenlesbare Zusammenfassung fuer /manuai storage.
	 */
	public String describeDe(ServerLevel level) {
		if (storageMapping.isEmpty()) {
			return "Kein Lager bekannt. Fuehre zuerst /manuai scanstorage in der Naehe der Kisten aus.";
		}
		StringBuilder sb = new StringBuilder("MANUAI STORAGE\n");
		for (Map.Entry<String, List<BlockPos>> entry : storageMapping.entrySet()) {
			int totalItems = 0;
			for (BlockPos pos : entry.getValue()) {
				BlockEntity be = level.getBlockEntity(pos);
				if (be instanceof ChestBlockEntity chest) {
					for (int i = 0; i < chest.getContainerSize(); i++) {
						totalItems += chest.getItem(i).getCount();
					}
				}
			}
			sb.append(String.format("%s: %d Chest(s), %d Items%n",
					entry.getKey(), entry.getValue().size(), totalItems));
		}
		return sb.toString();
	}
}
