package me.drex.antixray.common.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import me.drex.antixray.common.interfaces.IChunkPacket;
import me.drex.antixray.common.interfaces.IClientboundChunkBatchStartPacket;
import me.drex.antixray.common.util.Arguments;
import me.drex.antixray.common.util.ChunkPacketInfo;
import me.drex.antixray.common.util.Util;
import me.drex.antixray.common.util.controller.ChunkPacketBlockController;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundLevelChunkPacketData;
import net.minecraft.network.protocol.game.ClientboundLevelChunkWithLightPacket;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.lighting.LevelLightEngine;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.BitSet;

@Mixin(ClientboundLevelChunkWithLightPacket.class)
public abstract class ClientboundLevelChunkWithLightPacketMixin implements IChunkPacket {

    @Unique
    boolean antixray$ready = false;

    @Unique
    IClientboundChunkBatchStartPacket antixray$batchStartPacket;

    @WrapOperation(
        method = "<init>(Lnet/minecraft/world/level/chunk/LevelChunk;Lnet/minecraft/world/level/lighting/LevelLightEngine;Ljava/util/BitSet;Ljava/util/BitSet;)V",
        at = @At(
            value = "NEW",
            target = "(Lnet/minecraft/world/level/chunk/LevelChunk;)Lnet/minecraft/network/protocol/game/ClientboundLevelChunkPacketData;"
        )
    )
    public ClientboundLevelChunkPacketData setChunkPacketInfoArgument(
        LevelChunk chunk, Operation<ClientboundLevelChunkPacketData> original,
        @Share("controller") LocalRef<ChunkPacketBlockController> controllerLocalRef,
        @Share("chunkPacketInfo") LocalRef<ChunkPacketInfo<BlockState>> chunkPacketInfoLocalRef
    ) {
        // custom argument
        this.antixray$batchStartPacket = Arguments.BATCH_START_PACKET.get();
        var packetListener = Arguments.PACKET_LISTENER.get();
        final ChunkPacketBlockController controller;
        if (packetListener != null) {
            controller = Util.getBlockController(packetListener.player);
        } else {
            // Chunk packets may not have the packet listener argument, if they are manually sent by other mods
            controller = Util.getBlockController(chunk.getLevel());
        }

        final ChunkPacketInfo<BlockState> packetInfo = controller.getChunkPacketInfo((ClientboundLevelChunkWithLightPacket) (Object) this, chunk);

        controllerLocalRef.set(controller);
        chunkPacketInfoLocalRef.set(packetInfo);

        var previous = Arguments.PACKET_INFO.get();
        Arguments.PACKET_INFO.set(packetInfo);
        try {
            return original.call(chunk);
        } finally {
            Arguments.PACKET_INFO.set(previous);
        }
    }

    @Inject(
        method = "<init>(Lnet/minecraft/world/level/chunk/LevelChunk;Lnet/minecraft/world/level/lighting/LevelLightEngine;Ljava/util/BitSet;Ljava/util/BitSet;)V",
        at = @At("TAIL")
    )
    public void modifyBlocks(
        LevelChunk chunk, LevelLightEngine levelLightEngine, BitSet bitSet, BitSet bitSet2, CallbackInfo ci,
        @Share("controller") LocalRef<ChunkPacketBlockController> controllerLocalRef,
        @Share("chunkPacketInfo") LocalRef<ChunkPacketInfo<BlockState>> chunkPacketInfoLocalRef
    ) {
        controllerLocalRef.get().modifyBlocks((ClientboundLevelChunkWithLightPacket) (Object) this, chunkPacketInfoLocalRef.get());
    }

    @Inject(
        method = "<init>(Lnet/minecraft/network/RegistryFriendlyByteBuf;)V",
        at = @At("TAIL")
    )
    public void markReady(RegistryFriendlyByteBuf registryFriendlyByteBuf, CallbackInfo ci) {
        this.antixray$ready = true;
    }

    @Override
    public boolean isAntixray$ready() {
        return antixray$ready;
    }

    @Override
    public void antixray$setReady(boolean antixray$ready) {
        this.antixray$ready = antixray$ready;
        if (antixray$batchStartPacket != null) {
            // Chunk packets may not have a batch start packet, if they are manually sent by other mods
            antixray$batchStartPacket.antixray$notifyChunkReady();
        }
    }


}
