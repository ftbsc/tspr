package ftbsc.tspr.asm.events;

import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;

public class ChatClearEvent extends Event implements ICancellableEvent {
	public ChatClearEvent() {}
}
