package at.woergl.manuai.task;

/**
 * Definiert welche Art von Ressourcen-Task ManuAI ausfuehren soll.
 * Jeder Typ ist an eine primaere Minecraft-Item-ID gekoppelt (fuer
 * Lagerbestandspruefung, Inventarpruefung usw.).
 *
 * Erweiterbar: neue Ressourcen einfach als neuer Eintrag hinzufuegen.
 * Die eigentliche "wo finde ich das"-Logik kommt spaeter in den
 * ResourceScanner (siehe Lastenheft Punkt 21).
 */
public enum TaskType {
	OAK_LOG("minecraft:oak_log", "Eichenholz"),
	SPRUCE_LOG("minecraft:spruce_log", "Fichtenholz"),
	BIRCH_LOG("minecraft:birch_log", "Birkenholz"),
	COBBLESTONE("minecraft:cobblestone", "Cobblestone"),
	IRON_ORE("minecraft:raw_iron", "Eisen"),
	COAL("minecraft:coal", "Kohle"),
	OAK_SAPLING("minecraft:oak_sapling", "Eichen-Setzling");

	private final String itemId;
	private final String displayNameDe;

	TaskType(String itemId, String displayNameDe) {
		this.itemId = itemId;
		this.displayNameDe = displayNameDe;
	}

	public String getItemId() {
		return itemId;
	}

	public String getDisplayNameDe() {
		return displayNameDe;
	}

	/**
	 * Wie viele Items stecken in einem "Stack" fuer diesen Typ.
	 * Fast alles ist 64, kann aber pro Ressource abweichen.
	 */
	public int getStackSize() {
		return 64;
	}

	public static TaskType fromKeyword(String keywordDe) {
		String k = keywordDe.toLowerCase();
		if (k.contains("eiche") && k.contains("setzling")) return OAK_SAPLING;
		if (k.contains("eiche") || k.contains("oak")) return OAK_LOG;
		if (k.contains("fichte") || k.contains("spruce")) return SPRUCE_LOG;
		if (k.contains("birke") || k.contains("birch")) return BIRCH_LOG;
		if (k.contains("cobble") || k.contains("stein")) return COBBLESTONE;
		if (k.contains("eisen") || k.contains("iron")) return IRON_ORE;
		if (k.contains("kohle") || k.contains("coal")) return COAL;
		return null;
	}
}
