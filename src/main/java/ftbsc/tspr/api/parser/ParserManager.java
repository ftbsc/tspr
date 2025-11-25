package ftbsc.tspr.api.parser;

import ftbsc.tspr.api.parser.types.EnumParser;
import net.minecraft.ChatFormatting;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;

/**
 * Manages the various {@link IParser} instances.
 */
public class ParserManager {
	private static ParserManager instance = null;

	/**
	 * Gets or creates the instance of the {@link ParserManager} singleton.
	 * @return the instance
	 */
	public static ParserManager getInstance() {
		if(instance == null) {
			instance = new ParserManager();
		}

		return instance;
	}

	private final Map<Class<?>, IParser<?>> parsers = new HashMap<>();

	private ParserManager() {
		for(IParser<?> parser : ServiceLoader.load(IParser.class)) {
			this.parsers.put(parser.type(), parser);
		}
	}

	/**
	 * Checks whether a given type is supported.
	 * @param type the {@link Class} to check
	 * @return whether the type is supported
	 */
	public boolean has(Class<?> type) {
		return this.parsers.containsKey(type);
	}

	/**
	 * Registers the given parser.
	 * @param parser the parser to register
	 */
	public void register(IParser<?> parser) {
		this.parsers.put(parser.type(), parser);
	}

	/**
	 * Gets the parser associated with a certain class.
	 * @param type the class
	 * @return the appropriate parser, or null if it wasn't found
	 * @param <T> the type supported by the parser
	 */
	@SuppressWarnings("unchecked") // it's not even actually unsafe
	public <T> @Nullable IParser<T> get(Class<T> type) {
		if(!this.has(type) && Enum.class.isAssignableFrom(type)) { // enums are always known!
			this.register(new EnumParser<>(type));
		}

		return (IParser<T>) this.parsers.get(type);
	}
}
