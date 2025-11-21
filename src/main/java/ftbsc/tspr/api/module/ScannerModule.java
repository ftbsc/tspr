package ftbsc.tspr.api.module;

import java.util.HashSet;
import java.util.Set;

import ftbsc.tspr.Tiramisuper;
import ftbsc.tspr.helpers.Draw;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

public abstract class ScannerModule extends TogglableModule {

	protected ModConfigSpec.DoubleValue alpha;

	protected abstract Iterable<Block> getBlocks();
	protected abstract Integer getColor(Block block);

	protected void draw(Draw draw, BlockPos pos, int color, float alpha) {
		draw.drawFilledBox(pos, color, alpha);
	}

	protected Set<Block> handledBlocks = new HashSet<>();

	public ScannerModule() {
		for (Block block : this.getBlocks()) {
			this.handledBlocks.add(block);
			Tiramisuper.SCANNER.watch(block);
		}
	}

	@Override
	public void prepareConfig(ModConfigSpec.Builder builder) {
		this.alpha = builder
			.comment("transparency level for this overlay")
			.defineInRange("alpha", 0.2, 0., Double.MAX_VALUE);

		super.prepareConfig(builder);
	}

	@SubscribeEvent
	protected void onRenderLevelStage(RenderLevelStageEvent.AfterEntities event) {
		if (!this.enabled.getAsBoolean()) {
			return;
		}

		Draw draw = Draw.prepare(event);
		float alpha = (float) this.alpha.getAsDouble();

		for (Block block : this.handledBlocks) {
			Integer color = this.getColor(block);
			if (color == null) continue;
			for (BlockPos pos : Tiramisuper.SCANNER.getAll(block)) {
				this.draw(draw, pos, color, alpha);
			}
		}
	}
}
