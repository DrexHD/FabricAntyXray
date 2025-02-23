package me.drex.antixray.common.util;

import me.drex.antixray.common.interfaces.IClientboundChunkBatchStartPacket;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;

/**
 * Some of these arguments are used by multiple methods. This allows them to be accessed by all called methods, but
 * may cause them to be present 'unexpectedly'.
 */
public class Arguments {
    public static final ThreadLocal<ChunkPacketInfo<BlockState>> PACKET_INFO = new ThreadLocal<>();
    public static final ThreadLocal<ChunkAccess> CHUNK_ACCESS = new ThreadLocal<>();
    public static final ThreadLocal<Integer> CHUNK_SECTION_INDEX = new ThreadLocal<>();
    public static final ThreadLocal<Object[]> PRESET_VALUES = new ThreadLocal<>();
    public static final ThreadLocal<IClientboundChunkBatchStartPacket> BATCH_START_PACKET = new ThreadLocal<>();
    public static final ThreadLocal<ServerGamePacketListenerImpl> PACKET_LISTENER = new ThreadLocal<>();
}
