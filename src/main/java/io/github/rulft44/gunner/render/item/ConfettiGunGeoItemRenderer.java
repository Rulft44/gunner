package io.github.rulft44.gunner.render.item;

import io.github.rulft44.gunner.Gunner;
import io.github.rulft44.gunner.item.ConfettiGunItem;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.DefaultedItemGeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class ConfettiGunGeoItemRenderer extends GeoItemRenderer<ConfettiGunItem> {
	public ConfettiGunGeoItemRenderer() {
		super(new DefaultedItemGeoModel(Identifier.of(Gunner.ID, "confetti_gun")));
	}
}
