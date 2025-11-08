package ftbsc.tspr.commands;

import com.google.auto.service.AutoService;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import ftbsc.tspr.api.ILoadable;
import ftbsc.tspr.api.command.BaseCommand;
import ftbsc.tspr.helpers.Chat;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.blocks.BlockStateArgument;
import net.minecraft.commands.arguments.blocks.BlockInput;

@AutoService(ILoadable.class)
public class BlockSearch extends BaseCommand {

	@Override
	public String getName() { return "block"; }

	public LiteralArgumentBuilder<CommandSourceStack> command(LiteralArgumentBuilder<CommandSourceStack> builder, CommandBuildContext context) {
		return builder
			.then(
				Commands.literal("search")
					.then(
						Commands.argument("id", IntegerArgumentType.integer(0))
							.executes( ctx -> {
								int block_id = ctx.getArgument("id", Integer.class);
								int block_number = block_id >> 4;
								int block_meta   = block_id & 0b1111;
								BlockState state = Block.stateById(block_id);
								Chat.message("block #[%d:%d]::%d >> %s", block_number, block_meta, block_id, state.toString());
								return 1;
							})
					)
					.then(
						Commands.argument("number", IntegerArgumentType.integer(0))
							.then(
								Commands.argument("meta", IntegerArgumentType.integer(0))
									.executes( ctx -> {
										int block_number = ctx.getArgument("number", Integer.class);
										int block_meta   = ctx.getArgument("meta", Integer.class);
										int block_id = (block_number << 4) | block_meta;
										BlockState state = Block.stateById(block_id);
										Chat.message("block #[%d:%d]::%d >> %s", block_number, block_meta, block_id, state.toString());
										return 1;
									})
							)
					)
			)
			.then(
				Commands.literal("id")
					.then(
						Commands.argument("name", BlockStateArgument.block(context))
							.executes( ctx -> {
								BlockInput arg = ctx.getArgument("name", BlockInput.class);
								BlockState state = arg.getState();
								int block_id = Block.getId(state);
								Chat.message("block #[%d:%d] >> %s", block_id >> 4, block_id & 0xF, state.toString());
								return 1;
							})
					)
			)
			.executes(ctx -> {
				Chat.message(ChatFormatting.RED, "no block specified");
				return 0;
			});
	}

}
