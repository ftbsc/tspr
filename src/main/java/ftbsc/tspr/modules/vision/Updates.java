package ftbsc.tspr.modules.vision;

import java.util.concurrent.ConcurrentLinkedQueue;


import com.google.auto.service.AutoService;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.gizmos.GizmoStyle;
import net.minecraft.gizmos.Gizmos;
import net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket;
import net.minecraft.network.protocol.game.ClientboundSectionBlocksUpdatePacket;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.event.level.LevelEvent;
import ftbsc.tspr.api.ILoadable;
import ftbsc.tspr.api.module.TogglableModule;
import ftbsc.tspr.asm.events.PacketEvent;
import ftbsc.tspr.helpers.Color;
import ftbsc.tspr.helpers.Position;
import ftbsc.tspr.helpers.Tuple;

@AutoService(ILoadable.class)
public class Updates extends TogglableModule {

	private ModConfigSpec.IntValue duration;
	private ModConfigSpec.DoubleValue alpha;
	private ModConfigSpec.EnumValue<ChatFormatting> color;

	private final ConcurrentLinkedQueue<Tuple<BlockPos, Long>> updates = new ConcurrentLinkedQueue<>();

	@Override
	public void config(ModConfigSpec.Builder builder) {
		this.duration = builder
			.comment("how long to show block updates (in ms)")
			.defineInRange("duration", 250, 0, Integer.MAX_VALUE);

		this.alpha = builder
			.comment("alpha channel value for highlights")
			.defineInRange("alpha", .75, 0., 1.);

		this.color = builder
			.comment("color to use for block outlines")
			.defineEnum("color", ChatFormatting.WHITE);
	}


	private float getAlpha(float multiplier, long insert, long now, long duration) {
		long age = now - insert;
		if (age > duration) return 0.f;
		return multiplier * (1.f - ((float) age / (float) duration));
	}

	@SubscribeEvent
	void onRenderLevelStage(RenderLevelStageEvent.AfterLevel event) {
		if (!this.enabled.getAsBoolean()) {
			return;
		}

		for (Tuple<BlockPos, Long> entry : this.updates) {
			float alpha = this.getAlpha((float) this.alpha.getAsDouble(), entry.b, System.currentTimeMillis(), this.duration.get());
			int color_stroke = Color.pack(this.color.get(), alpha);
			int color_fill = Color.pack(this.color.get(), alpha / 2.f);
			Gizmos.cuboid(entry.a, GizmoStyle.strokeAndFill(color_stroke, 1.f, color_fill)).setAlwaysOnTop();
		}
	}

	@SubscribeEvent
	public void onTick(ClientTickEvent.Pre event) {
		long time = System.currentTimeMillis();
		long duration = this.duration.get();
		while (this.updates.peek() != null && time - this.updates.peek().b > duration) {
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
			packet.runUpdates( (pos, state) -> this.updates.add(new Tuple<>(Position.clone(pos), System.currentTimeMillis())) );
		}
	}

	@SubscribeEvent
	void onWorldUnload(LevelEvent.Unload event) {
		this.updates.clear();
	}
}
