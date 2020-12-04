package fr.mrcubee.skinchanger;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import fr.mrcubee.bukkit.packet.*;
import fr.mrcubee.bukkit.player.PlayerInfoAction;
import fr.mrcubee.bukkit.player.PlayerUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Map;

public class SkinUtils {

    private static void playerSendPacketToOther(Player ignore, GenericOutPacket... packets) {
        if (ignore == null || packets == null)
            return;
        for (int i = 0; i < packets.length; i++)
            if (packets[i] == null)
                return;
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!player.equals(ignore)) {
                for (GenericOutPacket packet : packets)
                    packet.sendPlayer(player);
            }
        }
    }

    private static void playerSendPacketToSelf(Player target, GenericOutPacket... packets) {
        if (target == null || packets == null)
            return;
        for (int i = 0; i < packets.length; i++)
            if (packets[i] == null)
                return;
        for (GenericOutPacket packet : packets)
            packet.sendPlayer(target);
    }

    private static GenericOutPacket getRemovePlayerInfo(Player player) {
        GenericPacketPlayOutPlayerInfo removePlayerInfo;

        if (player == null)
            return null;
        removePlayerInfo = GenericPacketPlayOutPlayerInfo.create();
        if (removePlayerInfo == null)
            return null;
        removePlayerInfo.addPlayer(player);
        removePlayerInfo.setAction(PlayerInfoAction.REMOVE_PLAYER);
        return removePlayerInfo;
    }

    private static GenericOutPacket getRemovePlayer(Player player) {
        GenericPacketPlayOutEntityDestroy entityDestroy;

        if (player == null)
            return null;
        entityDestroy = GenericPacketPlayOutEntityDestroy.create();
        if (entityDestroy == null)
            return null;
        entityDestroy.setEntityID(player.getEntityId());
        return entityDestroy;
    }

    private static GenericOutPacket getAddPlayerInfo(Player player) {
        GenericPacketPlayOutPlayerInfo removePlayerInfo;

        if (player == null)
            return null;
        removePlayerInfo = GenericPacketPlayOutPlayerInfo.create();
        if (removePlayerInfo == null)
            return null;
        removePlayerInfo.addPlayer(player);
        removePlayerInfo.setAction(PlayerInfoAction.ADD_PLAYER);
        return removePlayerInfo;
    }

    private static GenericOutPacket getAddPlayer(Player player) {
        GenericPacketPlayOutNamedEntitySpawn namedEntitySpawn;

        if (player == null)
            return null;
        namedEntitySpawn = GenericPacketPlayOutNamedEntitySpawn.create();
        if (namedEntitySpawn == null)
            return null;
        namedEntitySpawn.fillAllFromPlayer(player);
        return namedEntitySpawn;
    }

    private static GenericOutPacket getRespawnPlayer(Player player) {
        GenericPacketPlayOutRespawn respawn;

        if (player == null)
            return null;
        respawn = GenericPacketPlayOutRespawn.create();
        if (respawn == null)
            return null;
        respawn.fillAllFromPlayer(player);
        return respawn;
    }

    private static GenericOutPacket getTeleportPlayer(Player player) {
        GenericPacketPlayOutEntityTeleport teleport;

        if (player == null)
            return null;
        teleport = GenericPacketPlayOutEntityTeleport.create();
        if (teleport == null)
            return null;
        teleport.fillAllFromEntity(player);
        return teleport;
    }

    public static boolean setSkin(Player player, GameProfile gameProfile) {
        GameProfile playerGameProfile;
        GenericOutPacket[] packetsToOther;
        GenericOutPacket[] packetsSelf;

        if (player == null || gameProfile == null)
            return false;
        playerGameProfile = PlayerUtils.getGameProfile(player);
        playerGameProfile.getProperties().clear();
        for (Map.Entry<String, Property> entry : gameProfile.getProperties().entries())
            playerGameProfile.getProperties().put(entry.getKey(), entry.getValue());
        packetsToOther = new GenericOutPacket[] {
                getRemovePlayerInfo(player),
                getRemovePlayer(player),
                getAddPlayerInfo(player),
                getAddPlayer(player)
        };
        packetsSelf = new GenericOutPacket[] {
                getRemovePlayerInfo(player),
                getAddPlayerInfo(player),
                getRespawnPlayer(player),
                getTeleportPlayer(player)
        };
        playerSendPacketToOther(player, packetsToOther);
        playerSendPacketToSelf(player, packetsSelf);
        return true;
    }

}
