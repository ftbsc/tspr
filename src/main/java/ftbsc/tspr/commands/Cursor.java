package ftbsc.tspr.commands;

import com.google.auto.service.AutoService;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import ftbsc.tspr.api.ILoadable;
import ftbsc.tspr.api.command.BaseCommand;
import ftbsc.tspr.helpers.Chat;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.util.Mth;
import net.minecraft.core.BlockPos;

@AutoService(ILoadable.class)
public class Cursor extends BaseCommand {

	public LiteralArgumentBuilder<CommandSourceStack> command(LiteralArgumentBuilder<CommandSourceStack> builder, CommandBuildContext context) {
		return builder
			.then(
				Commands.literal("info")
					.executes(ctx -> {
						Vec3 vec = MC.hitResult.getLocation();
						BlockPos pos = new BlockPos(Mth.floor(vec.x), Mth.floor(vec.y), Mth.floor(vec.z));
						switch (MC.hitResult.getType()) {
							case BLOCK:
								BlockState state = MC.level.getBlockState(pos);
								Chat.message("Block @ %s: %s", pos.toString(), state.toString());
								return 1;
							case ENTITY:
								double dist = Double.MAX_VALUE;
								Entity closest = null;
								for (Entity e : MC.level.entitiesForRendering()) {
									double currDist = e.distanceToSqr(vec);
									if (currDist < dist) {
										closest = e;
										dist = currDist;
									}
								}
								Chat.message("Entity %s", closest.toString());
								return 1;
							default:
							case MISS:
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
