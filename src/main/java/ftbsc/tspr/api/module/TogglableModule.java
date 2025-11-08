package ftbsc.tspr.api.module;

import org.lwjgl.glfw.GLFW;

import ftbsc.tspr.helpers.Chat;
import net.minecraft.client.KeyMapping;
import net.neoforged.neoforge.common.ModConfigSpec;

public abstract class TogglableModule extends BaseModule {

	protected ModConfigSpec.BooleanValue enabled;

	public boolean isEnabled() {
		return this.enabled.getAsBoolean();
	}

	public boolean toggle() {
		boolean previous = this.enabled.getAsBoolean();
		this.enabled.set(!previous);
		Chat.message("%s %s", this.name, previous ? "disabled" : "enabled");
		return !previous;
	}

	private KeyMapping toggleKey = new KeyMapping(
		String.format("key.tspr.%s", this.name.toLowerCase()),
		GLFW.GLFW_KEY_UNKNOWN,
		"key.categories.tspr.toggles"
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
