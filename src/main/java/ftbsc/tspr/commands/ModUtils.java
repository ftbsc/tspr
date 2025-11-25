package ftbsc.tspr.commands;

import com.google.auto.service.AutoService;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import ftbsc.tspr.Tiramisuper;
import ftbsc.tspr.api.ILoadable;
import ftbsc.tspr.api.command.BaseCommand;
import ftbsc.tspr.api.module.BaseModule;
import ftbsc.tspr.api.module.TogglableModule;
import ftbsc.tspr.helpers.Chat;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

import java.util.stream.Collectors;

/**
 * Command to list or manage modules
 */
@AutoService(ILoadable.class)
public class ModUtils extends BaseCommand {

	@Override
	public String getName() { return "mods"; }

	@Override
	public LiteralArgumentBuilder<CommandSourceStack> command(LiteralArgumentBuilder<CommandSourceStack> builder, CommandBuildContext context) {
		return builder
			.then(
				Commands.literal("off")
					.executes(ctx -> {
						for (BaseModule mod : Tiramisuper.mods()) {
							if (mod instanceof TogglableModule) {
								((TogglableModule) mod).setEnabled(false);
							}
						}
						return 1;
					})
			)
			.executes(ctx -> {
				String mods = Tiramisuper.mods().stream()
					.map(x -> x.getName())
					.collect(Collectors.joining(", "));
				Chat.message("[ %s ]", mods);
				return 1;
			});
	}

}
