package ftbsc.tspr.modules.hud;

import java.util.LinkedList;
import java.util.Queue;

import com.google.auto.service.AutoService;

import ftbsc.tspr.api.ILoadable;
import ftbsc.tspr.api.module.HudModule;
import ftbsc.tspr.asm.events.PacketEvent;
import net.minecraft.ChatFormatting;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.protocol.game.ClientboundSetTimePacket;
import net.minecraft.util.ARGB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.gui.GuiLayer;
import net.neoforged.neoforge.common.ModConfigSpec;

import static ftbsc.tspr.Tiramisuper.mc;

@AutoService(ILoadable.class)
public class InfoDisplay extends HudModule {

	public void config(ModConfigSpec.Builder builder) {
		this.logo = builder
			.comment("show TSPR logo at the top")
			.define("logo", true);
		this.speed = builder
			.comment("show instant and average speed")
			.define("speed", true);
		this.age = builder
			.comment("show world age")
			.define("age", true);
		this.time = builder
			.comment("show current world time")
			.define("time", true);
		this.fps = builder
			.comment("show current frames-per-second")
			.define("fps", true);
		this.ping = builder
			.comment("show current network latency (ping time)")
			.define("ping", true);
		this.tps = builder
			.comment("show calculated server ticks-per-second")
			.define("tps", true);
		this.tps_sample_size = builder
			.comment("how many samples to keep for evaluating average tps")
			.defineInRange("tps-sample-size", 20, 1, Integer.MAX_VALUE);
		this.speed_sample_size = builder
			.comment("how many samples to keep for evaluating average speed")
			.defineInRange("tps-sample-size", 100, 1, Integer.MAX_VALUE);
		this.color = builder
			.comment("color to use")
			.defineEnum("color", ChatFormatting.WHITE);
	}

	public GuiLayer getLayer() {
		return new InfoDisplayLayer(this);
	}

	private Vec3 last_position = new Vec3(0.0, 0.0, 0.0);
	private double instant_speed = 0.0;
	private double average_speed = 0.0;
	private double instant_tps   = 0.0;
	private int    instant_ping  = 0;
	private Queue<Double> speed_history = new LinkedList<>();
	private Queue<Long>   tps_history   = new LinkedList<>();

	private ModConfigSpec.BooleanValue logo;
	private ModConfigSpec.BooleanValue speed;
	private ModConfigSpec.BooleanValue age;
	private ModConfigSpec.BooleanValue time;
	private ModConfigSpec.BooleanValue fps;
	private ModConfigSpec.BooleanValue ping;
	private ModConfigSpec.BooleanValue tps;
	private ModConfigSpec.IntValue tps_sample_size;
	private ModConfigSpec.IntValue speed_sample_size;
	private ModConfigSpec.EnumValue<ChatFormatting> color;
	// private ModConfigSpec.BooleanValue biome;
	// private ModConfigSpec.BooleanValue light;
	// private ModConfigSpec.BooleanValue saturation;
	// private ModConfigSpec.BooleanValue system_time;
	// private ModConfigSpec.BooleanValue damage_value;
	// private ModConfigSpec.BooleanValue effects_list;
	// private ModConfigSpec.BooleanValue item_quantity;
	// private ModConfigSpec.BooleanValue client_chunk_size;
	// private ModConfigSpec.BooleanValue hide_effects;


	@SubscribeEvent
	public void onTick(ClientTickEvent.Post event) {
		if (!this.speed.get()) return;
		if (mc().player != null) {
			this.instant_speed = this.last_position.distanceTo(mc().player.position());
			this.last_position = mc().player.position();
			PlayerInfo info = mc().getConnection().getPlayerInfo(
				mc().player.getGameProfile().id()
			);
			if (info != null) { // bungeecord switching makes this null for a second
				this.instant_ping = info.getLatency();
			}
		} else {
			this.instant_speed = 0.0;
			this.instant_ping = 0;
		}

		this.speed_history.offer(this.instant_speed);
		while (this.speed_history.size() >= this.speed_sample_size.get()) {
			this.speed_history.poll();
		}

		double buf = 0.0;
		for (double v : this.speed_history) { buf += v; }
		this.average_speed = buf / this.speed_history.size();
	}

	@SubscribeEvent
	public void onPacket(PacketEvent.Incoming event) {
		if (event.packet instanceof ClientboundSetTimePacket) {
			this.tps_history.offer(System.currentTimeMillis());
			while (this.tps_history.size() > this.tps_sample_size.get()) {
				this.tps_history.poll();
			}
			double positive_time = 0.;
			double last_time = 0;
			for (long t : this.tps_history) {
				if (last_time != 0) {
					double delta = (double) (t - last_time) / 1000.;
					positive_time += Math.max(delta, 1.);
				}
				last_time = t;
			}
			this.instant_tps = 20 / (positive_time / (this.tps_history.size() - 1));
		}
	}

	// Time utils
	private String getTimePhase(long time) {
		if (time > 23000) return "Dawn";
		if (time > 18500) return "Night";
		if (time > 17500) return "Midnight";
		if (time > 13000) return "Evening";
		if (time > 12000) return "Dusk";
		if (time > 6500) return "Afternoon";
		if (time > 5500) return "Noon";
		return "Morning";
	}

	private int getNextStep(long time) {
		if (time > 23000) return 24000;
		if (time > 18500) return 23000;
		if (time > 17500) return 18500;
		if (time > 13000) return 17500;
		if (time > 12000) return 13000;
		if (time > 6500) return 12000;
		if (time > 5500) return 6500;
		return 5500;
	}

	private final int TPS = 20;

	public class InfoDisplayLayer implements GuiLayer {
		private final InfoDisplay mod;
		public InfoDisplayLayer(InfoDisplay mod) {
			this.mod = mod;
		}
		@Override
		public void render(GuiGraphics gui, DeltaTracker deltaTracker) {
			if (this.mod.shouldHide()) return;

			int y = this.mod.getY();
			int x = this.mod.getX();
			int color = ARGB.opaque(this.mod.color.get().getColor());
			float scale = (float) this.mod.scale.getAsDouble();

			gui.pose().pushMatrix();
			gui.pose().scale(4.f * scale);

			Component logo = Component.literal("TSPR")
				.withStyle(
					Style.EMPTY
						.withBold(true)
						.withColor(ARGB.opaque(0xBF616A))
						.withShadowColor(ARGB.opaque(0x000000))
				);


			if (this.mod.logo.get()) {
				gui.drawString(mc().font, logo, x, y, color, true);
				y = this.mod.inc(y, (mc().font.lineHeight + 1) * 4);
			}


			gui.pose().popMatrix();
			gui.pose().pushMatrix();
			gui.pose().scale(scale);

			long day = 0;
			long time = 0;
			if (mc().level != null) {
				day = mc().level.dayTime() / 24000L;
				time = mc().level.dayTime() % 24000L;
			}

			if (this.mod.fps.get()) {
				gui.drawString(mc().font, this.mod.prefixed("fps: %d", mc().getFps()), x, y, color);
				y = this.mod.inc(y, mc().font.lineHeight + 1);
			}

			if (this.mod.ping.get()) {
				gui.drawString(mc().font, this.mod.prefixed("ping: %d", this.mod.instant_ping), x, y, color);
				y = this.mod.inc(y, mc().font.lineHeight + 1);
			}

			if (this.mod.tps.get()) {
				gui.drawString(mc().font, this.mod.prefixed("tps: %.1f", this.mod.instant_tps), x, y, color);
				y = this.mod.inc(y, mc().font.lineHeight + 1);
			}

			if (this.mod.speed.get()) {
				gui.drawString(mc().font, this.mod.prefixed("speed: %.1f [%.1f] m/s", this.mod.instant_speed * 20.0, this.mod.average_speed * 20.0), x, y, color);
				y = this.mod.inc(y, mc().font.lineHeight + 1);
			}

			if (this.mod.age.get()) {
				gui.drawString(mc().font, this.mod.prefixed("age: %d (~%d days)", day, day / (3 * 24)), x, y, color);
				y = this.mod.inc(y, mc().font.lineHeight + 1);
			}

			if (this.mod.time.get()) {
				gui.drawString(mc().font, this.mod.prefixed("time: %d/%d (%s)", (time / this.mod.TPS), (this.mod.getNextStep(time) / this.mod.TPS), this.mod.getTimePhase(time)), x, y, color);
				y = this.mod.inc(y, mc().font.lineHeight + 1);
			}

			gui.pose().popMatrix();
		}
	}
	
}
