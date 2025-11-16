package ftbsc.tspr.modules.hud;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.google.auto.service.AutoService;

import ftbsc.tspr.Tiramisuper;
import ftbsc.tspr.api.ILoadable;
import ftbsc.tspr.api.module.HudModule;
import ftbsc.tspr.api.module.TogglableModule;
import net.minecraft.ChatFormatting;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.util.ARGB;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.gui.GuiLayer;
import net.neoforged.neoforge.common.ModConfigSpec;

@AutoService(ILoadable.class)
public class ActiveModules extends HudModule {

	private ModConfigSpec.EnumValue<ChatFormatting> color;

	public void config(ModConfigSpec.Builder builder) {
		this.color = builder
			.comment("color for drawing list")
			.defineEnum("color", ChatFormatting.WHITE);
	}

	private List<Component> modList = new ArrayList<>();

	@SubscribeEvent
	void onTick(ClientTickEvent.Post event) {
		if (!this.isEnabled()) return;
		this.modList = Tiramisuper.mods().stream()
			.filter(m -> m instanceof TogglableModule)
			.filter(m -> !(m instanceof HudModule))
			.map(m -> (TogglableModule) m)
			.filter(m -> m.isEnabled())
			.map(m -> m.getName())
			.sorted((a, b) -> Integer.compare(b.length(), a.length()))
			.map(n ->	Component.literal("")
				.append(Component.literal("$").withStyle(Style.EMPTY.withColor(ChatFormatting.DARK_GRAY).withObfuscated(true)))
				.append(Component.literal(" >> ").withColor(0xBF616A))
				.append(Component.literal(n))
			)
			.collect(Collectors.toList());
	}

	public GuiLayer getLayer() {
		return new ActiveModulesLayer(this);
	}

	private class ActiveModulesLayer implements GuiLayer {
		private ActiveModules mod;

		private ActiveModulesLayer(ActiveModules mod) {
			this.mod = mod;
		}

		@Override
		public void render(GuiGraphics gui, DeltaTracker deltaTracker) {
			if (!this.mod.isEnabled()) return;
			int y = this.mod.getY();
			for (Component row : this.mod.modList) {
				gui.drawString(
					MC.font,
					row,
					this.mod.getX(),
					y,
					ARGB.opaque(this.mod.color.get().getColor())
				);
				y += MC.font.lineHeight + 1;
			}
		}
	}
}
