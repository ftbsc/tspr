package ftbsc.tspr.commands;

import com.google.auto.service.AutoService;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import ftbsc.tspr.api.ILoadable;
import ftbsc.tspr.api.command.BaseCommand;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.debug.GameModeSwitcherScreen;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
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
						return 0;
					})
			)
			.then(
				Commands.literal("hitboxes")
					.executes(ctx -> {
						boolean flag = !MC.getEntityRenderDispatcher().shouldRenderHitBoxes();
						MC.getEntityRenderDispatcher().setRenderHitBoxes(flag);
						return 0;
					})
			)
			.then(
				Commands.literal("clearmessages")
					.executes(ctx -> {
						if (MC.gui != null) {
							MC.gui.getChat().clearMessages(false);
							return 0;
						}
						return 1;
					})
			)
			// .then(
			// 	Commands.literal("renderdistance")
			// 		.then(
			// 			Commands.argument("action", EnumArgument.enumArgument(RenderDistanceAction.class))
			// 				.executes(ctx -> {
			// 					RenderDistanceAction action = ctx.getArgument("action", RenderDistanceAction.class);
			// 					double new_distance = MathHelper.clamp(
			// 						(double)(MC.options.renderDistance + (action == RenderDistanceAction.INCREASE ? +1 : -1)),
			// 						AbstractWidget.RENDER_DISTANCE.getMinValue(), AbstractOption.RENDER_DISTANCE.getMaxValue()
			// 					);
			// 					AbstractOption.RENDER_DISTANCE.set(MC.options, new_distance);
			// 					log("debug.cycle_renderdistance.message");
			// 					return 0;
			// 				})
			// 		)
			// 		.executes(ctx -> {
			// 			return 1;
			// 		})
			// )
			.then(
				Commands.literal("boundaries")
					.executes(ctx -> {
						boolean flag1 = MC.debugRenderer.switchRenderChunkborder();
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
			// .then(
			// 	Commands.literal("gamemode")
			// 		.executes(ctx -> {
			// 			MC.setScreen(new GamemodeSelectionScreen());
			// 			return 0;
			// 		})
			// )
			.executes(ctx -> {
				return 1;
			});
	}

}
