package ftbsc.tspr.api.module;

import ftbsc.tspr.api.IGlobals;
import ftbsc.tspr.api.ILoadable;
import net.neoforged.neoforge.common.ModConfigSpec;

public abstract class BaseModule implements IGlobals, ILoadable {

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

	public String getName() {
		return this.name;
	}
}
