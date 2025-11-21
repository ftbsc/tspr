package ftbsc.tspr.modules.vision;

import java.util.Arrays;
import java.util.Map;

import com.google.auto.service.AutoService;

import ftbsc.tspr.api.ILoadable;
import ftbsc.tspr.api.module.ScannerModule;
import ftbsc.tspr.helpers.Draw;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

@AutoService(ILoadable.class)
public class Spawners extends ScannerModule {

	public void config(ModConfigSpec.Builder builder) {}

	protected Iterable<Block> getBlocks() {
		return Arrays.asList(
			Blocks.SPAWNER
		);
	}

	protected Integer getColor(Block block) {
		return Spawners.blockColors.get(block);
	}

	@Override
	protected void draw(Draw draw, BlockPos pos, int color, float alpha) {
		draw.drawOutlineFilledBox(pos, color, alpha, alpha / 2.f);
	}

	@SubscribeEvent
	void onRenderLevelStage(RenderLevelStageEvent.AfterEntities event) {
		this.doRender(event);
	}

	private static final Map<Block, Integer> blockColors = Map.ofEntries(
		Map.entry(Blocks.SPAWNER, ChatFormatting.DARK_RED.getColor())
	);
}
