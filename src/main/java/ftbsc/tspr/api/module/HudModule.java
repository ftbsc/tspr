package ftbsc.tspr.api.module;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.util.Mth;
import net.neoforged.neoforge.client.gui.GuiLayer;
import net.neoforged.neoforge.common.ModConfigSpec;

public abstract class HudModule extends TogglableModule {

	public abstract GuiLayer getLayer();

	public enum Anchor {
		TOPLEFT,
		TOPCENTER,
		TOPRIGHT,
		MIDDLELEFT,
		MIDDLECENTER,
		MIDDLERIGHT,
		BOTTOMLEFT,
		BOTTOMCENTER,
		BOTTOMRIGHT,
	}

	protected ModConfigSpec.EnumValue<Anchor> anchor;
	protected ModConfigSpec.IntValue x;
	protected ModConfigSpec.IntValue y;
	protected ModConfigSpec.DoubleValue scale;

	@Override
	public void prepareConfig(ModConfigSpec.Builder builder) {
		this.anchor = builder
			.comment("anchor position on screen")
			.defineEnum("anchor", Anchor.TOPLEFT);

		this.x = builder
			.comment("x offset from anchor point")
			.defineInRange("x", 0, Integer.MIN_VALUE, Integer.MAX_VALUE);

		this.y = builder
			.comment("y offset from anchor point")
			.defineInRange("y", 0, Integer.MIN_VALUE, Integer.MAX_VALUE);

		this.scale = builder
			.comment("scale rendering by this factor")
			.defineInRange("scale", 1., 0., Double.MAX_VALUE);

		super.prepareConfig(builder);
	}

	protected int getX() { return this.getX(0); }
	protected int getX(int width) {
		int x = this.x.get();

		switch (this.anchor.get()) {
			case TOPLEFT:
			case MIDDLELEFT:
			case BOTTOMLEFT:
				break;

			case BOTTOMCENTER:
			case MIDDLECENTER:
			case TOPCENTER:
				x = (MC.getWindow().getGuiScaledWidth() / 2) + x - (width / 2);
				break;

			case TOPRIGHT:
			case MIDDLERIGHT:
			case BOTTOMRIGHT:
				x = MC.getWindow().getGuiScaledWidth() - x - width;
				break;
		}

		return Mth.floor((double) x / this.scale.get());
	}

	protected int getY() { return this.getY(0); }
	protected int getY(int height) {
		int y = this.y.get();

		switch (this.anchor.get()) {
			case TOPLEFT:
			case TOPCENTER:
			case TOPRIGHT:
				break;

			case MIDDLELEFT:
			case MIDDLECENTER:
			case MIDDLERIGHT:
				y = (MC.getWindow().getGuiScaledHeight() / 2) + y - (height / 2);
				break;

			case BOTTOMLEFT:
			case BOTTOMCENTER:
			case BOTTOMRIGHT:
				y = MC.getWindow().getGuiScaledHeight() - y - height;
				break;
		}

		return Mth.floor((double) y / this.scale.get());
	}

	protected int inc(int y, int val) {
		switch (this.anchor.get()) {
			case BOTTOMLEFT:
			case BOTTOMCENTER:
			case BOTTOMRIGHT:
				return y - val;

			// TODO: might be cool to get the total height and "center" it?
			case MIDDLELEFT:
			case MIDDLECENTER:
			case MIDDLERIGHT:
				return y + val;

			case TOPLEFT:
			case TOPCENTER:
			case TOPRIGHT:
			default:
				return y + val;
		}
	}

	protected Component prefixed(String text, Object... args) {
		return this.prefixed(Component.literal(String.format(text, args)));
	}

	protected Component prefixed(Component text) {
		switch (this.anchor.get()) {
			case TOPRIGHT:
			case MIDDLERIGHT:
			case BOTTOMRIGHT:
				return Component.literal("")
				.append(text)
				.append(Component.literal(" < ").withColor(0xBF616A))
				.append(Component.literal("$").withStyle(Style.EMPTY.withColor(ChatFormatting.DARK_GRAY).withObfuscated(true)));

			case BOTTOMCENTER:
			case MIDDLECENTER:
			case TOPCENTER:
				return Component.literal("")
				.append(Component.literal("$").withStyle(Style.EMPTY.withColor(ChatFormatting.DARK_GRAY).withObfuscated(true)))
				.append(Component.literal(" > ").withColor(0xBF616A))
				.append(text)
				.append(Component.literal(" < ").withColor(0xBF616A))
				.append(Component.literal("$").withStyle(Style.EMPTY.withColor(ChatFormatting.DARK_GRAY).withObfuscated(true)));

			case TOPLEFT:
			case MIDDLELEFT:
			case BOTTOMLEFT:
			default:
				return Component.literal("")
				.append(Component.literal("$").withStyle(Style.EMPTY.withColor(ChatFormatting.DARK_GRAY).withObfuscated(true)))
				.append(Component.literal(" > ").withColor(0xBF616A))
				.append(text);
		}
	}

	protected boolean shouldHide() {
		return !this.enabled.getAsBoolean() || MC.getDebugOverlay().showDebugScreen() || MC.options.hideGui;
	}
}
