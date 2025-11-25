package ftbsc.tspr.commands;

import com.google.auto.service.AutoService;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import ftbsc.tspr.api.ILoadable;
import ftbsc.tspr.api.command.BaseCommand;
import ftbsc.tspr.helpers.Chat;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

/**
 * Command to forcibly change player position, clientside
 */
@AutoService(ILoadable.class)
public class Teleport extends BaseCommand {

	@Override
	public String getName() { return "tp"; }

	@Override
	public LiteralArgumentBuilder<CommandSourceStack> command(LiteralArgumentBuilder<CommandSourceStack> builder, CommandBuildContext context) {
		return builder
			.then(
				Commands.literal("up")
					.then(
						Commands.argument("distance", DoubleArgumentType.doubleArg())
							.executes( ctx -> {
								Minecraft mc = Minecraft.getInstance();
								if (mc.player != null) {
									LocalPlayer player = mc.player;
									double distance = ctx.getArgument("distance", Double.class);
									player.setPos(
										player.position().x,
										player.position().y + distance,
										player.position().z
									);
									Chat.message(String.format("blinked up %.1f blocks", distance));
									return 1;
								}
								Chat.message(ChatFormatting.RED, "no loaded player");
								return 0;
							})
					)
			)
			.then(
				Commands.argument("x", DoubleArgumentType.doubleArg())
					.then(
						Commands.argument("y", DoubleArgumentType.doubleArg())
							.then(
								Commands.argument("z", DoubleArgumentType.doubleArg())
									.executes( ctx -> {
										Minecraft mc = Minecraft.getInstance();
										double x = ctx.getArgument("x", Double.class);
										double y = ctx.getArgument("y", Double.class);
										double z = ctx.getArgument("z", Double.class);
										if (mc.player != null) {
											mc.player.setPos(x, y, z);
											Chat.message(String.format("blinked to X%.0f | Z%.0f", x, z));
											return 1;
										}
										Chat.message(ChatFormatting.RED, "no loaded player");
										return 0;
									})
							)
					)
			)
			.executes(ctx -> {
				Chat.message(ChatFormatting.RED, "no args specified");
				return 0;
			});
	}
}
