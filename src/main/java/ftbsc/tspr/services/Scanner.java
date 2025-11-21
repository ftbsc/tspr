package ftbsc.tspr.services;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.BiConsumer;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.level.ChunkEvent;

import static ftbsc.tspr.Tiramisuper.mc;

public final class Scanner {

	private final ExecutorService executor = Executors.newSingleThreadExecutor();

	public void onLoad(Block block, BiConsumer<BlockPos, BlockState> fun) {
		Set<BiConsumer<BlockPos, BlockState>> funcs = this.loadSubscribers.get(block);
		if (funcs == null) {
			funcs = new HashSet<>();
		}
		funcs.add(fun);
		this.loadSubscribers.put(block, funcs);
	}

	public void onUnload(Block block, BiConsumer<BlockPos, BlockState> fun) {
		Set<BiConsumer<BlockPos, BlockState>> funcs = this.unloadSubscribers.get(block);
		if (funcs == null) {
			funcs = new HashSet<>();
		}
		funcs.add(fun);
		this.unloadSubscribers.put(block, funcs);
	}

	private final Map<Block, Set<BiConsumer<BlockPos, BlockState>>> loadSubscribers = new ConcurrentHashMap<>();
	private final Map<Block, Set<BiConsumer<BlockPos, BlockState>>> unloadSubscribers = new ConcurrentHashMap<>();

	@SubscribeEvent
	void onChunkLoaded(ChunkEvent.Load event) {
		this.executor.submit(() -> this.onChunkEvent(event));
	}

	@SubscribeEvent
	void onChunkUnloaded(ChunkEvent.Unload event) {
		this.executor.submit(() -> this.onChunkEvent(event));
	}

	private void onChunkEvent(ChunkEvent<LevelChunk> event) {
		LevelChunk chunk = event.getChunk();
		ChunkPos pos = chunk.getPos();

		var subscribers = switch(event) {
			case ChunkEvent.Load l: yield this.loadSubscribers;
			case ChunkEvent.Unload u: yield this.unloadSubscribers;
			default: yield null;
		};

		if (subscribers == null) return;

		for (int x = 0; x < 16; x++) {
			for (int y = chunk.getMinY(); y < chunk.getMaxY(); y++) {
				for (int z = 0; z < 16; z++) {
					BlockPos absPos = pos.getBlockAt(x, y, z);
					BlockState state = chunk.getBlockState(absPos);

					var callbacks = subscribers.get(state.getBlock());
					if (callbacks != null) {
						for (var fun : callbacks) {
							mc().execute(() -> fun.accept(absPos, state));
						}
					}
				}
			}
		}
	}
}
