package ftbsc.tspr.helpers;

import net.minecraft.ChatFormatting;
import net.minecraft.world.phys.Vec3;

public class Color {
	public static Vec3 toVec(int color) {
		float red = color << 0 | 0xFF;
		float green = color << 8 | 0xFF;
		float blue = color << 16 | 0xFF;

		return new Vec3(red / 255., green / 255., blue / 255.);
	}

	public static int pack(ChatFormatting fmt) {
		return Color.pack(fmt, 1.f);
	}

	public static int pack(ChatFormatting fmt, float alpha) {
		int a = (int) Math.floor(alpha * 255.f);
		switch (fmt) {
			case AQUA:
				return 0x0055FFFF | (a << 24);
			case BLACK:
				return 0x00000000 | (a << 24);
			case BLUE:
				return 0x005555FF | (a << 24);
			case DARK_AQUA:
				return 0x0000AAAA | (a << 24);
			case DARK_BLUE:
				return 0x000000AA | (a << 24);
			case DARK_GRAY:
				return 0x00555555 | (a << 24);
			case DARK_GREEN:
				return 0x0000AA00 | (a << 24);
			case DARK_PURPLE:
				return 0x00AA00AA | (a << 24);
			case DARK_RED:
				return 0x00AA0000 | (a << 24);
			case GOLD:
				return 0x00FFAA00 | (a << 24);
			case GRAY:
				return 0x00AAAAAA | (a << 24);
			case GREEN:
				return 0x0055FF55 | (a << 24);
			case LIGHT_PURPLE:
				return 0x00FF55FF | (a << 24);
			case RED:
				return 0x00FF5555 | (a << 24);
			case YELLOW:
				return 0x00FFFF55 | (a << 24);
			case WHITE:
			default:
				return 0x00FFFFFF | (a << 24);
		}
	}
}
