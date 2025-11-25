package ftbsc.tspr.asm.events;

import net.minecraft.network.chat.Component;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;

/**
 * Event fired before a new chat message (from players, server or local commands) is
 * added to the chat log. If cancelled, won't be added
 */
public class ChatMessageEvent extends Event implements ICancellableEvent {
	/** the message being added */
	public Component message;

	/** create a new instance from given message */
	public ChatMessageEvent(Component message) {
		this.message = message;
	}
}
