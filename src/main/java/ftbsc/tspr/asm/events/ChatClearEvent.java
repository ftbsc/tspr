package ftbsc.tspr.asm.events;

import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;

/**
 * Event fired before the chat is cleared.
 * If cancelled, chat history is preserved
 */
public class ChatClearEvent extends Event implements ICancellableEvent {
}
