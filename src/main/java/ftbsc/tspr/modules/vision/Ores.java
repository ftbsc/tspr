package ftbsc.tspr.modules.vision;

import java.util.Arrays;
import java.util.Map;

import com.google.auto.service.AutoService;

import ftbsc.tspr.Tiramisuper;
import ftbsc.tspr.api.ILoadable;
import ftbsc.tspr.api.module.ScannerModule;
import ftbsc.tspr.helpers.Color;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.gizmos.GizmoStyle;
import net.minecraft.gizmos.Gizmos;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

import static ftbsc.tspr.Tiramisuper.mc;

/**
 * Highlight ores, for easier mining
 * Depends on {@link ftbsc.tspr.services.Scanner} service
 */
@AutoService(ILoadable.class)
public class Ores extends ScannerModule {

	private ModConfigSpec.BooleanValue ancientDebris;
	private ModConfigSpec.BooleanValue diamonds;
	private ModConfigSpec.BooleanValue redstone;
	private ModConfigSpec.BooleanValue iron;
	private ModConfigSpec.BooleanValue gold;
	private ModConfigSpec.BooleanValue copper;
	private ModConfigSpec.BooleanValue coal;
	private ModConfigSpec.BooleanValue emerald;
	private ModConfigSpec.BooleanValue lapis;
	private ModConfigSpec.BooleanValue quartz;
	private ModConfigSpec.DoubleValue distance;
	private Map<Block, ModConfigSpec.BooleanValue> blockConfigs;

	@Override
	public void config(ModConfigSpec.Builder builder) {
		this.ancientDebris = builder
			.comment("show Ancient Debris")
			.define("ancient-debris", true);
		this.diamonds = builder
			.comment("show Diamond ores")
			.define("diamonds", true);
		this.redstone = builder
			.comment("show Redstone ores")
			.define("redstone", false);
		this.iron = builder
			.comment("show Iron ores")
			.define("iron", false);
		this.gold = builder
			.comment("show Gold ores")
			.define("gold", false);
		this.copper = builder
			.comment("show Copper ores")
			.define("copper", false);
		this.coal = builder
			.comment("show Coal ores")
			.define("coal", false);
		this.emerald = builder
			.comment("show Emerald ores")
			.define("emerald", true);
		this.lapis = builder
			.comment("show Lapis ores")
			.define("lapis", false);
		this.quartz = builder
			.comment("show Quartz ores")
			.define("quartz", false);
		this.distance = builder
			.comment("max distance from player for rendering")
			.defineInRange("distance", 20., 0., Double.MAX_VALUE);

		// TODO jank having it here but whatever...
		this.blockConfigs = Map.ofEntries(
			Map.entry(Blocks.ANCIENT_DEBRIS, this.ancientDebris),

			Map.entry(Blocks.DIAMOND_ORE, this.diamonds),
			Map.entry(Blocks.DEEPSLATE_DIAMOND_ORE, this.diamonds),

			Map.entry(Blocks.REDSTONE_ORE, this.redstone),
			Map.entry(Blocks.DEEPSLATE_REDSTONE_ORE, this.redstone),

			Map.entry(Blocks.IRON_ORE, this.iron),
			Map.entry(Blocks.DEEPSLATE_IRON_ORE, this.iron),

			Map.entry(Blocks.GOLD_ORE, this.gold),
			Map.entry(Blocks.DEEPSLATE_GOLD_ORE, this.gold),
			Map.entry(Blocks.NETHER_GOLD_ORE, this.gold),

			Map.entry(Blocks.COPPER_ORE, this.copper),
			Map.entry(Blocks.DEEPSLATE_COPPER_ORE, this.copper),

			Map.entry(Blocks.COAL_ORE, this.coal),
			Map.entry(Blocks.DEEPSLATE_COAL_ORE, this.coal),

			Map.entry(Blocks.EMERALD_ORE, this.emerald),
			Map.entry(Blocks.DEEPSLATE_EMERALD_ORE, this.emerald),

			Map.entry(Blocks.LAPIS_ORE, this.lapis),
			Map.entry(Blocks.DEEPSLATE_LAPIS_ORE, this.lapis),

			Map.entry(Blocks.NETHER_QUARTZ_ORE, this.quartz)
		);
	}

	@Override
	protected Iterable<Block> getBlocks() {
		return Arrays.asList(
			Blocks.ANCIENT_DEBRIS,

			Blocks.DIAMOND_ORE,
			Blocks.DEEPSLATE_DIAMOND_ORE,

			Blocks.REDSTONE_ORE,
			Blocks.DEEPSLATE_REDSTONE_ORE,

			Blocks.IRON_ORE,
			Blocks.DEEPSLATE_IRON_ORE,

			Blocks.GOLD_ORE,
			Blocks.DEEPSLATE_GOLD_ORE,
			Blocks.NETHER_GOLD_ORE,

			Blocks.COPPER_ORE,
			Blocks.DEEPSLATE_COPPER_ORE,

			Blocks.COAL_ORE,
			Blocks.DEEPSLATE_COAL_ORE,

			Blocks.EMERALD_ORE,
			Blocks.DEEPSLATE_EMERALD_ORE,

			Blocks.LAPIS_ORE,
			Blocks.DEEPSLATE_LAPIS_ORE,

			Blocks.NETHER_QUARTZ_ORE
		);
	}

	@SubscribeEvent
	void onRenderLevelStage(RenderLevelStageEvent.AfterLevel event) {
		if (!this.enabled.getAsBoolean()) {
			return;
		}

		double maxDist = Mth.square(this.distance.getAsDouble());
		float alpha = (float) this.alpha.getAsDouble();

		for (Block block : this.handledBlocks) {
			Integer rgb = this.getColor(block);
			if (rgb == null) continue;
			int color = ARGB.color(alpha, rgb);
			ModConfigSpec.BooleanValue shouldDraw = blockConfigs.get(block);
			if (shouldDraw == null || !shouldDraw.getAsBoolean()) continue;

			for (BlockPos pos : Tiramisuper.SCANNER.getAll(block)) {
				if (mc().player.distanceToSqr(pos.getX(), pos.getY(), pos.getZ()) > maxDist) {
					continue;
				}
				Gizmos.cuboid(pos, 0.1f, GizmoStyle.stroke(color, 1.5f)).setAlwaysOnTop();
			}
		}
	}

	@Override
	protected Integer getColor(Block block) {
		return Ores.blockColors.get(block);
	}

	private static final Map<Block, Integer> blockColors = Map.ofEntries(
		Map.entry(Blocks.ANCIENT_DEBRIS, Color.pack(ChatFormatting.DARK_AQUA)),

		Map.entry(Blocks.DIAMOND_ORE, Color.pack(ChatFormatting.AQUA)),
		Map.entry(Blocks.DEEPSLATE_DIAMOND_ORE, Color.pack(ChatFormatting.AQUA)),

		Map.entry(Blocks.REDSTONE_ORE, Color.pack(ChatFormatting.RED)),
		Map.entry(Blocks.DEEPSLATE_REDSTONE_ORE, Color.pack(ChatFormatting.RED)),

		Map.entry(Blocks.IRON_ORE, Color.pack(ChatFormatting.GRAY)),
		Map.entry(Blocks.DEEPSLATE_IRON_ORE, Color.pack(ChatFormatting.GRAY)),

		Map.entry(Blocks.GOLD_ORE, Color.pack(ChatFormatting.YELLOW)),
		Map.entry(Blocks.DEEPSLATE_GOLD_ORE, Color.pack(ChatFormatting.YELLOW)),
		Map.entry(Blocks.NETHER_GOLD_ORE, Color.pack(ChatFormatting.YELLOW)),

		Map.entry(Blocks.COPPER_ORE, Color.pack(ChatFormatting.GOLD)),
		Map.entry(Blocks.DEEPSLATE_COPPER_ORE, Color.pack(ChatFormatting.GOLD)),

		Map.entry(Blocks.COAL_ORE, Color.pack(ChatFormatting.BLACK)),
		Map.entry(Blocks.DEEPSLATE_COAL_ORE, Color.pack(ChatFormatting.BLACK)),

		Map.entry(Blocks.EMERALD_ORE, Color.pack(ChatFormatting.GREEN)),
		Map.entry(Blocks.DEEPSLATE_EMERALD_ORE, Color.pack(ChatFormatting.GREEN)),

		Map.entry(Blocks.LAPIS_ORE, Color.pack(ChatFormatting.BLUE)),
		Map.entry(Blocks.DEEPSLATE_LAPIS_ORE, Color.pack(ChatFormatting.BLUE)),

		Map.entry(Blocks.NETHER_QUARTZ_ORE, Color.pack(ChatFormatting.GRAY))
	);
}
