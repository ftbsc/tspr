package ftbsc.tspr.commands;

import com.google.auto.service.AutoService;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import ftbsc.tspr.api.ILoadable;
import ftbsc.tspr.api.command.BaseCommand;
import ftbsc.tspr.helpers.Chat;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.debug.DebugScreenEntries;
import net.minecraft.client.gui.screens.debug.DebugOptionsScreen;
import net.minecraft.client.gui.screens.debug.GameModeSwitcherScreen;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.util.Mth;
import net.neoforged.neoforge.server.command.EnumArgument;

import static ftbsc.tspr.Tiramisuper.mc;

@AutoService(ILoadable.class)
public class DebugActions extends BaseCommand {

	private enum RenderDistanceAction {
		INCREASE,
		DECREASE
	}

	public LiteralArgumentBuilder<CommandSourceStack> command(LiteralArgumentBuilder<CommandSourceStack> builder, CommandBuildContext context) {
		return builder
			.then(
				Commands.literal("overlay")
					.then(
						Commands.literal("network")
							.executes(ctx -> {
								mc().getDebugOverlay().toggleNetworkCharts();
								Chat.message("toggled debug overlay network chart");
								return 0;
							})
					)
					.then(
						Commands.literal("fps")
							.executes(ctx -> {
								mc().getDebugOverlay().toggleFpsCharts();
								Chat.message("toggled debug overlay fps chart");
								return 0;
							})
					)
					.then(
						Commands.literal("profiler")
							.executes(ctx -> {
								mc().getDebugOverlay().toggleProfilerChart();
								Chat.message("toggled debug overlay profiler chart");
								return 0;
							})
					)
					.executes(ctx -> {
						Chat.message(ChatFormatting.RED, "no argument given");
						return 1;
					})
			)
			.then(
				Commands.literal("reloadchunks")
					.executes(ctx -> {
						mc().levelRenderer.allChanged();
						Chat.message("reloading chunks");
						return 0;
					})
			)
			.then(
				Commands.literal("hitboxes")
					.executes(ctx -> {
						boolean flag = mc().debugEntries.toggleStatus(DebugScreenEntries.ENTITY_HITBOXES);
						Chat.message("entity hitboxes %s", flag ? "enabled" : "disabled");
						return 0;
					})
			)
			.then(
				Commands.literal("clearmessages")
					.executes(ctx -> {
						if (mc().gui != null) {
							mc().gui.getChat().clearMessages(false);
							Chat.message("cleared chat messages");
							return 0;
						}
						return 1;
					})
			)
			.then(
				Commands.literal("renderdistance")
					.then(
						Commands.argument("action", EnumArgument.enumArgument(RenderDistanceAction.class))
							.executes(ctx -> {
								RenderDistanceAction action = ctx.getArgument("action", RenderDistanceAction.class);
								int new_distance = Mth.clamp(
									mc().options.renderDistance().get() + (action == RenderDistanceAction.INCREASE ? +1 : -1),
									Options.RENDER_DISTANCE_SHORT, Options.RENDER_DISTANCE_REALLY_FAR
								);
								mc().options.renderDistance().set(new_distance);
								Chat.message("set render distance to %d", new_distance);
								return 0;
							})
					)
					.executes(ctx -> {
						return 1;
					})
			)
			.then(
				Commands.literal("boundaries")
					.executes(ctx -> {
						boolean flag1 = mc().debugEntries.toggleStatus(DebugScreenEntries.CHUNK_BORDERS);
						Chat.message("chunk boundaries %s", flag1 ? "enabled" : "disabled");
						return 0;
					})
			)
			.then(
				Commands.literal("tooltips")
					.executes(ctx -> {
						mc().options.advancedItemTooltips = !mc().options.advancedItemTooltips;
						mc().options.save();
						Chat.message("toggled advanced item tooltips");
						return 0;
					})
			)
			.then(
				Commands.literal("reloadresources")
					.executes(ctx -> {
						mc().reloadResourcePacks();
						Chat.message("reloaded resource pack");
						return 0;
					})
			)
			.then(
				Commands.literal("gamemode")
					.executes(ctx -> {
						mc().setScreen(new GameModeSwitcherScreen());
						return 0;
					})
			)
			.then(
				Commands.literal("screen")
					.executes(ctx -> {
						mc().setScreen(new DebugOptionsScreen());
						return 0;
					})
			)
			.executes(ctx -> {
				return 1;
			});
	}

}
