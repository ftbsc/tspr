package ftbsc.tspr.modules.vision;

import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;

import com.google.auto.service.AutoService;

import ftbsc.tspr.Tiramisuper;
import ftbsc.tspr.api.ILoadable;
import ftbsc.tspr.api.module.TogglableModule;
import ftbsc.tspr.helpers.Draw;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

@AutoService(ILoadable.class)
public class Storage extends TogglableModule {
	private ModConfigSpec.DoubleValue alpha;

	private Block[] handledBlocks = {
		Blocks.CHEST,
		Blocks.TRAPPED_CHEST,
		Blocks.BARREL,
		Blocks.HOPPER,
		Blocks.DROPPER,
		Blocks.DISPENSER,
		Blocks.ENDER_CHEST,
		Blocks.FURNACE,
	};

	public Storage() {
		for (Block block : this.handledBlocks) {
			Tiramisuper.SCANNER.onLoad(block, (pos, state) -> this.states.put(pos, state.getBlock()));
			Tiramisuper.SCANNER.onUnload(block, (pos, state) -> this.states.remove(pos));
		}
	}

	public void config(ModConfigSpec.Builder builder) {
		this.alpha = builder
			.comment("alpha channel value for highlights")
			.defineInRange("alpha", .25, 0., 1.);
	}

	private ConcurrentHashMap<BlockPos, Block> states = new ConcurrentHashMap<>();

	@SubscribeEvent
	void onRenderLevelStage(RenderLevelStageEvent.AfterEntities event) {
		if (!this.enabled.getAsBoolean()) {
			return;
		}

		Draw draw = Draw.prepare(event);

		for (Entry<BlockPos, Block> entry : this.states.entrySet()) {
			Integer color = Storage.blockColors.get(entry.getValue());
			if (color == null) continue;
			float alpha = (float) this.alpha.getAsDouble();
			draw.drawFilledBox(entry.getKey(), color, alpha);
		}
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
}
