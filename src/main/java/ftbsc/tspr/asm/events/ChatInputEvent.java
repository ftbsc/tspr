package ftbsc.tspr.asm.events;

import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;

public class ChatInputEvent extends Event implements ICancellableEvent {
	public final String message;

	public ChatInputEvent(String message) {
		this.message = message;
	}
}
