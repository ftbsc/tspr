package ftbsc.tspr.api.parser.types;

import ftbsc.tspr.api.parser.IParser;

import java.util.Arrays;
import java.util.Comparator;

/**
 * An {@link IParser} for enum types.
 * @param <E> the Enum type this represents
 * @author zaaarf
 */
public record EnumParser<E>(Class<E> type) implements IParser<E> {
	@Override
	public E parse(String input) {
		return Arrays.stream(this.type().getEnumConstants())
			.filter(e -> ((Enum<?>) e).name().toLowerCase().startsWith(input.toLowerCase()))
			.min(Comparator.comparing(e -> ((Enum<?>) e).name().length())) // shortest one is the best match
			.orElse(null);
	}
}
