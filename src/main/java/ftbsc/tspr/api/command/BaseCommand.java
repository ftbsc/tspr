package ftbsc.tspr.api.command;

import java.util.Collections;
import java.util.List;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import ftbsc.tspr.api.IGlobals;
import ftbsc.tspr.api.ILoadable;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public abstract class BaseCommand implements IGlobals, ILoadable {

	public final String name = this.getClass().getSimpleName();
	private final LiteralArgumentBuilder<CommandSourceStack> builder = Commands.literal(this.name.toLowerCase());

	public abstract LiteralArgumentBuilder<CommandSourceStack> command(LiteralArgumentBuilder<CommandSourceStack> builder);

	public List<LiteralArgumentBuilder<CommandSourceStack>> subcommands() {
		return Collections.emptyList();
	}

	public BaseCommand() {}

	public void register(CommandDispatcher<CommandSourceStack> dispatcher) {
		dispatcher.register(this.command(this.builder));
	}

	public String getName() {
		return this.name;
	}
}
