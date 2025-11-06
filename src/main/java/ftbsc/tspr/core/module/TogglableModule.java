package ftbsc.tspr.core.module;

import org.lwjgl.glfw.GLFW;

import net.minecraft.client.KeyMapping;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.common.ModConfigSpec;

public abstract class TogglableModule extends BaseModule {

	protected ModConfigSpec.BooleanValue enabled;

	public boolean isEnabled() {
		return this.enabled.getAsBoolean();
	}

	public boolean toggle() {
		boolean previous = this.enabled.getAsBoolean();
		this.enabled.set(!previous);
		MC.gui.getChat().addMessage(
			Component
				.literal(String.format(">> %s %s", this.name, previous ? "disabled" : "enabled"))
				.withColor(0xBF616A)
		);
		return !previous;
	}

	private KeyMapping toggleKey = new KeyMapping(
		String.format("key.ftbsc-tspr.%s", this.name.toLowerCase()),
		GLFW.GLFW_KEY_UNKNOWN,
		"key.categories.ftbsc-tspr.toggles"
	);

	public KeyMapping getToggleKey() {
		return this.toggleKey;
	}

	@Override
	public void prepareConfig(ModConfigSpec.Builder builder) {
		builder.push(this.name.toLowerCase());

		this.enabled = builder
			.comment(String.format("Should %s be enabled?", this.name))
			.define("enabled", false);

		this.config(builder);

		builder.pop();
	}

}
