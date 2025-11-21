package ftbsc.tspr.modules.client;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

import ftbsc.tspr.asm.events.ChatClearEvent;
import ftbsc.tspr.asm.events.ChatMessageEvent;
import ftbsc.tspr.api.module.BaseModule;
import ftbsc.tspr.api.ILoadable;

import com.google.auto.service.AutoService;

import static ftbsc.tspr.Tiramisuper.mc;

@AutoService(ILoadable.class)
public class ChatTweaks extends BaseModule {

	private ModConfigSpec.BooleanValue timestamps;
	private ModConfigSpec.BooleanValue keepPrevious;

	public void config(ModConfigSpec.Builder builder) {
		this.timestamps = builder
			.comment("add timestamps to chat")
			.define("timestamps", true);
		this.keepPrevious = builder
			.comment("prevent chat log from being cleared")
			.define("keepPrevious", true);
	}

	@SubscribeEvent
	void onChatMessage(ChatMessageEvent event) {
		if (this.timestamps.getAsBoolean()) {
			DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
			LocalTime localTime = LocalTime.now();
			String timestamp = String.format("%s | ", dtf.format(localTime));
			event.message = Component.literal("")
				.append(Component.literal(timestamp).withStyle(ChatFormatting.DARK_GRAY))
				.append(event.message);
		}
	}

	@SubscribeEvent
	void onChatClear(ChatClearEvent event) {
		if (this.keepPrevious.getAsBoolean()) {
			event.setCanceled(true);
			mc().gui.getChat().addMessage(
				Component.literal("----------").withStyle(ChatFormatting.DARK_GRAY)
			);
		}
	}
}
