package ftbsc.tspr.commands;

import com.google.auto.service.AutoService;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import ftbsc.tspr.api.ILoadable;
import ftbsc.tspr.api.command.BaseCommand;
import ftbsc.tspr.helpers.Chat;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;

/**
 * Command to get information on what's under player cursor
 */
@AutoService(ILoadable.class)
public class Cursor extends BaseCommand {

	@Override
	public LiteralArgumentBuilder<CommandSourceStack> command(LiteralArgumentBuilder<CommandSourceStack> builder, CommandBuildContext context) {
		return builder
			.then(
				Commands.literal("info")
					.executes(ctx -> {
						Minecraft mc = Minecraft.getInstance();
						switch (mc.hitResult) {
							case null:
								Chat.message(ChatFormatting.RED, "hitResult is null");
								return 0;
							case BlockHitResult block:
								BlockPos pos = block.getBlockPos();
								if (mc.level != null) {
									BlockState state = mc.level.getBlockState(pos);
									Chat.message("Block @ %s: %s", pos.toString(), state.toString());
									return 1;
								}
								Chat.message(ChatFormatting.RED, "world is not available");
								return 0;
							case EntityHitResult entity:
								Vec3 loc = entity.getLocation();
								Chat.message("Entity @ %s: %s", loc, entity.getEntity());
								return 1;
							default:
								Chat.message("nothing under cursor");
								return 0;
						}
					})
			)
			.executes(ctx -> {
				Chat.message(ChatFormatting.RED, "no domain specified");
				return 0;
			});
	}

}
