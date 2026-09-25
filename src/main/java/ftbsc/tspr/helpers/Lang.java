package ftbsc.tspr.helpers;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * static utilities helping with Java lang shortcomings
 */
public final class Lang {
	/**
	 * Equivalent of ?? operator in other languages.
	 * Returns the second, default value if first one is null
	 * @param obj nullable thing
	 * @param def non-null default
	 */
	public static <T> T NN(@Nullable T obj, @Nonnull T def) {
		if (obj != null) return obj;
		return def;
	}
}


