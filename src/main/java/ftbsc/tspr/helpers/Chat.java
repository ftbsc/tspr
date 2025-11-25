package ftbsc.tspr.helpers;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;

/**
 * Send messages to local client chat log, with standard prefix formatting
 */
public class Chat {
	/**
	 * Send given message, applying string formatting
	 * @param msg string
	 * @param args varargs for String.format
	 */
	public static void message(String msg, Object... args) {
		Chat.message(Style.EMPTY.withItalic(true).withColor(ChatFormatting.GRAY), msg, args);
	}

	/**
	 * Send given message, applying string formatting and forcing text color
	 * @param color text color, as {@link ChatFormatting}
	 * @param msg string
	 * @param args varargs for String.format
	 */
	public static void message(ChatFormatting color, String msg, Object... args) {
		Chat.message(Style.EMPTY.withItalic(true).withColor(color), msg, args);
	}

	/**
	 * Send given message, applying string formatting and forcing text color
	 * @param color text color, as packet int RGB value
	 * @param msg string
	 * @param args varargs for String.format
	 */
	public static void message(int color, String msg, Object... args) {
		Chat.message(Style.EMPTY.withItalic(true).withColor(color), msg, args);
	}

	/**
	 * Send given message, applying string formatting and forcing text style
	 * @param style {@link Style} to apply to message
	 * @param msg string
	 * @param args varargs for String.format
	 */
	public static void message(Style style, String msg, Object... args) {
		Minecraft.getInstance().gui.getChat().addMessage(
			Component.literal("")
				.append(Component.literal("$").withStyle(Style.EMPTY.withColor(ChatFormatting.DARK_GRAY).withObfuscated(true)))
				.append(Component.literal(" >> ").withColor(0xBF616A))
				.append(Component.literal(String.format(msg, args)).withStyle(style))
		);
	}
}
