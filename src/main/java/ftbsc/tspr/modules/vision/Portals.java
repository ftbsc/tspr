package ftbsc.tspr.modules.vision;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.google.auto.service.AutoService;

import ftbsc.tspr.Tiramisuper;
import ftbsc.tspr.api.ILoadable;
import ftbsc.tspr.api.module.ScannerModule;
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
public class Portals extends ScannerModule {

	public void config(ModConfigSpec.Builder builder) {}

	protected Iterable<Block> getBlocks() {
		return Arrays.asList(
			Blocks.NETHER_PORTAL,
			Blocks.END_PORTAL
		);
	}

	protected Integer getColor(Block block) {
		return Portals.blockColors.get(block);
	}

	private static final Map<Block, Integer> blockColors = Map.ofEntries(
		Map.entry(Blocks.NETHER_PORTAL, ChatFormatting.DARK_PURPLE.getColor()),
		Map.entry(Blocks.END_PORTAL, ChatFormatting.DARK_AQUA.getColor())
	);
}
