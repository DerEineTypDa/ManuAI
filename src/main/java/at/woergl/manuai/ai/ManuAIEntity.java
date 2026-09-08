package at.woergl.manuai.ai;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.level.Level;

/**
 * Der eigentliche ManuAI-Mob.
 *
 * FUNDAMENT-STAND: Dieser Mob existiert, hat echtes Pathfinding-Grundgeruest
 * (PathfinderMob), Basis-Attribute und harmlose Vanilla-Goals (schwimmen,
 * umherlaufen, umschauen), damit er sich ueberhaupt sinnvoll in der Welt
 * bewegt und nicht sofort in Wasser/Lava ertrinkt.
 *
 * NOCH NICHT IMPLEMENTIERT (naechste Ausbaustufen):
 *  - Ein eigenes ManuAITaskGoal, das den aktiven Task aus dem TaskManager
 *    liest und daraus echtes Verhalten macht (zu Baum laufen, abbauen,
 *    zurueck zum Lager, einlagern usw.) - Lastenheft Punkt 21-34
 *  - Rendering (Model/Textur/Renderer) - ohne das erscheint der Mob beim
 *    Summonen ohne sichtbares Modell. Das ist der naechste sinnvolle
 *    Schritt nach dem ersten erfolgreichen Build, damit du ManuAI ueberhaupt
 *    siehst.
 *  - Inventar-Handling, Werkzeug-Crafting, Mining
 *
 * Zum Testen nach dem Bauen: /summon manuai:manuai
 */
public class ManuAIEntity extends PathfinderMob {

	public ManuAIEntity(EntityType<? extends ManuAIEntity> entityType, Level level) {
		super(entityType, level);
		this.setPersistenceRequired(); // despawnt nicht wie normale Mobs
	}

	public static AttributeSupplier.Builder createAttributes() {
		return PathfinderMob.createMobAttributes()
				.add(Attributes.MAX_HEALTH, 20.0)
				.add(Attributes.MOVEMENT_SPEED, 0.3)
				.add(Attributes.FOLLOW_RANGE, 48.0)
				.add(Attributes.STEP_HEIGHT, 1.0);
	}

	@Override
	protected void registerGoals() {
		this.goalSelector.addGoal(0, new FloatGoal(this));
		// TODO naechste Ausbaustufe: eigenes TaskExecutionGoal mit hoher
		// Prioritaet HIER einfuegen (Prioritaet z.B. 1), das den aktiven
		// Task aus TaskManager.getInstance().getActiveTask() ausliest.
		this.goalSelector.addGoal(4, new RandomStrollGoal(this, 0.8));
		this.goalSelector.addGoal(5, new LookAtPlayerGoal(this, LivingEntity.class, 6.0f));
		this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));
	}
}
