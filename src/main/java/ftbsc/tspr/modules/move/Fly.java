package ftbsc.tspr.modules.move;

import ftbsc.tspr.api.ILoadable;
import ftbsc.tspr.api.module.TogglableModule;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

import com.google.auto.service.AutoService;

import static ftbsc.tspr.Tiramisuper.mc;

/**
 * Make player able to fly, with various means
 */
@AutoService(ILoadable.class)
public class Fly extends TogglableModule {

	private ModConfigSpec.DoubleValue speed;
	private ModConfigSpec.DoubleValue downpush;
	private ModConfigSpec.IntValue interval;

	@Override
	protected void config(ModConfigSpec.Builder builder) {
		this.speed = builder
			.comment("how fast to fly")
			.defineInRange("speed", 0.1, 0., Double.MAX_VALUE);
		this.downpush = builder
			.comment("how much to push player down to prevent server kick")
			.defineInRange("downpush", 0.1, 0., Double.MAX_VALUE);
		this.interval = builder
			.comment("how often (in ticks) to push player down to prevent kicking")
			.defineInRange("interval", 60, 1, Integer.MAX_VALUE);
	}

	private boolean once = false;
	private int tick = 0;

	@SubscribeEvent
	void onTick(ClientTickEvent.Pre _event) {
		var player = mc().player;
		if (player == null) return;

		if (!this.enabled.get()) {
			if (this.once) {
				this.once = false;
				this.tick = 0;
				player.getAbilities().flying = false;
			}
			return;
		}

		player.getAbilities().flying = true;
		player.getAbilities().setFlyingSpeed(this.speed.get().floatValue());

		this.once = true;
		this.tick += 1;

		if (this.tick % this.interval.get() == 0) {
			player.setDeltaMovement(0., -this.downpush.getAsDouble(), 0.);
			this.tick = 0;
		}
	}
}
