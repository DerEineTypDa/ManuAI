package at.woergl.manuai.ai;

import at.woergl.manuai.ManuAI;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

public class ModEntityTypes {

	public static final EntityType<ManuAIEntity> MANUAI = register(
			"manuai",
			EntityType.Builder.<ManuAIEntity>of(ManuAIEntity::new, MobCategory.CREATURE)
					.sized(0.6f, 1.95f) // etwa Spielergroesse
	);

	private static <T extends Entity> EntityType<T> register(String name, EntityType.Builder<T> builder) {
		ResourceKey<EntityType<?>> key = ResourceKey.create(
				Registries.ENTITY_TYPE,
				Identifier.fromNamespaceAndPath(ManuAI.MOD_ID, name)
		);
		return Registry.register(BuiltInRegistries.ENTITY_TYPE, key, builder.build(key));
	}

	public static void registerModEntityTypes() {
		ManuAI.LOGGER.info("Registriere EntityTypes fuer {}", ManuAI.MOD_ID);
	}

	public static void registerAttributes() {
		FabricDefaultAttributeRegistry.register(MANUAI, ManuAIEntity.createAttributes());
	}
}
