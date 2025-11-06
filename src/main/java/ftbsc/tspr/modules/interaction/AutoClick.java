package ftbsc.tspr.modules.interaction;

import ftbsc.tspr.core.module.TogglableModule;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

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
		if (!this.enabled.getAsBoolean() || MC.player == null) {
			this.counter = this.interval.getAsInt();
			return;
		}

		if (this.counter > 0) {
			this.counter -= 1;
		}

		if (this.counter == 0) {
			if (this.attack.getAsBoolean()) {
				MC.options.keyAttack.setDown(true);
			} else {
				MC.options.keyUse.setDown(true);
			}
			this.counter = this.interval.getAsInt();
		}
	}
}
