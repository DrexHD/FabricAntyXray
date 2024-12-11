package me.drex.antixray.neoforge;

import me.drex.antixray.common.AntiXray;
import me.drex.antixray.common.util.Platform;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.server.permission.PermissionAPI;
import net.neoforged.neoforge.server.permission.events.PermissionGatherEvent;
import net.neoforged.neoforge.server.permission.nodes.PermissionNode;
import net.neoforged.neoforge.server.permission.nodes.PermissionTypes;

import java.nio.file.Path;

public class AntiXrayNeoforge extends AntiXray {
    public static final PermissionNode<Boolean> ANTIXRAY_BYPASS = new PermissionNode<>(MOD_ID, "bypass", PermissionTypes.BOOLEAN, (player, playerUUID, context) -> false);

    public AntiXrayNeoforge() {
        super(Platform.NEOFORGE);
        NeoForge.EVENT_BUS.register(this);
    }

    @Override
    public Path getConfigDirectory() {
        return FMLPaths.CONFIGDIR.get();
    }

    @Override
    public String getConfigFileName() {
        return "antixray-neoforge.toml";
    }

    @Override
    public boolean hasBypassPermission(ServerPlayer player) {
        return PermissionAPI.getPermission(player, ANTIXRAY_BYPASS);
    }

    @SubscribeEvent
    public void handlePermissionNodesGather(PermissionGatherEvent.Nodes event) {
        event.addNodes(ANTIXRAY_BYPASS);
    }
}
