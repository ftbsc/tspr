package ftbsc.tspr.asm.events;

import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;

/**
 * Event fired when local user sends a message in the chat box, before processing it.
 * If cancelled, won't be sent to the server
 */
public class ChatInputEvent extends Event implements ICancellableEvent {
	/** the input message text */
	public final String message;

	/** make a new instance, given the input message */
	public ChatInputEvent(String message) {
		this.message = message;
	}
}
