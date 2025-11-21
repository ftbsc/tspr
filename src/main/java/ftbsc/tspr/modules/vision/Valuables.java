package ftbsc.tspr.modules.vision;

import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

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
public class Valuables extends TogglableModule {

	private ModConfigSpec.DoubleValue alpha;
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
	private Map<Block, ModConfigSpec.BooleanValue> blockConfigs;

	private Block[] handledBlocks = {
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

		Blocks.NETHER_QUARTZ_ORE,
	};

	public Valuables() {
		for (Block block : this.handledBlocks) {
			Tiramisuper.SCANNER.onLoad(block, (pos, state) -> this.states.put(pos, state.getBlock()));
		}
		Tiramisuper.SCANNER.onUnload((pos) -> {
			for (BlockPos key : this.states.keySet()) {
				if (pos.contains(key)) {
					this.states.remove(key);
				}
			}
		});
	}

	public void config(ModConfigSpec.Builder builder) {
		this.alpha = builder
			.comment("alpha channel value for highlights")
			.defineInRange("alpha", .1, 0., 1.);
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

	private ConcurrentHashMap<BlockPos, Block> states = new ConcurrentHashMap<>();

	@SubscribeEvent
	void onRenderLevelStage(RenderLevelStageEvent.AfterEntities event) {
		if (!this.enabled.getAsBoolean()) {
			return;
		}

		Draw draw = Draw.prepare(event);

		for (Entry<BlockPos, Block> entry : this.states.entrySet()) {
			Integer color = Valuables.blockColors.get(entry.getValue());
			if (color == null) continue;
			ModConfigSpec.BooleanValue shouldDraw = blockConfigs.get(entry.getValue());
			if (shouldDraw == null || !shouldDraw.getAsBoolean()) continue;
			float alpha = (float) this.alpha.getAsDouble();
			draw.drawOutlineBox(entry.getKey(), color, alpha);
		}
	}

	private static final Map<Block, Integer> blockColors = Map.ofEntries(
		Map.entry(Blocks.ANCIENT_DEBRIS, ChatFormatting.GOLD.getColor()),

		Map.entry(Blocks.DIAMOND_ORE, ChatFormatting.AQUA.getColor()),
		Map.entry(Blocks.DEEPSLATE_DIAMOND_ORE, ChatFormatting.AQUA.getColor()),

		Map.entry(Blocks.REDSTONE_ORE, ChatFormatting.RED.getColor()),
		Map.entry(Blocks.DEEPSLATE_REDSTONE_ORE, ChatFormatting.RED.getColor()),

		Map.entry(Blocks.IRON_ORE, ChatFormatting.GRAY.getColor()),
		Map.entry(Blocks.DEEPSLATE_IRON_ORE, ChatFormatting.GRAY.getColor()),

		Map.entry(Blocks.GOLD_ORE, ChatFormatting.YELLOW.getColor()),
		Map.entry(Blocks.DEEPSLATE_GOLD_ORE, ChatFormatting.YELLOW.getColor()),
		Map.entry(Blocks.NETHER_GOLD_ORE, ChatFormatting.YELLOW.getColor()),

		Map.entry(Blocks.COPPER_ORE, ChatFormatting.DARK_RED.getColor()),
		Map.entry(Blocks.DEEPSLATE_COPPER_ORE, ChatFormatting.DARK_RED.getColor()),

		Map.entry(Blocks.COAL_ORE, ChatFormatting.DARK_GRAY.getColor()),
		Map.entry(Blocks.DEEPSLATE_COAL_ORE, ChatFormatting.DARK_GRAY.getColor()),

		Map.entry(Blocks.EMERALD_ORE, ChatFormatting.GREEN.getColor()),
		Map.entry(Blocks.DEEPSLATE_EMERALD_ORE, ChatFormatting.GREEN.getColor()),

		Map.entry(Blocks.LAPIS_ORE, ChatFormatting.BLUE.getColor()),
		Map.entry(Blocks.DEEPSLATE_LAPIS_ORE, ChatFormatting.BLUE.getColor()),

		Map.entry(Blocks.NETHER_QUARTZ_ORE, ChatFormatting.WHITE.getColor())
	);
}
