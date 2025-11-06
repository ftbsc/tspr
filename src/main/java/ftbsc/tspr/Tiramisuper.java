package ftbsc.tspr;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.NeoForge;

import ftbsc.tspr.core.Scheduler;
import ftbsc.tspr.core.module.BaseModule;
import ftbsc.tspr.core.module.TogglableModule;
// import ftbsc.tspr.modules.interaction.AutoClick;
import ftbsc.tspr.modules.movement.AutoWalk;
import ftbsc.tspr.modules.player.AutoDisconnect;
import ftbsc.tspr.modules.player.AutoFish;
import ftbsc.tspr.modules.client.ChatTweaks;

@Mod(value = Tiramisuper.MODID, dist = Dist.CLIENT)
@EventBusSubscriber(modid = Tiramisuper.MODID, value = Dist.CLIENT)
public class Tiramisuper {
	public static final String MODID = "tspr";
	public static final Logger LOGGER = LogUtils.getLogger();
	public static final Scheduler SCHEDULER = new Scheduler();

	// TODO do this as a dynamic service loader so we don't need to register modules here by hand
	private static final BaseModule[] MODULES = {
		new AutoFish(),
		new AutoDisconnect(),
		new AutoWalk(),
		new ChatTweaks(),
		// new AutoClick(), // TODO doesn't work
	};

	public Tiramisuper(IEventBus modEventBus, ModContainer modContainer) {
		ModConfigSpec.Builder builder = new ModConfigSpec.Builder();

		for (BaseModule mod : Tiramisuper.MODULES) {
			mod.prepareConfig(builder);
		}

		ModConfigSpec spec = builder.build();
		modContainer.registerConfig(ModConfig.Type.COMMON, spec, "tspr.toml");
		modContainer.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);

		for (BaseModule mod : Tiramisuper.MODULES) {
			mod.register();
		}
		NeoForge.EVENT_BUS.register(Tiramisuper.SCHEDULER);
	}

	@SubscribeEvent
	static void onClientSetup(FMLClientSetupEvent event) {
		Tiramisuper.LOGGER.info("Tiramisuper loading >> {}", Minecraft.getInstance().getUser().getName());
	}

	@SubscribeEvent
	static void onRegisterKeyMappings(RegisterKeyMappingsEvent event) {
		for (BaseModule mod : Tiramisuper.MODULES) {
			// TODO is there a nicer way?
			if (mod instanceof TogglableModule) {
				TogglableModule toggleMod = (TogglableModule) mod;
				event.register(toggleMod.getToggleKey());
			}
		}
	}

	@SubscribeEvent
	static void handleToggleKeys(ClientTickEvent.Post event) {
		for (BaseModule mod : Tiramisuper.MODULES) {
			// TODO is there a nicer way?
			if (mod instanceof TogglableModule) {
				TogglableModule toggleMod = (TogglableModule) mod;
				boolean toggle = false;
				while (toggleMod.getToggleKey().consumeClick()) {
					// debounce
					toggle = true;
				}
				if (toggle) {
					toggleMod.toggle();
				}
			}
		}
	}
}
