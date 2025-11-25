package ftbsc.tspr.api.command.parser;

import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;

/**
 * Container for the various {@link IParser} instances.
 */
public class ArgParser {

	private static final Map<Class<?>, IParser<?>> parsers = Map.ofEntries(
		Map.entry(Boolean.class, new BooleanParser()),
		Map.entry(Integer.class, new IntegerParser()),
		Map.entry(Double.class,  new DoubleParser()),
		Map.entry(String.class,  new StringParser())
	);

	/**
	 * Gets the parser associated with a certain class.
	 * @param type the class
	 * @return the appropriate parser, or null if it wasn't found
	 * @param <T> the type supported by the parser
	 */
	@SuppressWarnings("unchecked") // it's not even actually unsafe
	public static <T> @Nullable IParser<T> get(Class<T> type) {
		if(!ArgParser.parsers.containsKey(type) && Enum.class.isAssignableFrom(type)) { // enums are always known!
			ArgParser.parsers.put(type, new EnumParser<>(type));
		}

		return (IParser<T>) ArgParser.parsers.get(type);
	}




	/**
	 * An {@link IParser} for {@link Boolean} types.
	 * Also accepts "1" or "t" as true
	 * @author zaaarf
	 */
	public static class BooleanParser implements IParser<Boolean> {
		@Override
		public Class<Boolean> type() {
			return Boolean.class;
		}
	
		@Override
		public Boolean parse(String in) {
			if (in.equals("1")) return true;
			if (in.toLowerCase().equals("t")) return true;
			return Boolean.parseBoolean(in);
		}
	}

	/**
	 * An {@link IParser} for {@link String} types.
	 * @author zaaarf
	 */
	public static class StringParser implements IParser<String> {
		@Override
		public Class<String> type() {
			return String.class;
		}
	
		@Override
		public String parse(String in) {
			return in;
		}
	}

	/**
	 * An {@link IParser} for {@link Double} types.
	 * @author zaaarf
	 */
	public static class DoubleParser implements IParser<Double> {
		@Override
		public Class<Double> type() {
			return Double.class;
		}
	
		@Override
		public Double parse(String in) {
			return Double.parseDouble(in);
		}
	}

	/**
	 * An {@link IParser} for {@link Integer} types.
	 * @author zaaarf
	 */
	public static class IntegerParser implements IParser<Integer> {
		@Override
		public Class<Integer> type() {
			return Integer.class;
		}
	
		@Override
		public Integer parse(String in) {
			return Integer.parseInt(in);
		}
	}

	/**
	 * An {@link IParser} for enum types.
	 * @param <E> the Enum type this represents
	 * @author zaaarf
	 */
	public static record EnumParser<E>(Class<E> type) implements IParser<E> {
		@Override
		public E parse(String input) {
			return Arrays.stream(this.type().getEnumConstants())
				.filter(e -> ((Enum<?>) e).name().toLowerCase().startsWith(input.toLowerCase()))
				.min(Comparator.comparing(e -> ((Enum<?>) e).name().length())) // shortest one is the best match
				.orElse(null);
		}
	}
}
