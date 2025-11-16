package ftbsc.tspr.modules.hud;

import java.util.LinkedList;
import java.util.Queue;

import com.google.auto.service.AutoService;

import ftbsc.tspr.api.ILoadable;
import ftbsc.tspr.api.module.HudModule;
import ftbsc.tspr.asm.events.PacketEvent;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSetTimePacket;
import net.minecraft.util.ARGB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.gui.GuiLayer;
import net.neoforged.neoforge.common.ModConfigSpec;

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
	// private ModConfigSpec.BooleanValue biome;
	// private ModConfigSpec.BooleanValue light;
	// private ModConfigSpec.BooleanValue saturation;
	// private ModConfigSpec.BooleanValue system_time;
	// private ModConfigSpec.BooleanValue damage_value;
	// private ModConfigSpec.BooleanValue effects_list;
	// private ModConfigSpec.BooleanValue item_quantity;
	// private ModConfigSpec.BooleanValue client_chunk_size;
	// private ModConfigSpec.BooleanValue hide_effects;

	private ModConfigSpec.ConfigValue<Integer> tps_sample_size;
	private ModConfigSpec.ConfigValue<Integer> speed_sample_size;

	@SubscribeEvent
	public void onTick(ClientTickEvent.Post event) {
		if (!this.speed.get()) return;
		if (MC.player != null) {
			this.instant_speed = this.last_position.distanceTo(MC.player.position());
			this.last_position = MC.player.position();
			PlayerInfo info = MC.getConnection().getPlayerInfo(
				MC.player.getGameProfile().getId()
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

		if (this.last_fps_string != MC.fpsString) {
			this.last_fps_string = MC.fpsString;
			this.curr_fps = this.last_fps_string.split(" ")[0];
		}
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

	private String last_fps_string;
	private String curr_fps = "0";

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
			if (!this.mod.isEnabled()) return;

			int y = this.mod.getY();
			int x = this.mod.getX();

			if (this.mod.logo.get()) {
				gui.drawStringWithBackdrop(MC.font, Component.literal("TSPR"), x, y, 4, ARGB.opaque(0xBF616A));
				y += MC.font.lineHeight + 1;
			}

			long day = 0;
			long time = 0;
			if (MC.level != null) {
				day = MC.level.dayTime() / 24000L;
				time = MC.level.dayTime() % 24000L;
			}

			if (this.mod.fps.get()) {
				gui.drawString(MC.font, String.format("> fps: %s", this.mod.curr_fps), x, y, ARGB.opaque(0xFFFFFF));
				y += MC.font.lineHeight + 1;
			}

			if (this.mod.ping.get()) {
				gui.drawString(MC.font, String.format("> ping: %d", this.mod.instant_ping), x, y, ARGB.opaque(0xFFFFFF));
				y += MC.font.lineHeight + 1;
			}

			if (this.mod.tps.get()) {
				gui.drawString(MC.font, String.format("> tps: %.1f", this.mod.instant_tps), x, y, ARGB.opaque(0xFFFFFF));
				y += MC.font.lineHeight + 1;
			}

			if (this.mod.speed.get()) {
				gui.drawString(MC.font, String.format("> speed: %.1f [%.1f] m/s", this.mod.instant_speed * 20.0, this.mod.average_speed * 20.0), x, y, ARGB.opaque(0xFFFFFF));
				y += MC.font.lineHeight + 1;
			}

			if (this.mod.age.get()) {
				gui.drawString(MC.font, String.format("> age: %d (~%d days)", day, day / (3 * 24)), x, y, ARGB.opaque(0xFFFFFF));
				y += MC.font.lineHeight + 1;
			}

			if (this.mod.time.get()) {
				gui.drawString(MC.font, String.format("> time: %d/%d (%s)", (time / this.mod.TPS), this.mod.getNextStep(time), this,mod.getTimePhase(time)), x, y, ARGB.opaque(0xFFFFFF));
				y += MC.font.lineHeight + 1;
			}
		}
	}
	
}
