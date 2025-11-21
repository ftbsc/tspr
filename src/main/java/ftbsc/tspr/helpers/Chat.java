package ftbsc.tspr.helpers;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;

public class Chat {
	public static void message(String msg, Object... args) {
		Chat.message(Style.EMPTY.withItalic(true).withColor(ChatFormatting.GRAY), msg, args);
	}

	public static void message(ChatFormatting color, String msg, Object... args) {
		Chat.message(Style.EMPTY.withItalic(true).withColor(color), msg, args);
	}

	public static void message(int color, String msg, Object... args) {
		Chat.message(Style.EMPTY.withItalic(true).withColor(color), msg, args);
	}

	public static void message(Style style, String msg, Object... args) {
		Minecraft.getInstance().gui.getChat().addMessage(
			Component.literal("")
				.append(Component.literal("$").withStyle(Style.EMPTY.withColor(ChatFormatting.DARK_GRAY).withObfuscated(true)))
				.append(Component.literal(" >> ").withColor(0xBF616A))
				.append(Component.literal(String.format(msg, args)).withStyle(style))
		);
	}
}
