package ftbsc.tspr.modules.move;

import ftbsc.tspr.api.module.TogglableModule;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

import ftbsc.tspr.api.ILoadable;

import com.google.auto.service.AutoService;

import static ftbsc.tspr.Tiramisuper.mc;

@AutoService(ILoadable.class)
public class Walk extends TogglableModule {

	protected void config(ModConfigSpec.Builder builder) {}
	private boolean once = false;

	@SubscribeEvent
	void onTick(ClientTickEvent.Pre event) {
		if (mc().player == null) return;

		if (this.enabled.getAsBoolean()) {
			mc().options.keyUp.setDown(true);
			this.once = true;
		} else if (this.once) {
			mc().options.keyUp.setDown(false);
			this.once = false;
		}
	}

}
