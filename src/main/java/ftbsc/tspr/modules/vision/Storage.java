package ftbsc.tspr.modules.vision;

import java.util.Arrays;
import java.util.Map;

import com.google.auto.service.AutoService;

import ftbsc.tspr.api.ILoadable;
import ftbsc.tspr.api.module.ScannerModule;
import ftbsc.tspr.helpers.Color;
import net.minecraft.ChatFormatting;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

@AutoService(ILoadable.class)
public class Storage extends ScannerModule {

	@Override
	public void config(ModConfigSpec.Builder builder) {}
	
	@Override
	protected Iterable<Block> getBlocks() {
		return Arrays.asList(
			Blocks.CHEST,
			Blocks.TRAPPED_CHEST,
			Blocks.BARREL,

			Blocks.DISPENSER,
			Blocks.DROPPER,
			Blocks.FURNACE,
			Blocks.BLAST_FURNACE,
			Blocks.SMOKER,

			Blocks.ENDER_CHEST,

			Blocks.HOPPER,

			Blocks.SHULKER_BOX
		);
	}

	@Override
	protected Integer getColor(Block block) {
		return Storage.blockColors.get(block);
	}

	private static final Map<Block, Integer> blockColors = Map.ofEntries(
		Map.entry(Blocks.CHEST, Color.pack(ChatFormatting.GOLD)),
		Map.entry(Blocks.TRAPPED_CHEST, Color.pack(ChatFormatting.GOLD)),
		Map.entry(Blocks.BARREL, Color.pack(ChatFormatting.GOLD)),

		Map.entry(Blocks.DISPENSER, Color.pack(ChatFormatting.GRAY)),
		Map.entry(Blocks.DROPPER, Color.pack(ChatFormatting.GRAY)),
		Map.entry(Blocks.FURNACE, Color.pack(ChatFormatting.GRAY)),
		Map.entry(Blocks.BLAST_FURNACE, Color.pack(ChatFormatting.GRAY)),
		Map.entry(Blocks.SMOKER, Color.pack(ChatFormatting.GRAY)),

		Map.entry(Blocks.ENDER_CHEST, Color.pack(ChatFormatting.DARK_PURPLE)),

		Map.entry(Blocks.HOPPER, Color.pack(ChatFormatting.DARK_GREEN)),

		Map.entry(Blocks.SHULKER_BOX, Color.pack(ChatFormatting.DARK_AQUA))
	);

	@SubscribeEvent
	void onRenderLevelStage(RenderLevelStageEvent.AfterLevel event) {
		this.doRender(event);
	}
}
