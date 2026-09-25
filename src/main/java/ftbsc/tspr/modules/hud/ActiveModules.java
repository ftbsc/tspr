package ftbsc.tspr.modules.hud;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.google.auto.service.AutoService;

import ftbsc.tspr.Tiramisuper;
import ftbsc.tspr.api.ILoadable;
import ftbsc.tspr.api.module.HudModule;
import ftbsc.tspr.api.module.TogglableModule;
import ftbsc.tspr.helpers.Color;
import ftbsc.tspr.helpers.Lang;
import net.minecraft.ChatFormatting;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.util.ARGB;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.gui.GuiLayer;
import net.neoforged.neoforge.common.ModConfigSpec;

@AutoService(ILoadable.class)
public class ActiveModules extends HudModule {

	private ModConfigSpec.EnumValue<ChatFormatting> color;

	@Override
	public void config(ModConfigSpec.Builder builder) {
		this.color = builder
			.comment("color for drawing list")
			.defineEnum("color", ChatFormatting.WHITE);
	}

	private List<String> modList = new ArrayList<>();

	@SubscribeEvent
	void onTick(ClientTickEvent.Post event) {
		if (!this.isEnabled()) return;
		this.modList = Tiramisuper.mods().stream()
			.filter(m -> m instanceof TogglableModule)
			.filter(m -> !(m instanceof HudModule))
			.map(m -> (TogglableModule) m)
			.filter(m -> m.isEnabled())
			.map(m -> String.format("%s:%s", m.getCategory(), m.getName()))
			.sorted((a, b) -> Integer.compare(b.length(), a.length()))
			.collect(Collectors.toList());
	}

	@Override
	public GuiLayer getLayer() {
		return new ActiveModulesLayer(this);
	}

	private class ActiveModulesLayer implements GuiLayer {
		private ActiveModules mod;

		private ActiveModulesLayer(ActiveModules mod) {
			this.mod = mod;
		}

		@Override
		public void render(GuiGraphicsExtractor gui, DeltaTracker deltaTracker) {
			if (this.mod.shouldHide()) return;
			gui.pose().pushMatrix();
			gui.pose().scale((float) this.mod.scale.getAsDouble());
			int y = this.mod.getY();
			for (String row : this.mod.modList) {
				y = this.mod.drawString(
					gui,
					this.mod.prefixed(row),
					this.mod.getX(),
					y,
					ARGB.opaque(Color.pack(Lang.NN(this.mod.color.get(), ChatFormatting.WHITE)))
				);
			}
			gui.pose().popMatrix();
		}
	}
}
