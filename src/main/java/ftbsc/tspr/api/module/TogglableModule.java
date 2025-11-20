package ftbsc.tspr.api.module;

import org.lwjgl.glfw.GLFW;

import ftbsc.tspr.helpers.Chat;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.ModConfigSpec;

public abstract class TogglableModule extends BaseModule {

	protected ModConfigSpec.BooleanValue enabled;

	private static final KeyMapping.Category category = new KeyMapping.Category(ResourceLocation.parse("ftbsc:tspr.options.toggles"));

	public boolean isEnabled() {
		return this.enabled.getAsBoolean();
	}

	public boolean toggle() {
		boolean previous = this.enabled.getAsBoolean();
		this.enabled.set(!previous);
		Chat.message("%s %s", this.getName(), previous ? "disabled" : "enabled");
		return !previous;
	}

	public boolean setEnabled(boolean enabled) {
		boolean previous = this.enabled.getAsBoolean();
		if (previous != enabled) {
			this.enabled.set(enabled);
			Chat.message("%s %s", this.getName(), previous ? "disabled" : "enabled");
		}
		return previous;
	}

	private KeyMapping toggleKey = new KeyMapping(
		String.format("key.tspr.%s", this.getName().toLowerCase()),
		GLFW.GLFW_KEY_UNKNOWN,
		TogglableModule.category
	);

	public KeyMapping getToggleKey() {
		return this.toggleKey;
	}

	@Override
	public void prepareConfig(ModConfigSpec.Builder builder) {
		this.enabled = builder
			.comment(String.format("Should %s be enabled?", this.getName()))
			.define("enabled", false);

		super.prepareConfig(builder);
	}
}
