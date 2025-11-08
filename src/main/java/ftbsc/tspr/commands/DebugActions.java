package ftbsc.tspr.commands;

import com.google.auto.service.AutoService;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import ftbsc.tspr.api.ILoadable;
import ftbsc.tspr.api.command.BaseCommand;
import ftbsc.tspr.helpers.Chat;
import net.minecraft.client.Options;
import net.minecraft.client.gui.screens.debug.GameModeSwitcherScreen;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.util.Mth;
import net.neoforged.neoforge.server.command.EnumArgument;

@AutoService(ILoadable.class)
public class DebugActions extends BaseCommand {

	private enum RenderDistanceAction {
		INCREASE,
		DECREASE
	}

	public LiteralArgumentBuilder<CommandSourceStack> command(LiteralArgumentBuilder<CommandSourceStack> builder) {
		return builder
			.then(
				Commands.literal("reloadchunks")
					.executes(ctx -> {
						MC.levelRenderer.allChanged();
						Chat.message("reloading chunks");
						return 0;
					})
			)
			.then(
				Commands.literal("hitboxes")
					.executes(ctx -> {
						boolean flag = !MC.getEntityRenderDispatcher().shouldRenderHitBoxes();
						MC.getEntityRenderDispatcher().setRenderHitBoxes(flag);
						Chat.message("entity hitboxes %s", flag ? "enabled" : "disabled");
						return 0;
					})
			)
			.then(
				Commands.literal("clearmessages")
					.executes(ctx -> {
						if (MC.gui != null) {
							MC.gui.getChat().clearMessages(false);
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
									MC.options.renderDistance().get() + (action == RenderDistanceAction.INCREASE ? +1 : -1),
									Options.RENDER_DISTANCE_TINY, Options.RENDER_DISTANCE_REALLY_FAR
								);
								MC.options.renderDistance().set(new_distance);
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
						boolean flag1 = MC.debugRenderer.switchRenderChunkborder();
						Chat.message("chunk boundaries %s", flag1 ? "enabled" : "disabled");
						return 0;
					})
			)
			.then(
				Commands.literal("tooltips")
					.executes(ctx -> {
						MC.options.advancedItemTooltips = !MC.options.advancedItemTooltips;
						MC.options.save();
						return 0;
					})
			)
			.then(
				Commands.literal("reloadresources")
					.executes(ctx -> {
						MC.reloadResourcePacks();
						return 0;
					})
			)
			.then(
				Commands.literal("gamemode")
					.executes(ctx -> {
						MC.setScreen(new GameModeSwitcherScreen());
						return 0;
					})
			)
			.executes(ctx -> {
				return 1;
			});
	}

}
