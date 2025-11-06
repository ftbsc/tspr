package ftbsc.tspr.asm.events;

import net.minecraft.network.protocol.Packet;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;

public class PacketEvent {
	public static class Outgoing extends Event implements ICancellableEvent {
		public final Packet<?> packet;

		public Outgoing(Packet<?> packet) {
			this.packet = packet;
		}
	}

	public static class Incoming extends Event implements ICancellableEvent {
		public final Packet<?> packet;

		public Incoming(Packet<?> packet) {
			this.packet = packet;
		}
	}
}
