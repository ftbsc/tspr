package ftbsc.tspr.services;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.Nullable;

import ftbsc.tspr.asm.events.PacketEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket;
import net.minecraft.network.protocol.game.ClientboundSectionBlocksUpdatePacket;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.chunk.LevelChunk;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.level.ChunkEvent;
import net.neoforged.neoforge.event.level.LevelEvent;

public final class Scanner {

	private final ExecutorService executor = Executors.newSingleThreadExecutor();

	/**
	 * register a block to be watched on chunk loads
	 */
	public void watch(Block block) {
		this.watched.add(block);
	}

	public Iterable<BlockPos> getAll(Block block) {
		Set<BlockPos> positions = this.blockToPos.get(block);
		return positions == null ? ConcurrentHashMap.newKeySet() : positions;
	}

	public @Nullable Block get(BlockPos pos) {
		return this.posToBlock.get(pos);
	}

	private final Set<Block> watched = ConcurrentHashMap.newKeySet();
	private final Map<BlockPos, Block> posToBlock = new ConcurrentHashMap<>();
	private final Map<Block, Set<BlockPos>> blockToPos = new ConcurrentHashMap<>();

	private void processAdd(BlockPos pos, Block block) {
		if (this.watched.contains(block)) {
			this.posToBlock.put(pos, block);
			this.blockToPos.putIfAbsent(block, ConcurrentHashMap.newKeySet());
			this.blockToPos.compute(block, (k, positions) -> {
				positions.add(pos);
				return positions;
			});
		}
	}

	private void processRemoval(BlockPos pos) {
		Block changed = this.posToBlock.remove(pos);
		if (changed != null) {
			this.blockToPos.computeIfPresent(changed, (k, positions) -> {
				positions.remove(pos);
				return positions;
			});
		}
	}

	private void processChange(BlockPos pos, Block block) {
		this.processRemoval(pos);
		if (this.watched.contains(block)) {
			this.processAdd(pos, block);
		}
	}

	@SubscribeEvent
	void onChunkLoaded(ChunkEvent.Load event) {
		this.executor.submit(() -> {
			LevelChunk chunk = event.getChunk();
			ChunkPos chunkPos = chunk.getPos();

			for (int x = 0; x < 16; x++) {
				for (int y = chunk.getMinY(); y < chunk.getMaxY(); y++) {
					for (int z = 0; z < 16; z++) {
						BlockPos pos = chunkPos.getBlockAt(x, y, z);
						Block block = chunk.getBlockState(pos).getBlock();
						this.processAdd(pos, block);
					}
				}
			}
		});
	}

	@SubscribeEvent
	void onPacket(PacketEvent.Incoming event) {
		this.executor.submit(() -> {
			if (event.packet instanceof ClientboundBlockUpdatePacket packet) {
				this.processChange(packet.getPos(), packet.getBlockState().getBlock());
			}

			if (event.packet instanceof ClientboundSectionBlocksUpdatePacket packet) {
				packet.runUpdates((pos, state) -> this.processChange(new BlockPos(pos), state.getBlock()));
			}
		});
	}

	@SubscribeEvent
	void onChunkUnloaded(ChunkEvent.Unload event) {
		this.executor.submit(() -> {
			ChunkPos chunkPos = event.getChunk().getPos();
			for (BlockPos pos : this.posToBlock.keySet()) {
				if (chunkPos.contains(pos)) {
					this.processRemoval(pos);
				}
			}
		});
	}

	@SubscribeEvent
	void onWorldUnload(LevelEvent.Unload event) {
		this.posToBlock.clear();
		this.blockToPos.clear();
	}
}
