package ftbsc.tspr.api.command;

import com.electronwill.nightconfig.core.Config;
import com.electronwill.nightconfig.core.UnmodifiableConfig;
import com.electronwill.nightconfig.core.UnmodifiableConfig.Entry;
import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.context.StringRange;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestion;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

import ftbsc.tspr.Tiramisuper;
import ftbsc.tspr.helpers.Chat;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.ModConfigSpec.ConfigValue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

/**
 * An {@link ArgumentType} which only accepts valid config paths, providing completions on each path step
 * @author alemi
 */
public class ConfigArgumentType implements ArgumentType<ConfigArgumentType.ConfigLevel> {
	private List<String> availableConfigs;

	/**
	 * Build a {@link ConfigArgumentType} from provided configuration specification ({@link ModConfigSpec})
	 */
	public static ConfigArgumentType of(ModConfigSpec spec) {
		ConfigArgumentType cfg = new ConfigArgumentType();
		cfg.availableConfigs = new ArrayList<>();
		ConfigArgumentType.recursiveConfigVisitor(spec.getValues(), value -> {
			cfg.availableConfigs.add(value.getPath().stream().collect(Collectors.joining(".")));
		});
		return cfg;
	}

	@Override
	public ConfigLevel parse(StringReader reader) throws CommandSyntaxException {
		Object thing = Tiramisuper.spec().getValues().get(reader.readString());
		if (thing instanceof Config container) {
			return new ConfigLevel(container, null);
		} else if (thing instanceof ConfigValue value) {
			return new ConfigLevel(null, value);
		} else {
			throw new SimpleCommandExceptionType(new LiteralMessage("no such config")).create();
		}
	}

	@Override
	public <S> CompletableFuture<Suggestions> listSuggestions(final CommandContext<S> context, final SuggestionsBuilder builder) {
		return CompletableFuture.supplyAsync(() -> {
			List<Suggestion> suggestions = this.availableConfigs.stream()
				.filter(k -> builder.getRemaining().isEmpty() || k.startsWith(builder.getRemaining()))
				.map(k -> ConfigArgumentType.splitForContext(k, builder.getRemaining()))
				.distinct()
				.map(k -> new Suggestion(
					StringRange.at(builder.getStart() + builder.getRemaining().length()),
					k.substring(builder.getRemaining().length()),
					new LiteralMessage(k)
				))
				.collect(Collectors.toList());

			return Suggestions.create("cfg", suggestions);
		});
	}

	@Override
	public Collection<String> getExamples() {
		return ConfigArgumentType.EXAMPLES;
	}


	/**
	 * Wrapper utility type for handling either a config Node or Container
	 */
	public static class ConfigLevel {
		/** Config container, if present */
		public final @Nullable Config container;
		/** Config node value, if present */
		public final @Nullable ConfigValue<?> value;

		/**
		 * Construct a {@link ConfigLevel} from either a Container ({@link Config}) or node ({@link ConfigValue})
		 */
		public ConfigLevel(@Nullable Config container, @Nullable ConfigValue<?> value) {
			this.container = container;
			this.value = value;
		}

		/**
		 * Pretty-prints to chat the current Config level: either a distinct node, or all
		 * childen nodes contained in given level
		 */
		public void print() {
			if (this.container != null) {
				ConfigArgumentType.recursiveConfigVisitor(this.container, value -> {
					Chat.message("%s: %s", String.join(".", value.getPath()), value.get().toString());
				});
			}

			if (this.value != null) {
				ConfigValue<?> v = this.value; // make null checker happy
				Chat.message("%s: %s", String.join(".", v.getPath()), v.get().toString());
			}
		}
	}

	private static List<String> EXAMPLES = Arrays.asList("category.mod.option");

	private static String splitForContext(String key, String input) {
		int dots = 0;
		for (char c : input.toCharArray()) {
			if (c == '.') dots++;
		}
		String[] fragments = key.split("\\.");
		switch (dots) {
			case 0: return fragments[0];
			case 1: return fragments[0] + "." + fragments[1];
			case 2: return fragments[0] + "." + fragments[1] + "." + fragments[2];
			default:
				Tiramisuper.LOGGER.error("failed context split: dots={} input={}", dots, input);
				return key;
		}
	}

	private static void recursiveConfigVisitor(UnmodifiableConfig container, Consumer<ConfigValue<?>> visitor) {
		for (Entry e : container.entrySet()) {
			if (e.getValue() instanceof ConfigValue value) {
				visitor.accept(value);
			} else if (e.getValue() instanceof Config innerContainer) {
				ConfigArgumentType.recursiveConfigVisitor(innerContainer, visitor);
			}
		}
	}
}

