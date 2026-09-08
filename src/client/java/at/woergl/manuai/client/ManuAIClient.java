package at.woergl.manuai.client;

import at.woergl.manuai.ai.ModEntityTypes;
import at.woergl.manuai.client.entity.model.ManuAIEntityModel;
import at.woergl.manuai.client.entity.model.ModEntityModelLayers;
import at.woergl.manuai.client.entity.renderer.ManuAIEntityRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;

/**
 * Client-seitiger Einstiegspunkt (Lastenheft Punkt 38 - GUI/Komfort
 * beginnt hier, aktuell nur das reine Rendering des Mobs).
 *
 * Ohne diese Klasse wuerde ManuAI beim Summonen zwar existieren
 * (Server-seitig voll funktionsfaehig), aber unsichtbar sein, weil
 * kein EntityRenderer registriert ist.
 */
public class ManuAIClient implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
		EntityModelLayerRegistry.registerModelLayer(
				ModEntityModelLayers.MANUAI, ManuAIEntityModel::createBodyLayer);

		EntityRendererRegistry.register(ModEntityTypes.MANUAI, ManuAIEntityRenderer::new);
	}
}
