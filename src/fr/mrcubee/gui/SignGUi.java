package fr.mrcubee.gui;

import fr.mrcubee.bukkit.Packets;
import fr.mrcubee.bukkit.events.PacketReceiveEvent;
import fr.mrcubee.bukkit.packet.*;
import fr.mrcubee.skinchanger.SkinChanger;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class SignGUi implements Listener {

    private GenericListenerManager manager;
    private Map<Player, Consumer<String[]>> consumerMap;

    public SignGUi() {
        this.consumerMap = new HashMap<Player, Consumer<String[]>>();
        this.manager = GenericListenerManager.create();
        SkinChanger.getInstance().getServer().getPluginManager().registerEvents(this, SkinChanger.getInstance());
    }

    private void removeSign(Player player, Location location) {
        GenericPacketPlayOutBlockChange blockChange;
        Block block;

        if (player == null || location == null)
            return;
        blockChange = GenericPacketPlayOutBlockChange.create();
        if (blockChange == null)
            return;
        location.setWorld(player.getWorld());
        block = location.getBlock();
        blockChange.setLocation(location);
        blockChange.setBlock(block.getType(), block.getData());
        blockChange.sendPlayer(player);
    }

    @EventHandler
    public void packetReceived(PacketReceiveEvent event) {
        GenericPacketPlayInUpdateSign updateSign;
        Consumer<String[]> consumer;
        String[] lines;

        if (event.getListenerManager() != this.manager || event.getPacket().getPacket() != Packets.PLAY_IN_UPDATE_SIGN
        || !this.consumerMap.containsKey(event.getSender()))
            return;
        updateSign = (GenericPacketPlayInUpdateSign) event.getPacket();
        removeSign(event.getSender(), updateSign.getLocation());
        this.manager.removePlayer(event.getSender());
        consumer = this.consumerMap.remove(event.getSender());
        if (consumer == null)
            return;
        lines = updateSign.getLines();
        if (lines == null)
            return;
        consumer.accept(lines);
    }

    private boolean openSign(Player player, String[] lines) {
        GenericPacketPlayOutBlockChange blockChange;
        GenericPacketPlayOutUpdateSign updateSign;
        GenericPacketPlayOutOpenSignEditor openSignEditor;
        Location location;

        if (player == null || lines == null || this.manager == null)
            return false;
        blockChange = GenericPacketPlayOutBlockChange.create();
        updateSign = GenericPacketPlayOutUpdateSign.create();
        openSignEditor = GenericPacketPlayOutOpenSignEditor.create();
        location = player.getLocation();
        location.setY(player.getWorld().getMaxHeight() - 1);

        blockChange.setLocation(location);
        blockChange.setBlock(Material.WALL_SIGN, 0);

        updateSign.setLocation(location);
        updateSign.setLines(lines);

        openSignEditor.setLocation(location);

        this.manager.addPlayer(player);
        blockChange.sendPlayer(player);
        updateSign.sendPlayer(player);
        openSignEditor.sendPlayer(player);
        return true;
    }

    public boolean open(Player player, Consumer<String[]> consumer, String... lines) {
        if (consumer == null)
            return false;
        else if (!openSign(player, lines))
            return false;
        this.consumerMap.put(player, consumer);
        return true;
    }
}
