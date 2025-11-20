package ftbsc.tspr.modules.vision;

import java.util.concurrent.ConcurrentLinkedQueue;


import com.google.auto.service.AutoService;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShapeRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket;
import net.minecraft.network.protocol.game.ClientboundSectionBlocksUpdatePacket;
import net.minecraft.util.Tuple;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.common.ModConfigSpec;
import ftbsc.tspr.api.ILoadable;
import ftbsc.tspr.api.module.TogglableModule;
import ftbsc.tspr.asm.events.PacketEvent;

@AutoService(ILoadable.class)
public class BlockUpdates extends TogglableModule {

	private ModConfigSpec.IntValue duration;
	private ModConfigSpec.DoubleValue alpha;

	// TODO allow to customize color

	private final ConcurrentLinkedQueue<Tuple<BlockPos, Long>> updates = new ConcurrentLinkedQueue<>();

	public void config(ModConfigSpec.Builder builder) {
		this.duration = builder
			.comment("how long to show block updates (in ms)")
			.defineInRange("duration", 250, 0, Integer.MAX_VALUE);

		this.alpha = builder
			.comment("alpha channel value for highlights")
			.defineInRange("alpha", .75, 0., 1.);
	}


	private float getAlpha(float multiplier, long insert, long now, long duration) {
		long age = now - insert;
		if (age > duration) return 0.f;
		return multiplier * (1.f - ((float) age / (float) duration));
	}

	@SubscribeEvent
	void onRenderLevelStage(RenderLevelStageEvent.AfterEntities event) {
		if (!this.enabled.getAsBoolean() || MC.getDebugOverlay().showDebugScreen()) {
			return;
		}

		PoseStack poseStack = event.getPoseStack();
		Vec3 camera = event.getLevelRenderState().cameraRenderState.pos;
		VertexConsumer consumer = MC.renderBuffers().bufferSource().getBuffer(RenderType.lines());

		for (Tuple<BlockPos, Long> entry : this.updates) {
			float alpha = this.getAlpha((float) this.alpha.getAsDouble(), entry.getB(), System.currentTimeMillis(), this.duration.get());
			Vec3 offset = Vec3.atLowerCornerOf(entry.getA()).subtract(camera);
			poseStack.pushPose();
			poseStack.translate(offset.x, offset.y, offset.z);
			ShapeRenderer.renderLineBox(poseStack.last(), consumer, new AABB(0, 0, 0, 1, 1, 1), 1F, 1F, 1F, alpha);
			poseStack.popPose();
		}
	}

	@SubscribeEvent
	public void onTick(ClientTickEvent.Pre event) {
		long time = System.currentTimeMillis();
		long duration = this.duration.get();
		while (this.updates.peek() != null && time - this.updates.peek().getB() > duration) {
			this.updates.poll();
		}
	}


	@SubscribeEvent
	public void onPacket(PacketEvent.Incoming event) {
		if (!this.enabled.get()) return;
		if (event.packet instanceof ClientboundBlockUpdatePacket) {
			ClientboundBlockUpdatePacket packet = (ClientboundBlockUpdatePacket) event.packet;
			this.updates.add(new Tuple<>(packet.getPos(), System.currentTimeMillis()));
		}

		if (event.packet instanceof ClientboundSectionBlocksUpdatePacket) {
			ClientboundSectionBlocksUpdatePacket packet = (ClientboundSectionBlocksUpdatePacket) event.packet;
			packet.runUpdates( (pos, state) -> this.updates.add(new Tuple<>(new BlockPos(pos), System.currentTimeMillis())) );
		}
	}
}
