package ftbsc.tspr.api.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;

import ftbsc.tspr.api.ILoadable;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

/**
 * An abstract simple command, providing base methods and utils
 */
public abstract class BaseCommand implements ILoadable {

	@Override
	public String getName() {
		return this.getClass().getSimpleName();
	}

	/**
	 * Build this command on a {@link CommandBuildContext} and returns a {@link LiteralCommandNode} ready to register
	 * on desired {@link CommandDispatcher}
	 * @return node to register on dispatcher
	 */
	public LiteralCommandNode<CommandSourceStack> build(CommandBuildContext ctx) {
		return this.command(Commands.literal(this.getName().toLowerCase()), ctx).build();
	}

	/**
	 * Override this method to provide the actual command declaration.
	 * Provides both a {@link LiteralArgumentBuilder} and its {@link CommandBuildContext}
	 * @return the builder, for chaining
	 */
	public abstract LiteralArgumentBuilder<CommandSourceStack> command(LiteralArgumentBuilder<CommandSourceStack> builder, CommandBuildContext ctx);
}
