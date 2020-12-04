package fr.mrcubee.skinchanger;

import com.mojang.authlib.GameProfile;
import fr.mrcubee.gui.SignGUi;
import fr.mrcubee.mojangapi.MojangAPI;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class SkinChanger extends JavaPlugin {

    private static SkinChanger instance;

    private SignGUi signGUi;

    @Override
    public void onLoad() {
        SkinChanger.instance = this;
    }

    @Override
    public void onEnable() {
        this.signGUi = new SignGUi();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player player;

        if (!(sender instanceof Player))
            return false;
        player = (Player) sender;
        this.signGUi.open(player, lines -> {
                    GameProfile gameProfile = MojangAPI.getFromInput(lines[0]);

                    if (gameProfile == null) {
                        player.sendMessage(ChatColor.RED + "Player not exist.");
                        return;
                    }
                    SkinUtils.setSkin(player, gameProfile);

        }, "",
                "^^^",
                "Player Name");
        return true;
    }

    public static SkinChanger getInstance() {
        return SkinChanger.instance;
    }
}
