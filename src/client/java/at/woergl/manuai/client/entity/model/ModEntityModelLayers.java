package at.woergl.manuai.client.entity.model;

import at.woergl.manuai.ManuAI;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.resources.Identifier;

public final class ModEntityModelLayers {

	public static final ModelLayerLocation MANUAI = new ModelLayerLocation(
			Identifier.fromNamespaceAndPath(ManuAI.MOD_ID, "manuai"), "main");

	private ModEntityModelLayers() {
	}
}
