package ftbsc.tspr.asm.patches;

import javax.annotation.Nullable;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import ftbsc.tspr.asm.events.ChatMessageEvent;
import ftbsc.lll.processor.annotations.Find;
import ftbsc.lll.processor.annotations.Injector;
import ftbsc.lll.processor.annotations.Patch;
import ftbsc.lll.processor.annotations.Target;
import ftbsc.lll.proxies.impl.MethodProxy;
import ftbsc.lll.utils.nodes.MethodProxyInsnNode;
import net.minecraft.client.GuiMessageTag;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MessageSignature;
import net.neoforged.neoforge.common.NeoForge;

@Patch(ChatComponent.class)
public abstract class ChatMessagePatch implements Opcodes {

	@Target(of = "injectChatMessage")
	public abstract void addMessage(Component chatComponent, @Nullable MessageSignature headerSignature, @Nullable GuiMessageTag tag);


	@Injector(reason = "add hook to intercept received chat messages")
	public void injectChatMessage(ClassNode clazz, MethodNode main) {
		// inject at top of method
		LabelNode skip = new LabelNode();
		InsnList is = new InsnList();
		is.add(new VarInsnNode(ALOAD, 1));
		is.add(new MethodProxyInsnNode(INVOKESTATIC, this.chatMessage));
		is.add(new InsnNode(DUP));
		is.add(new JumpInsnNode(IFNONNULL, skip));
		is.add(new InsnNode(RETURN));
		is.add(skip);
		is.add(new VarInsnNode(ASTORE, 1));

		main.instructions.insert(is);
	}

	@Find(ChatMessagePatch.class)
	public MethodProxy chatMessage;

	@Target(of = "chatMessage")
	public static @Nullable Component chatInput(Component message) {
		ChatMessageEvent event = NeoForge.EVENT_BUS.post(new ChatMessageEvent(message));
		return event.isCanceled() ? null : event.message;
	}
}
