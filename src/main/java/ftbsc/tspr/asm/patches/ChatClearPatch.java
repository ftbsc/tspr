package ftbsc.tspr.asm.patches;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodNode;

import ftbsc.lll.processor.annotations.Injector;
import ftbsc.lll.processor.annotations.Patch;
import ftbsc.lll.processor.annotations.Target;
import net.minecraft.client.gui.components.ChatComponent;

// TODO put an option somewhere to allow chat clearing, don't just disable it

@Patch(ChatComponent.class)
public abstract class ChatClearPatch implements Opcodes {

	@Target(of = "injectChatClear")
	public abstract void clearMessages(boolean clearSentMsgHistory);

	@Injector(reason = "add hook to intercept chat clearing")
	public void injectChatClear(ClassNode clazz, MethodNode main) {
		// inject at top of method
		InsnList is = new InsnList();
		is.add(new InsnNode(RETURN));
		main.instructions.insert(is);
	}
}
