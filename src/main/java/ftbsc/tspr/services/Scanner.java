package ftbsc.tspr.services;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import ftbsc.tspr.asm.events.PacketEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket;
import net.minecraft.network.protocol.game.ClientboundSectionBlocksUpdatePacket;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.level.ChunkEvent;

import static ftbsc.tspr.Tiramisuper.mc;

public final class Scanner {

	private final ExecutorService executor = Executors.newSingleThreadExecutor();

	public void onLoad(Block block, BiConsumer<BlockPos, BlockState> cb) {
		Set<BiConsumer<BlockPos, BlockState>> funcs = this.loadSubscribers.get(block);
		if (funcs == null) {
			funcs = new HashSet<>();
		}
		funcs.add(cb);
		this.loadSubscribers.put(block, funcs);
	}

	public void onUnload(Consumer<ChunkPos> cb) {
		this.unloadSubscribers.add(cb);
	}

	private final Map<Block, Set<BiConsumer<BlockPos, BlockState>>> loadSubscribers = new ConcurrentHashMap<>();
	private final Set<Consumer<ChunkPos>> unloadSubscribers = ConcurrentHashMap.newKeySet();

	private void runCallbacks(BlockPos pos, BlockState state) {
		var callbacks = this.loadSubscribers.get(state.getBlock());
		if (callbacks != null) {
			for (var fun : callbacks) {
				mc().execute(() -> fun.accept(pos, state));
			}
		}
	}

	@SubscribeEvent
	void onChunkLoaded(ChunkEvent.Load event) {
		this.executor.submit(() -> {
			LevelChunk chunk = event.getChunk();
			ChunkPos pos = chunk.getPos();

			for (int x = 0; x < 16; x++) {
				for (int y = chunk.getMinY(); y < chunk.getMaxY(); y++) {
					for (int z = 0; z < 16; z++) {
						BlockPos absPos = pos.getBlockAt(x, y, z);
						BlockState state = chunk.getBlockState(absPos);
						this.runCallbacks(absPos, state);
					}
				}
			}
		});
	}

	@SubscribeEvent
	public void onPacket(PacketEvent.Incoming event) {
		if (event.packet instanceof ClientboundBlockUpdatePacket packet) {
			this.runCallbacks(packet.getPos(), packet.getBlockState());
		}

		if (event.packet instanceof ClientboundSectionBlocksUpdatePacket packet) {
			packet.runUpdates((pos, state) -> this.runCallbacks(pos, state));
		}
	}

	@SubscribeEvent
	void onChunkUnloaded(ChunkEvent.Unload event) {
		this.executor.submit(() -> {
			ChunkPos pos = event.getChunk().getPos();
			for (Consumer<ChunkPos> cb : this.unloadSubscribers) {
				mc().execute(() -> cb.accept(pos));
			}
		});
	}
}
