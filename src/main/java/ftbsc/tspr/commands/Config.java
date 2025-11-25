package ftbsc.tspr.commands;

import java.util.stream.Collectors;

import com.google.auto.service.AutoService;
import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;

import ftbsc.tspr.Tiramisuper;
import ftbsc.tspr.api.ILoadable;
import ftbsc.tspr.api.command.BaseCommand;
import ftbsc.tspr.api.command.ConfigArgumentType;
import ftbsc.tspr.api.module.HudModule.Anchor;
import ftbsc.tspr.helpers.Chat;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

@AutoService(ILoadable.class)
public class Config extends BaseCommand {

	@Override
	public String getName() { return "cfg"; }

	@SuppressWarnings("unchecked")
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
								if (level.value == null) {
									throw new SimpleCommandExceptionType(new LiteralMessage("cannot set value of config container")).create();
								}
								ModConfigSpec.ConfigValue<?> cfg = level.value;
								Class<?> clazz = cfg.getDefault().getClass();
								// TODO this switch case is a bit disgusting, can we generalize??
								switch (clazz.getName()) {
									case "java.lang.String":
										ModConfigSpec.ConfigValue<String> arg_str = (ModConfigSpec.ConfigValue<String>) cfg;
										arg_str.set(newValue);
										break;
									case "java.lang.Boolean":
										ModConfigSpec.ConfigValue<Boolean> arg_bool = (ModConfigSpec.ConfigValue<Boolean>) cfg;
										arg_bool.set(Boolean.parseBoolean(newValue));
										break;
									case "java.lang.Double":
										ModConfigSpec.ConfigValue<Double> arg_double = (ModConfigSpec.ConfigValue<Double>) cfg;
										arg_double.set(Double.parseDouble(newValue));
										break;
									case "java.lang.Integer":
										ModConfigSpec.ConfigValue<Integer> arg_int = (ModConfigSpec.ConfigValue<Integer>) cfg;
										arg_int.set(Integer.parseInt(newValue));
										break;
									case "ftbsc.tspr.api.module.HudModule.Anchor":
										ModConfigSpec.ConfigValue<Anchor> arg_anchor = (ModConfigSpec.ConfigValue<Anchor>) cfg;
										arg_anchor.set(Anchor.valueOf(newValue));
										break;
									case "net.mincraft.ChatFormatting":
										ModConfigSpec.ConfigValue<ChatFormatting> arg_fmt = (ModConfigSpec.ConfigValue<ChatFormatting>) cfg;
										arg_fmt.set(ChatFormatting.valueOf(newValue));
										break;
									default:
										Chat.message("option of type '%s' is not yet settable via command", clazz);
										return 1;
								}
								cfg.save();
								String path = cfg.getPath().stream().collect(Collectors.joining("."));
								Chat.message("%s:= %s", path, cfg.get().toString());
								return 0;
							})
					)
			);
	}
}
