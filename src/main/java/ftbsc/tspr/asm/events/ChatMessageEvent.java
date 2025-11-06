package ftbsc.tspr.asm.events;

import net.minecraft.network.chat.Component;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;

public class ChatMessageEvent extends Event implements ICancellableEvent {
	public Component message;

	public ChatMessageEvent(Component message) {
		this.message = message;
	}
}
