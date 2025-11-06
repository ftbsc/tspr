package ftbsc.tspr.core.module;

import ftbsc.tspr.core.IGlobals;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.NeoForge;

public abstract class BaseModule implements IGlobals {

	/**
	 * define config arguments inside here, will be scoped automatically
	 * @param builder ModConfigSpec.Builder
	 */
	protected abstract void config(ModConfigSpec.Builder builder);

	protected final String name = this.getClass().getSimpleName();

	public void prepareConfig(ModConfigSpec.Builder builder) {
		builder.push(this.name.toLowerCase());

		this.config(builder);

		builder.pop();
	}

	public void register() {
		NeoForge.EVENT_BUS.register(this);
	}
}
