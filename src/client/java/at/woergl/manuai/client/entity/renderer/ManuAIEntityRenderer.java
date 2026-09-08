package at.woergl.manuai.client.entity.renderer;

import at.woergl.manuai.ManuAI;
import at.woergl.manuai.ai.ManuAIEntity;
import at.woergl.manuai.client.entity.model.ManuAIEntityModel;
import at.woergl.manuai.client.entity.model.ModEntityModelLayers;
import at.woergl.manuai.client.entity.state.ManuAIEntityRenderState;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.resources.Identifier;

/**
 * Zeichnet ManuAI mit einem einfachen bipedalen Modell und der
 * mitgelieferten Arbeiter-Textur (assets/manuai/textures/entity/manuai_worker.png).
 *
 * HINWEIS fuer den lokalen Build: Dies ist der Teil des Projekts, der am
 * ehesten kleine Namensanpassungen braucht, falls sich zwischen dem hier
 * angenommenen Stand und deiner tatsaechlichen 26.2-API-Version einzelne
 * Methodennamen im Rendering-Bereich (extractRenderState/scale) leicht
 * unterscheiden - der Compiler zeigt das sofort und praezise an. Der Rest
 * der Mod (Server-Logik: Tasks, Storage, Commands) haengt NICHT davon ab.
 */
public class ManuAIEntityRenderer extends HumanoidMobRenderer<ManuAIEntity, ManuAIEntityRenderState, ManuAIEntityModel> {

	private static final Identifier TEXTURE =
			Identifier.fromNamespaceAndPath(ManuAI.MOD_ID, "textures/entity/manuai_worker.png");

	public ManuAIEntityRenderer(EntityRendererProvider.Context context) {
		super(context, new ManuAIEntityModel(context.bakeLayer(ModEntityModelLayers.MANUAI)), 0.5f);
	}

	@Override
	public Identifier getTextureLocation(ManuAIEntityRenderState state) {
		return TEXTURE;
	}

	@Override
	public ManuAIEntityRenderState createRenderState() {
		return new ManuAIEntityRenderState();
	}
}
