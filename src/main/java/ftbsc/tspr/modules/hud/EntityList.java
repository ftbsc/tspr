package ftbsc.tspr.modules.hud;

import com.google.auto.service.AutoService;

import ftbsc.tspr.api.ILoadable;
import ftbsc.tspr.api.module.HudModule;
import net.minecraft.ChatFormatting;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.util.ARGB;
import net.minecraft.world.entity.Entity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.gui.GuiLayer;
import net.neoforged.neoforge.common.ModConfigSpec;

import static ftbsc.tspr.Tiramisuper.mc;

import java.util.LinkedList;
import java.util.List;

@AutoService(ILoadable.class)
public class EntityList extends HudModule {

	private ModConfigSpec.EnumValue<ChatFormatting> color;
	private ModConfigSpec.BooleanValue living;

	public void config(ModConfigSpec.Builder builder) {
		this.color = builder
			.comment("color to use")
			.defineEnum("color", ChatFormatting.WHITE);
		this.living = builder
			.comment("only list living entities")
			.define("living", true);
	}

	public GuiLayer getLayer() {
		return new EntityListLayer(this);
	}

	private List<Component> entities = new LinkedList<>();

	@SubscribeEvent
	void onTick(ClientTickEvent.Post event) {
		if (!this.isEnabled() || mc().level == null) return;
		List<Component> out = new LinkedList<>();
		for (Entity e : mc().level.entitiesForRendering()) {
			if (this.living.getAsBoolean() && !e.isAlive()) continue;
			out.add(e.getDisplayName());
		}
		this.entities = out;
	}


	public class EntityListLayer implements GuiLayer {
		private final EntityList mod;
		public EntityListLayer(EntityList mod) {
			this.mod = mod;
		}
		@Override
		public void render(GuiGraphics gui, DeltaTracker deltaTracker) {
			if (this.mod.shouldHide()) return;

			int y = this.mod.getY();
			int x = this.mod.getX();
			int color = ARGB.opaque(this.mod.color.get().getColor());
			float scale = (float) this.mod.scale.getAsDouble();

			gui.pose().pushMatrix();
			gui.pose().scale(scale);

			for (Component row : mod.entities) {
				gui.drawString(
					mc().font,
					this.mod.prefixed(row),
					x,
					y,
					color
				);
				y = this.mod.inc(y, mc().font.lineHeight + 1);
			}

			gui.pose().popMatrix();
		}
	}
	
}
