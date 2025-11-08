package ftbsc.tspr.modules.player;

import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.minecraft.client.gui.screens.DisconnectedScreen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;
import net.minecraft.network.chat.Component;
import net.neoforged.bus.api.SubscribeEvent;

import ftbsc.tspr.api.module.TogglableModule;
import ftbsc.tspr.api.ILoadable;

import com.google.auto.service.AutoService;

@AutoService(ILoadable.class)
public class AutoDisconnect extends TogglableModule {

	private ModConfigSpec.DoubleValue threshold;

	protected void config(ModConfigSpec.Builder builder) {
		this.threshold = builder
			.comment("Health limit for disconnecting (in half hearths)")
			.defineInRange("threshold", 5., 0., 20.);
	}

	// TODO once packet patch is implemented, do this on health update packet
	@SubscribeEvent
	void onHealthChange(ClientTickEvent.Pre event) {
		if (!this.enabled.getAsBoolean()) return;
		if (MC.player == null) return;


		if (MC.player.getHealth() <= this.threshold.getAsDouble()) {
			String reason = String.format("Health dropped below %.1f hearths", this.threshold.getAsDouble());
			MC.level.disconnect(Component.literal(reason));
			MC.disconnect(new AutoDisconnectScreen(reason), true);
			this.enabled.set(false);
		}
	}

	private class AutoDisconnectScreen extends DisconnectedScreen {
		AutoDisconnectScreen(String message) {
			super(
				new JoinMultiplayerScreen(new TitleScreen()),
				Component.literal("Auto-Disconnect triggered"),
				Component.literal(message),
				Component.literal("Back to Server Selection")
			);
		}
	}
}

