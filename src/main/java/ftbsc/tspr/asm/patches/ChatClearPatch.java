package ftbsc.tspr.asm.patches;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodNode;

import ftbsc.lll.processor.annotations.Find;
import ftbsc.lll.processor.annotations.Injector;
import ftbsc.lll.processor.annotations.Patch;
import ftbsc.lll.processor.annotations.Target;
import ftbsc.lll.proxies.impl.MethodProxy;
import ftbsc.lll.utils.nodes.MethodProxyInsnNode;
import ftbsc.tspr.asm.events.ChatClearEvent;
import net.minecraft.client.gui.components.ChatComponent;
import net.neoforged.neoforge.common.NeoForge;

@Patch(ChatComponent.class)
public abstract class ChatClearPatch implements Opcodes {

	@Find(ChatClearPatch.class)
	public MethodProxy onChatClear;

	@Target(of = "onChatClear")
	public static boolean onChatClear() {
		return NeoForge.EVENT_BUS.post(new ChatClearEvent()).isCanceled();
	}

	@Target(of = "injectChatClear")
	public abstract void clearMessages(boolean clearSentMsgHistory);

	@Injector(reason = "add hook to intercept chat clearing")
	public void injectChatClear(ClassNode clazz, MethodNode main) {
		// inject at top of method
		LabelNode skip = new LabelNode();
		InsnList is = new InsnList();
		is.add(new MethodProxyInsnNode(INVOKESTATIC, this.onChatClear));
		is.add(new JumpInsnNode(IFEQ, skip));
		is.add(new InsnNode(RETURN));
		is.add(skip);
		main.instructions.insert(is);
	}
}
