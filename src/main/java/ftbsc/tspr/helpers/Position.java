package ftbsc.tspr.helpers;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public class Position {
	public static BlockPos clampToBlock(Vec3 pos) {
		return new BlockPos(
			Mth.floor(pos.x),
			Mth.floor(pos.y),
			Mth.floor(pos.z)
		);
	}
}
