package ftbsc.tspr.modules.vision;

import java.util.Arrays;
import java.util.Map;

import com.google.auto.service.AutoService;

import ftbsc.tspr.api.ILoadable;
import ftbsc.tspr.api.module.ScannerModule;
import ftbsc.tspr.helpers.Color;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.gizmos.GizmoStyle;
import net.minecraft.gizmos.Gizmos;
import net.minecraft.util.ARGB;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

@AutoService(ILoadable.class)
public class Spawners extends ScannerModule {

	@Override
	public void config(ModConfigSpec.Builder builder) {}

	@Override
	protected Iterable<Block> getBlocks() {
		return Arrays.asList(
			Blocks.SPAWNER
		);
	}

	@Override
	protected Integer getColor(Block block) {
		return Spawners.blockColors.get(block);
	}

	@Override
	protected void draw(BlockPos pos, int rgb, float alpha) {
		int color = ARGB.color(alpha, rgb);
		Gizmos.cuboid(pos, GizmoStyle.strokeAndFill(color, 1.f, color)).setAlwaysOnTop();
	}

	@SubscribeEvent
	void onRenderLevelStage(RenderLevelStageEvent.AfterLevel event) {
		this.doRender(event);
	}

	private static final Map<Block, Integer> blockColors = Map.ofEntries(
		Map.entry(Blocks.SPAWNER, Color.pack(ChatFormatting.DARK_RED))
	);
}
