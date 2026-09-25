package ftbsc.tspr.modules.hud;

import com.google.auto.service.AutoService;

import ftbsc.tspr.api.ILoadable;
import ftbsc.tspr.api.module.HudModule;
import ftbsc.tspr.helpers.Color;
import ftbsc.tspr.helpers.Lang;
import net.minecraft.ChatFormatting;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.ARGB;
import net.minecraft.world.entity.Entity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.gui.GuiLayer;
import net.neoforged.neoforge.common.ModConfigSpec;

import static ftbsc.tspr.Tiramisuper.mc;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@AutoService(ILoadable.class)
public class EntityList extends HudModule {

	private ModConfigSpec.EnumValue<ChatFormatting> color;
	private ModConfigSpec.BooleanValue living;

	@Override
	public void config(ModConfigSpec.Builder builder) {
		this.color = builder
			.comment("color to use")
			.defineEnum("color", ChatFormatting.WHITE);
		this.living = builder
			.comment("only list living entities")
			.define("living", true);
	}

	@Override
	public GuiLayer getLayer() {
		return new EntityListLayer(this);
	}

	private List<Component> entities = new LinkedList<>();

	@SubscribeEvent
	void onTick(ClientTickEvent.Post event) {
		ClientLevel level = mc().level;
		if (!this.isEnabled() || level == null) return;

		Map<String, Integer> unique = new HashMap<>();
		for (Entity e : level.entitiesForRendering()) {
			if (this.living.getAsBoolean() && !e.isAlive()) continue;
			String name = e.getDisplayName().getString();
			unique.putIfAbsent(name, 0);
			unique.compute(name, (k, v) -> v + 1);
		}

		List<Component> out = new LinkedList<>();
		for (Map.Entry<String, Integer> entry : unique.entrySet()) {
			MutableComponent row = Component.literal("").append(Component.literal(entry.getKey()));
			if (entry.getValue() > 1) {
				row = row.append(Component.literal(String.format(" (%d)", entry.getValue())).withStyle(ChatFormatting.GRAY));
			}
			out.add(row);
		}

		Collections.sort(out, (a, b) -> Integer.compare(b.getString().length(), a.getString().length()));

		this.entities = out;
	}


	public class EntityListLayer implements GuiLayer {
		private final EntityList mod;
		public EntityListLayer(EntityList mod) {
			this.mod = mod;
		}
		@Override
		public void render(GuiGraphicsExtractor gui, DeltaTracker deltaTracker) {
			if (this.mod.shouldHide()) return;

			int y = this.mod.getY();
			int x = this.mod.getX();
			int color = ARGB.opaque(Color.pack(Lang.NN(this.mod.color.get(), ChatFormatting.WHITE)));
			float scale = (float) this.mod.scale.getAsDouble();

			gui.pose().pushMatrix();
			gui.pose().scale(scale);

			for (Component row : mod.entities) {
				y = this.mod.drawString(
					gui,
					this.mod.prefixed(row),
					x,
					y,
					color
				);
			}

			gui.pose().popMatrix();
		}
	}
	
}
