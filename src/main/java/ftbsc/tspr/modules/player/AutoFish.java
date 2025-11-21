package ftbsc.tspr.modules.player;

import net.neoforged.neoforge.client.event.sound.PlaySoundSourceEvent;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.minecraft.client.resources.sounds.Sound;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.FishingRodItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.SubscribeEvent;
import ftbsc.tspr.Tiramisuper;
import ftbsc.tspr.api.ILoadable;
import ftbsc.tspr.api.module.TogglableModule;

import com.google.auto.service.AutoService;

import static ftbsc.tspr.Tiramisuper.mc;

@AutoService(ILoadable.class)
public class AutoFish extends TogglableModule {

	public ModConfigSpec.BooleanValue recast;
	public ModConfigSpec.IntValue delay;

	protected void config(ModConfigSpec.Builder builder) {
		this.recast = builder
			.comment("Should auto-fish also recast rod?")
			.define("recast", true);
		this.delay = builder
			.comment("How long should auto fish wait before casting rod again (in game ticks)?")
			.defineInRange("delay", 10, 0, Integer.MAX_VALUE);
	}

	@SubscribeEvent
	void onSoundEffect(PlaySoundSourceEvent event) {
		if (!this.enabled.getAsBoolean()) return;
		if (mc().player == null) return;

		SoundInstance soundInstance = event.getSound();
		if (soundInstance == null) return;

		Sound sound = soundInstance.getSound();
		if (sound == null) return;

		Item held = mc().player.getMainHandItem().getItem();

		// TODO is this double check really necessary?
		boolean holdingRod = held == Items.FISHING_ROD || held instanceof FishingRodItem;

		if (!holdingRod) return;

		if (sound.getLocation().equals(ResourceLocation.withDefaultNamespace("random/splash"))) {
			mc().gameMode.useItem(mc().player, InteractionHand.MAIN_HAND);
			mc().player.swing(InteractionHand.MAIN_HAND);

			if (this.recast.getAsBoolean()) {
				Tiramisuper.SCHEDULER.schedule(this.delay.getAsInt(), () -> {
					mc().gameMode.useItem(mc().player, InteractionHand.MAIN_HAND);
					mc().player.swing(InteractionHand.MAIN_HAND);
				});
			}
		}
	}
}

