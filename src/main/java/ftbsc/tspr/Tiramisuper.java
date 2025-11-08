package ftbsc.tspr;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.mojang.logging.LogUtils;

import ftbsc.tspr.api.ILoadable;
import ftbsc.tspr.api.command.BaseCommand;
import ftbsc.tspr.helpers.Scheduler;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.CommandSourceStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.ModContainer;
import net.neoforged.neoforge.client.ClientCommandHandler;
import net.neoforged.neoforge.client.event.ClientChatEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterClientCommandsEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.NeoForge;
import ftbsc.tspr.api.module.BaseModule;
import ftbsc.tspr.api.module.TogglableModule;

@Mod(value = Tiramisuper.MODID, dist = Dist.CLIENT)
public class Tiramisuper {
	public static final String MODID = "tspr";
	public static final Logger LOGGER = LogUtils.getLogger();
	public static final Scheduler SCHEDULER = new Scheduler();

	private final List<BaseModule> modules = new ArrayList<>();
	private final List<BaseCommand> commands = new ArrayList<>();

	private final KeyMapping optionsKey = new KeyMapping(
		"key.tspr.showOptions",
		GLFW.GLFW_KEY_UNKNOWN,
		"key.categories.tspr.global"
	);

	private final ModContainer modContainer;
	private final CommandDispatcher<CommandSourceStack> dispatcher;

	private static Tiramisuper INSTANCE;

	public Tiramisuper(IEventBus modEventBus, ModContainer modContainer) {
		Tiramisuper.INSTANCE = this;
		this.modContainer = modContainer;

		for (ILoadable loadable : ServiceLoader.load(ILoadable.class)) {
			if (loadable instanceof BaseModule) {
				this.modules.add((BaseModule) loadable);
			} else if (loadable instanceof BaseCommand) {
				this.commands.add((BaseCommand) loadable);
			} else {
				LOGGER.warn("unexpected ILoadable : {}", loadable.getName());
			}
		}

		this.dispatcher = new CommandDispatcher<>();

		ModConfigSpec.Builder builder = new ModConfigSpec.Builder();
		for (BaseModule mod : this.modules) {
			mod.prepareConfig(builder);
		}

		ModConfigSpec spec = builder.build();
		modContainer.registerConfig(ModConfig.Type.COMMON, spec, "tspr.toml");
		modContainer.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);

		NeoForge.EVENT_BUS.register(this);
		NeoForge.EVENT_BUS.register(Tiramisuper.SCHEDULER);

		for (BaseModule mod : this.modules) {
			NeoForge.EVENT_BUS.register(mod);
		}
	}

	public static List<BaseModule> mods() {
		return Tiramisuper.INSTANCE.modules;
	}

	@SubscribeEvent
	public void onCommandSuggestionsBuilt(RegisterClientCommandsEvent event) {
		for (BaseCommand cmd : this.commands) {
			LiteralCommandNode<CommandSourceStack> node = cmd.build(event.getBuildContext());
			this.dispatcher.getRoot().addChild(node);
			event.getDispatcher().getRoot().addChild(node);
		}
	}

	@SubscribeEvent
	void onClientChatEvent(ClientChatEvent event) {
		if (event.getMessage().startsWith("/")) {
			CommandSourceStack source = ClientCommandHandler.getSource();
			try {
				LOGGER.info("Running command {}", event.getMessage());
				this.dispatcher.execute(event.getMessage().substring(1), source);
				Minecraft.getInstance().gui.getChat().addRecentChat(event.getMessage());
				event.setCanceled(true);
			} catch (CommandSyntaxException e) {
				LOGGER.error("Syntax error in command: {}", e.toString());
			}
		}
	}

	@SubscribeEvent
	void handleGlobalKeys(ClientTickEvent.Post event) {
		boolean showOptions = false;
		while (this.optionsKey.consumeClick()) {
			// debounce
			showOptions = true;
		}

		if (showOptions) {
			IConfigScreenFactory.getForMod(this.modContainer.getModInfo())
				.map(f -> f.createScreen(this.modContainer, null))
				.ifPresent(s ->Minecraft.getInstance().setScreen(s));
		}
	}

	@SubscribeEvent
	void handleToggleKeys(ClientTickEvent.Post event) {
		for (BaseModule mod : this.modules) {
			// TODO can we avoid checking all mods, base or not?
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

	@EventBusSubscriber(modid = Tiramisuper.MODID, value = Dist.CLIENT)
	private static class LifecycleHandler {
		@SubscribeEvent
		static void onRegisterKeyMappings(RegisterKeyMappingsEvent event) {
			event.register(Tiramisuper.INSTANCE.optionsKey);

			for (BaseModule mod : Tiramisuper.INSTANCE.modules) {
				if (mod instanceof TogglableModule) {
					TogglableModule toggleMod = (TogglableModule) mod;
					event.register(toggleMod.getToggleKey());
				}
			}
		}
	}
}
