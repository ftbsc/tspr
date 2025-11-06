package ftbsc.tspr.asm.patches;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import ftbsc.tspr.asm.events.ChatInputEvent;
import ftbsc.lll.processor.annotations.Find;
import ftbsc.lll.processor.annotations.Injector;
import ftbsc.lll.processor.annotations.Patch;
import ftbsc.lll.processor.annotations.Target;
import ftbsc.lll.proxies.impl.MethodProxy;
import ftbsc.lll.utils.nodes.MethodProxyInsnNode;
import net.minecraft.client.gui.screens.ChatScreen;
import net.neoforged.neoforge.common.NeoForge;

@Patch(ChatScreen.class)
public abstract class ChatInputPatch implements Opcodes {

	@Target(of = "injectChatInput")
	public abstract void handleChatInput(String message, boolean addToRecentChat);


	@Injector(reason = "add hook to intercept client chat messages")
	public void injectChatInput(ClassNode clazz, MethodNode main) {
		// inject at top of method
		LabelNode skip = new LabelNode();
		InsnList is = new InsnList();
		is.add(new VarInsnNode(ALOAD, 1));
		is.add(new MethodProxyInsnNode(INVOKESTATIC, this.chatInput));
		is.add(new JumpInsnNode(IFEQ, skip));
		is.add(new InsnNode(RETURN));
		is.add(skip);

		main.instructions.insert(is);
	}

	@Find(ChatInputPatch.class)
	public MethodProxy chatInput;

	@Target(of = "chatInput")
	public static boolean chatInput(String message) {
		return NeoForge.EVENT_BUS.post(new ChatInputEvent(message)).isCanceled();
	}
}
