package ftbsc.tspr.helpers;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

/**
 * Utility to clamp precise positions to {@link BlockPos}
 */
public class Position {

	/**
	 * Converts a {@link Vec3} into a {@link BlockPos} by flooring down all its components
	 */
	public static BlockPos clampToBlock(Vec3 pos) {
		return new BlockPos(
			Mth.floor(pos.x),
			Mth.floor(pos.y),
			Mth.floor(pos.z)
		);
	}

	/**
	 * Clone an existing {@link BlockPos}
	 */
	public static BlockPos clone(BlockPos pos) {
		return new BlockPos(
			pos.getX(),
			pos.getY(),
			pos.getZ()
		);
	}
}
