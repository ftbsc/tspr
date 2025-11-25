package ftbsc.tspr.asm.events;

import net.minecraft.network.protocol.Packet;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;

/**
 * Fired before a packet is sent to the server, or after a packet arrives
 * Use either {@link Outgoing} or {@link Incoming}
 */
public abstract class PacketEvent extends Event{
	/**
	 * Fired before a packet is sent to the server.
	 * Allows modifying packet data, or preventing its delivery
	 * completely by cancelling the event
	 */
	public static class Outgoing extends PacketEvent implements ICancellableEvent {
		/** the outgoing packet */
		public final Packet<?> packet;

		/** create a new instance from given packet */
		public Outgoing(Packet<?> packet) {
			this.packet = packet;
		}
	}

	/**
	 * Fired before a packet received from the server is processed.
	 * Allows modifying packet data, or preventing it from being processed
	 * by cancelling the event
	 */
	public static class Incoming extends PacketEvent implements ICancellableEvent {
		/** the incoming packet */
		public final Packet<?> packet;

		/** create a new instance from given packet */
		public Incoming(Packet<?> packet) {
			this.packet = packet;
		}
	}
}
