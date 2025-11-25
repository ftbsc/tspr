package ftbsc.tspr.commands;

import com.google.auto.service.AutoService;
import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;

import ftbsc.tspr.Tiramisuper;
import ftbsc.tspr.api.ILoadable;
import ftbsc.tspr.api.command.BaseCommand;
import ftbsc.tspr.api.command.ConfigArgumentType;
import ftbsc.tspr.api.command.parser.ArgParser;
import ftbsc.tspr.api.command.parser.IParser;
import ftbsc.tspr.helpers.Chat;
import net.neoforged.neoforge.common.ModConfigSpec.ConfigValue;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

/**
 * Command to set any {@link ConfigValue}, will offer suggestions for various mod paths
 * @author alemi
 */
@AutoService(ILoadable.class)
public class Config extends BaseCommand {

	@Override
	public String getName() { return "cfg"; }

	@Override
	public LiteralArgumentBuilder<CommandSourceStack> command(LiteralArgumentBuilder<CommandSourceStack> builder, CommandBuildContext context) {
		return builder
			.then(
				Commands.argument("path", ConfigArgumentType.of(Tiramisuper.spec()))
					.executes(ctx -> {
						ctx.getArgument("path", ConfigArgumentType.ConfigLevel.class).print();
						return 0;
					})
					.then(
						Commands.argument("value", StringArgumentType.greedyString())
							.executes(ctx -> {
								String newValue = ctx.getArgument("value", String.class);
								ConfigArgumentType.ConfigLevel level = ctx.getArgument("path", ConfigArgumentType.ConfigLevel.class);

								if (level.value != null) {
									ConfigValue<?> cfg = level.value;
									Class<?> clazz = cfg.getDefault().getClass();
									IParser<?> parser = ArgParser.get(clazz);
									if (parser == null) {
										Chat.message("option of type '%s' is not yet settable via command", clazz);
										return 1;
									}

									parser.writeConfig(level.value, newValue);
									cfg.save();
									Chat.message("%s:= %s", String.join(".", cfg.getPath()), cfg.get().toString());

									return 0;
								}

								throw new SimpleCommandExceptionType(new LiteralMessage("cannot set value of config container")).create();
							})
					)
			);
	}
}
