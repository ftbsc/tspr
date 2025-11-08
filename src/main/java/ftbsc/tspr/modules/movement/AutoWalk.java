package ftbsc.tspr.modules.movement;

import ftbsc.tspr.api.module.TogglableModule;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

import ftbsc.tspr.api.ILoadable;

import com.google.auto.service.AutoService;

@AutoService(ILoadable.class)
public class AutoWalk extends TogglableModule {

	protected void config(ModConfigSpec.Builder builder) {}
	private boolean once = false;

	@SubscribeEvent
	void onTick(ClientTickEvent.Pre event) {
		if (MC.player == null) return;

		if (this.enabled.getAsBoolean()) {
			MC.options.keyUp.setDown(true);
			this.once = true;
		} else if (this.once) {
			MC.options.keyUp.setDown(false);
			this.once = false;
		}
	}

}
