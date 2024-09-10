package net.hylustpickaxes.src.commands;

import net.hylustpickaxes.src.Main;
import net.hylustpickaxes.src.config.ConfigData;
import net.hylustpickaxes.src.gui.GUIManager;
import net.hylustpickaxes.src.tools.Tool;
import net.hylustpickaxes.src.utils.MiscUtils;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class UpgradeCmd implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player)
        {
            Player player = (Player) sender;
            ItemStack item = player.getInventory().getItemInMainHand();
            if (item != null && !player.isDead() && item.getType() != Material.AIR && Main.getToolManger().itemIsTool(item) && Main.getToolManger().isToolPickaxe(item) && ConfigData.accessUpgradeMenu.equalsIgnoreCase("both") || ConfigData.accessUpgradeMenu.equalsIgnoreCase("command"))
            {
                Tool tool = Main.getToolManger().getTool(item);
                player.openInventory(GUIManager.getUpgradeMenu(tool.getUpgrades(), player));
            }
        } else
        {
            sender.sendMessage(MiscUtils.chat(ConfigData.playerOnly));
        }
        return true;
    }
}
