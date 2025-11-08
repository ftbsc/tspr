package ftbsc.tspr.modules.defense;

import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

import com.google.auto.service.AutoService;

import ftbsc.tspr.api.ILoadable;
import ftbsc.tspr.api.module.TogglableModule;

@AutoService(ILoadable.class)
public class Aura extends TogglableModule {

	private enum LookType {
		NONE,
		PACKET,
		ONCE,
		// RESET
	}

	private ModConfigSpec.DoubleValue reach;
	private ModConfigSpec.DoubleValue strength;
	// private ModConfigSpec.BooleanValue tool;
	private ModConfigSpec.BooleanValue trace;
	private ModConfigSpec.EnumValue<LookType> look;
	private ModConfigSpec.BooleanValue swing;
	private ModConfigSpec.BooleanValue neutral;
	// private ModConfigSpec.BooleanValue friends;

	public void config(ModConfigSpec.Builder builder) {
		this.reach = builder
			.comment("max reach range for attacking")
			.defineInRange("reach", 4., 0., Double.MAX_VALUE);
		this.strength = builder
			.comment("minimum attack strenght to wait before attacking")
			.defineInRange("strength", 1., 0., 1.);
		this.trace = builder
			.comment("only attack entities that can be seen (raytraced)")
			.define("trace", true);
		this.look = builder
			.comment("how to look at target before attacking")
			.defineEnum("look", LookType.ONCE);
		this.swing = builder
			.comment("should arm be moved while attacking")
			.define("swing", true);
		this.neutral = builder
			.comment("also attack non-hostile entities")
			.define("neutral", false);
	}

	private void lookAtHidden(EntityAnchorArgument.Anchor anchor, Vec3 target) {
		// This code comes from vanilla Minecraft, but we send a packet rather than turning player
		Vec3 translated = anchor.apply(MC.player);
		double d0 = target.x - translated.x;
		double d1 = target.y - translated.y;
		double d2 = target.z - translated.z;
		double d3 = (double) Math.sqrt(d0 * d0 + d2 * d2);
		double xRot = (-(Math.atan2(d1, d3) * (180. / Math.PI))) % 360.;
		double yRot = ((Math.atan2(d2, d0) * (180. / Math.PI)) - 90.) % 360.;
		MC.player.connection.send(new ServerboundMovePlayerPacket.Rot(
			(float) xRot, (float) yRot, MC.player.onGround(), MC.player.horizontalCollision
		));
	}

	@SubscribeEvent
	public void onTick(ClientTickEvent.Pre event) {
		if (!this.isEnabled()) return;
		if (MC.level == null) return;
		if (MC.player == null) return;

		if (MC.player.getAttackStrengthScale(0.f) < this.strength.get()) return;

		float distance = Float.MAX_VALUE;
		Entity target = null;

		for (Entity e : MC.level.entitiesForRendering()) {
			if (e.equals(MC.player)) continue;
			if (!(e instanceof LivingEntity)) continue;
			if (!e.isAlive()) continue;
			if (e.distanceTo(MC.player) > this.reach.get()) continue;
			if (!this.neutral.get() && e.getClassification(false).isFriendly()) continue;
			// if (e instanceof PlayerEntity) {
			// 	PlayerEntity player = (PlayerEntity) e;
			// 	if (Boscovicino.friends().isFriend(player.getGameProfile().getId())) {
			// 		continue;
			// 	}
			// }
			if (this.trace.get() && !MC.player.hasLineOfSight(e)) continue;

			float dist = MC.player.distanceTo(e);
			if (dist < distance) {
				distance = dist;
				target = e;
			}
		}

		if (target != null) {
			switch (this.look.get()) {
				case ONCE:
					MC.player.lookAt(EntityAnchorArgument.Anchor.EYES, target.getEyePosition(1.0F));
					MC.player.connection.send(new ServerboundMovePlayerPacket.Rot(
						MC.player.getYRot(), MC.player.getXRot(), MC.player.onGround(), MC.player.horizontalCollision
					));
					break;
				case PACKET:
					this.lookAtHidden(EntityAnchorArgument.Anchor.EYES, target.getEyePosition(1.0F));
					break;
				case NONE: break;
			}

			// if (this.tool.get()) {
			// 	this.autotool.selectBestWeapon();
			// }

			MC.gameMode.attack(MC.player, target);
			if (this.swing.get()) {
				MC.player.swing(InteractionHand.MAIN_HAND);
			}
		}
	}
}
