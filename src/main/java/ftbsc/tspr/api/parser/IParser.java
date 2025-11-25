package ftbsc.tspr.api.parser;

import net.neoforged.neoforge.common.ModConfigSpec;
import org.jetbrains.annotations.Nullable;

/**
 * A parser that can turn strings into other types.
 * @param <T> the type this supports
 */
public interface IParser<T> {
	/**
	 * @return a {@link Class} representing the type supported by this
	 */
	Class<T> type();

	/**
	 * Parse the given string.
	 * @param in the input string
	 * @return the parsed element (null if it failed)
	 */
	@Nullable T parse(String in);

	/**
	 * Parse and write the result into the given config value, only if it's valid according to the spec.
	 * @param cfgValue the config value to write in
	 * @param in the input to parse
	 */
	@SuppressWarnings("unchecked") // safe!
	default void writeConfig(ModConfigSpec.ConfigValue<?> cfgValue, String in) {
		T result = this.parse(in);
		if (result != null && cfgValue.getSpec().test(result)) {
			((ModConfigSpec.ConfigValue<T>) cfgValue).set(result);
		}
	}
}
