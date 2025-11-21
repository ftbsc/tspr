package ftbsc.tspr.api.command;

import java.util.Collections;
import java.util.List;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;

import ftbsc.tspr.api.ILoadable;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public abstract class BaseCommand implements ILoadable {

	public String getName() {
		return this.getClass().getSimpleName();
	}

	public List<LiteralArgumentBuilder<CommandSourceStack>> subcommands() {
		return Collections.emptyList();
	}

	public BaseCommand() {}

	public LiteralCommandNode<CommandSourceStack> build(CommandBuildContext ctx) {
		return this.command(Commands.literal(this.getName().toLowerCase()), ctx).build();
	}

	// define command with this
	public abstract LiteralArgumentBuilder<CommandSourceStack> command(LiteralArgumentBuilder<CommandSourceStack> builder, CommandBuildContext ctx);
}
