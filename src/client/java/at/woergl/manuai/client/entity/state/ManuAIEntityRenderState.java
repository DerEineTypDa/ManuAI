package at.woergl.manuai.client.entity.state;

import net.minecraft.client.renderer.entity.state.HumanoidRenderState;

/**
 * Render-State fuer ManuAI. Seit der Trennung von Entity-Logik und
 * Rendering (Render-State-Pattern) braucht jede Entity mit eigenem
 * Modell eine solche State-Klasse, die pro Frame aus der echten Entity
 * befuellt wird (siehe ManuAIEntityRenderer#extractRenderState).
 *
 * HumanoidRenderState liefert bereits alles, was ein zweibeiniges
 * Modell braucht (Lauf-/Armanimation, Sneak-Status etc.) - fuer die
 * Fundament-Phase reicht das ohne Erweiterung.
 */
public class ManuAIEntityRenderState extends HumanoidRenderState {
}
