package ftbsc.tspr.modules.vision;

import java.util.Arrays;
import java.util.Map;

import com.google.auto.service.AutoService;

import ftbsc.tspr.api.ILoadable;
import ftbsc.tspr.api.module.ScannerModule;
import net.minecraft.ChatFormatting;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

@AutoService(ILoadable.class)
public class Storage extends ScannerModule {

	public void config(ModConfigSpec.Builder builder) {}
	
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

			Blocks.SHULKER_BOX,
			Blocks.WHITE_SHULKER_BOX,
			Blocks.ORANGE_SHULKER_BOX,
			Blocks.MAGENTA_SHULKER_BOX,
			Blocks.LIGHT_BLUE_SHULKER_BOX,
			Blocks.YELLOW_SHULKER_BOX,
			Blocks.LIME_SHULKER_BOX,
			Blocks.PINK_SHULKER_BOX,
			Blocks.GRAY_SHULKER_BOX,
			Blocks.LIGHT_GRAY_SHULKER_BOX,
			Blocks.CYAN_SHULKER_BOX,
			Blocks.PURPLE_SHULKER_BOX,
			Blocks.BLUE_SHULKER_BOX,
			Blocks.BROWN_SHULKER_BOX,
			Blocks.GREEN_SHULKER_BOX,
			Blocks.RED_SHULKER_BOX,
			Blocks.BLACK_SHULKER_BOX
		);
	}

	protected Integer getColor(Block block) {
		return Storage.blockColors.get(block);
	}

	private static final Map<Block, Integer> blockColors = Map.ofEntries(
		Map.entry(Blocks.CHEST, ChatFormatting.GOLD.getColor()),
		Map.entry(Blocks.TRAPPED_CHEST, ChatFormatting.GOLD.getColor()),
		Map.entry(Blocks.BARREL, ChatFormatting.GOLD.getColor()),

		Map.entry(Blocks.DISPENSER, ChatFormatting.GRAY.getColor()),
		Map.entry(Blocks.DROPPER, ChatFormatting.GRAY.getColor()),
		Map.entry(Blocks.FURNACE, ChatFormatting.GRAY.getColor()),
		Map.entry(Blocks.BLAST_FURNACE, ChatFormatting.GRAY.getColor()),
		Map.entry(Blocks.SMOKER, ChatFormatting.GRAY.getColor()),

		Map.entry(Blocks.ENDER_CHEST, ChatFormatting.DARK_PURPLE.getColor()),

		Map.entry(Blocks.HOPPER, ChatFormatting.DARK_GREEN.getColor()),

		Map.entry(Blocks.SHULKER_BOX, ChatFormatting.DARK_AQUA.getColor()),
		Map.entry(Blocks.WHITE_SHULKER_BOX, ChatFormatting.DARK_AQUA.getColor()),
		Map.entry(Blocks.ORANGE_SHULKER_BOX, ChatFormatting.DARK_AQUA.getColor()),
		Map.entry(Blocks.MAGENTA_SHULKER_BOX, ChatFormatting.DARK_AQUA.getColor()),
		Map.entry(Blocks.LIGHT_BLUE_SHULKER_BOX, ChatFormatting.DARK_AQUA.getColor()),
		Map.entry(Blocks.YELLOW_SHULKER_BOX, ChatFormatting.DARK_AQUA.getColor()),
		Map.entry(Blocks.LIME_SHULKER_BOX, ChatFormatting.DARK_AQUA.getColor()),
		Map.entry(Blocks.PINK_SHULKER_BOX, ChatFormatting.DARK_AQUA.getColor()),
		Map.entry(Blocks.GRAY_SHULKER_BOX, ChatFormatting.DARK_AQUA.getColor()),
		Map.entry(Blocks.LIGHT_GRAY_SHULKER_BOX, ChatFormatting.DARK_AQUA.getColor()),
		Map.entry(Blocks.CYAN_SHULKER_BOX, ChatFormatting.DARK_AQUA.getColor()),
		Map.entry(Blocks.PURPLE_SHULKER_BOX, ChatFormatting.DARK_AQUA.getColor()),
		Map.entry(Blocks.BLUE_SHULKER_BOX, ChatFormatting.DARK_AQUA.getColor()),
		Map.entry(Blocks.BROWN_SHULKER_BOX, ChatFormatting.DARK_AQUA.getColor()),
		Map.entry(Blocks.GREEN_SHULKER_BOX, ChatFormatting.DARK_AQUA.getColor()),
		Map.entry(Blocks.RED_SHULKER_BOX, ChatFormatting.DARK_AQUA.getColor()),
		Map.entry(Blocks.BLACK_SHULKER_BOX, ChatFormatting.DARK_AQUA.getColor())
	);

	@SubscribeEvent
	void onRenderLevelStage(RenderLevelStageEvent.AfterEntities event) {
		this.doRender(event);
	}
}
