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

	protected void prepareConfig(ModConfigSpec.Builder builder) {
		this.config(builder);
	}




	public final void buildConfig(ModConfigSpec.Builder builder) {
		this.pushConfig(builder);
		this.prepareConfig(builder);
		this.popConfig(builder);
	}

	private final void pushConfig(ModConfigSpec.Builder builder) {
		String category = this.getClass().getPackageName().replace("ftbsc.tspr.modules.", "");
		builder.push(category);
		builder.push(this.getName().toLowerCase());
	}

	private final void popConfig(ModConfigSpec.Builder builder) {
		builder.pop();
		builder.pop();
	}

	public String getName() {
		return this.getClass().getSimpleName();
	}
}
