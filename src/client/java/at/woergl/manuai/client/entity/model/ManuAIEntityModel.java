package at.woergl.manuai.client.entity.model;

import at.woergl.manuai.client.entity.state.ManuAIEntityRenderState;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;

/**
 * ManuAI verwendet fuer die Fundament-Phase ein normales bipedales
 * Modell (Kopf/Torso/Arme/Beine) - eine eigene, angepasste Geometrie
 * (z.B. Werkzeuggurt, Rucksack fuer den Item-Transport) ist ein
 * spaeterer Feinschliff-Schritt (Lastenheft Punkt 38 - Komfort/GUI-Phase),
 * kein Blocker fuer Funktion.
 */
public class ManuAIEntityModel extends HumanoidModel<ManuAIEntityRenderState> {

	public ManuAIEntityModel(ModelPart root) {
		super(root);
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition mesh = HumanoidModel.createMesh(net.minecraft.client.model.geom.builders.CubeDeformation.NONE, 0.0f);
		// 64x64 = modernes Standard-Layout (wie Spieler-/Zombie-Skins).
		// Die mitgelieferte manuai_worker.png ist aktuell 64x48 - fuer
		// exaktes UV-Mapping am besten lokal auf 64x64 skalieren
		// (z.B. in Affinity Photo/Photopea, transparent auffuellen).
		// Bis dahin rendert ManuAI trotzdem, die Textur sitzt nur nicht
		// pixelgenau auf jedem Body-Part.
		return LayerDefinition.create(mesh, 64, 64);
	}
}
