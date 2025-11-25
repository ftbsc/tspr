package ftbsc.tspr.modules.vision;

import static ftbsc.tspr.Tiramisuper.mc;

import com.google.auto.service.AutoService;

import ftbsc.tspr.api.ILoadable;
import ftbsc.tspr.api.module.TogglableModule;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

@AutoService(ILoadable.class)
public class Bright extends TogglableModule {
	
	public void config(ModConfigSpec.Builder builder) {}

	private boolean once = false;

	@SubscribeEvent
	void onTick(ClientTickEvent.Pre event) {
		if (!this.isEnabled()) {
			if (this.once) {
				mc().player.removeEffect(MobEffects.NIGHT_VISION);
				this.once = false;
			}
			return;
		}
		mc().player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 15840));
		this.once = true;
	}
}
