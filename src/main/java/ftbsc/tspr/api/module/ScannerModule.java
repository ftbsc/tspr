package ftbsc.tspr.api.module;

import java.util.HashSet;
import java.util.Set;

import ftbsc.tspr.Tiramisuper;
import net.minecraft.core.BlockPos;
import net.minecraft.gizmos.GizmoStyle;
import net.minecraft.gizmos.Gizmos;
import net.minecraft.util.ARGB;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

/**
 * A module which depends on the {@link ftbsc.tspr.services.Scanner} service to provide
 * position of specific blocks in the world
 */
public abstract class ScannerModule extends TogglableModule {

	protected ModConfigSpec.DoubleValue alpha;

	protected abstract Iterable<Block> getBlocks();
	protected abstract Integer getColor(Block block);

	protected void draw(BlockPos pos, int rgb, float alpha) {
		Gizmos.cuboid(pos, GizmoStyle.fill(ARGB.color(alpha, rgb))).setAlwaysOnTop();
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

	protected void doRender(RenderLevelStageEvent.AfterLevel event) {
		if (!this.enabled.getAsBoolean()) {
			return;
		}

		float alpha = (float) this.alpha.getAsDouble();

		for (Block block : this.handledBlocks) {
			Integer color = this.getColor(block);
			if (color == null) continue;
			for (BlockPos pos : Tiramisuper.SCANNER.getAll(block)) {
				this.draw(pos, color, alpha);
			}
		}
	}
}
