package ftbsc.tspr.modules.interaction;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

import ftbsc.tspr.api.module.TogglableModule;
import ftbsc.tspr.Tiramisuper;
import ftbsc.tspr.api.ILoadable;

import com.google.auto.service.AutoService;

import static ftbsc.tspr.Tiramisuper.mc;

@AutoService(ILoadable.class)
public class AutoClick extends TogglableModule {

	private ModConfigSpec.IntValue interval;
	private ModConfigSpec.BooleanValue attack;
	private int counter = 0;

	protected void config(ModConfigSpec.Builder builder) {
		this.interval = builder
			.comment("How many game ticks between each click?")
			.defineInRange("interval", 50, 0, Integer.MAX_VALUE);

		this.attack = builder
			.comment("Is this an attack click? (otherwise will be use-click)")
			.define("attack", true);
	}

	@SubscribeEvent
	void onTick(ClientTickEvent.Pre event) {
		if (!this.enabled.getAsBoolean() || mc().player == null) {
			this.counter = this.interval.getAsInt();
			return;
		}

		if (this.counter > 0) {
			this.counter -= 1;
		}

		if (this.counter == 0) {
			if (this.attack.getAsBoolean()) {
				mc().options.keyAttack.setDown(true);
				Tiramisuper.SCHEDULER.schedule(1, () -> mc().options.keyAttack.setDown(false));
			} else {
				mc().options.keyUse.setDown(true);
				Tiramisuper.SCHEDULER.schedule(1, () -> mc().options.keyUse.setDown(false));
			}
			this.counter = this.interval.getAsInt();
		}
	}
}
