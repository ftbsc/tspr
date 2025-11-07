package ftbsc.tspr.modules.client;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import ftbsc.tspr.asm.events.ChatMessageEvent;
import ftbsc.tspr.core.module.BaseModule;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.contents.PlainTextContents;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

public class ChatTweaks extends BaseModule {

	private ModConfigSpec.BooleanValue timestamps;

	public void config(ModConfigSpec.Builder builder) {
		this.timestamps = builder
			.comment("add timestamps to chat")
			.define("timestamps", true);
	}

	@SubscribeEvent
	void onChatMessage(ChatMessageEvent event) {
		if (this.timestamps.getAsBoolean()) {
			DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
			LocalTime localTime = LocalTime.now();
			String timestamp = String.format("%s | ", dtf.format(localTime));
			event.message = MutableComponent.create(new PlainTextContents.LiteralContents(timestamp))
				.setStyle(Style.EMPTY.withColor(0x555555))
				.append(
					MutableComponent.create(new PlainTextContents.LiteralContents(""))
						.setStyle(Style.EMPTY.withColor(0xFFFFFF))
				)
				.append(event.message);
		}
	}
}
