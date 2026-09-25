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
public class Portals extends ScannerModule {

	@Override
	public void config(ModConfigSpec.Builder builder) {}

	@Override
	protected Iterable<Block> getBlocks() {
		return Arrays.asList(
			Blocks.NETHER_PORTAL,
			Blocks.END_PORTAL
		);
	}

	@Override
	protected Integer getColor(Block block) {
		return Portals.blockColors.get(block);
	}

	private static final Map<Block, Integer> blockColors = Map.ofEntries(
		Map.entry(Blocks.NETHER_PORTAL, Color.pack(ChatFormatting.DARK_PURPLE)),
		Map.entry(Blocks.END_PORTAL, Color.pack(ChatFormatting.DARK_AQUA))
	);

	@SubscribeEvent
	void onRenderLevelStage(RenderLevelStageEvent.AfterLevel event) {
		this.doRender(event);
	}
}
