package ftbsc.tspr.asm.patches;

import ftbsc.lll.processor.annotations.Find;
import ftbsc.lll.processor.annotations.Injector;
import ftbsc.lll.processor.annotations.Patch;
import ftbsc.lll.processor.annotations.Target;
import ftbsc.lll.proxies.impl.MethodProxy;
import ftbsc.lll.utils.nodes.MethodProxyInsnNode;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.neoforged.neoforge.common.NeoForge;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import ftbsc.tspr.asm.events.PacketEvent;

@Patch(Connection.class)
public abstract class PacketPatch implements Opcodes {

	@Find(PacketPatch.class)
	public MethodProxy pktIn;

	@Target(of = "pktIn")
	public static boolean pktIn(Packet<?> pkt) {
		return NeoForge.EVENT_BUS.post(new PacketEvent.Incoming(pkt)).isCanceled();
	}

	@Target(of = "injectIncomingInterceptor")
	abstract void channelRead0(ChannelHandlerContext ctx, Packet<?> pak);

	@Injector(reason = "add hook to intercept and alter/cancel incoming packets")
	public void injectIncomingInterceptor(ClassNode clazz, MethodNode main) {
		// hook at the top
		LabelNode skip = new LabelNode();
		InsnList is = new InsnList();
		is.add(new VarInsnNode(ALOAD, 2));
		is.add(new MethodProxyInsnNode(INVOKESTATIC, pktIn));
		is.add(new JumpInsnNode(IFEQ, skip));
		is.add(new InsnNode(RETURN));
		is.add(skip);

		main.instructions.insert(is);
	}

	@Find(PacketPatch.class)
	public MethodProxy pktOut;

	@Target(of = "pktOut")
	public static boolean pktOut(Packet<?> pkt) {
		return NeoForge.EVENT_BUS.post(new PacketEvent.Outgoing(pkt)).isCanceled();
	}

	@Target(of = "injectOutgoingInterceptor")
	public abstract void sendPacket(Packet<?> pak, ChannelFutureListener listener, boolean flush);

	@Injector(reason = "add hook to intercept and alter/cancel outgoing packets")
	public void injectOutgoingInterceptor(ClassNode clazz, MethodNode main) {
		// hook at the top
		LabelNode skip = new LabelNode();
		InsnList is = new InsnList();
		is.add(new VarInsnNode(ALOAD, 1));
		is.add(new MethodProxyInsnNode(INVOKESTATIC, pktOut));
		is.add(new JumpInsnNode(IFEQ, skip));
		is.add(new InsnNode(RETURN));
		is.add(skip);

		main.instructions.insert(is);
	}
}
