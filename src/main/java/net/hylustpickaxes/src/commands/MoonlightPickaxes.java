package net.hylustpickaxes.src.commands;

import net.hylustpickaxes.src.Main;
import net.hylustpickaxes.src.config.ConfigData;
import net.hylustpickaxes.src.tools.Tool;
import net.hylustpickaxes.src.utils.MiscUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class MoonlightPickaxes implements CommandExecutor {

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender.hasPermission("moonlightpickaxes.admin") || sender.isOp())
        {
            int length = args.length;
            if (length == 4 && args[0].equalsIgnoreCase("give"))
            {
                Player player;
                int amount;
                String type = args[2];

                try
                {
                    player = Bukkit.getPlayer(args[1]);
                } catch (NullPointerException e)
                {
                    sender.sendMessage(MiscUtils.chat(ConfigData.notOnline));
                    return true;
                }

                try
                {
                    amount = Integer.parseInt(args[3]);
                } catch (IllegalArgumentException e)
                {
                    sender.sendMessage(MiscUtils.chat(ConfigData.invalidNumber));
                    return true;
                }

                for (String name : Main.getToolManger().getTokenTypes())
                {
                    if (name.equalsIgnoreCase(type))
                    {
                        Tool tool = Main.getToolManger().getTool(args[2]);
                        ItemStack item = tool.getItem(1, 0, 0, null);
                        item.setAmount(amount);
                        if (player.getInventory().firstEmpty() != -1)
                        {
                            player.getInventory().addItem(item);
                        } else
                        {
                            player.getLocation().getWorld().dropItemNaturally(player.getLocation(), item);
                        }
                        return true;
                    }
                }

            } else if (args.length == 1 && args[0].equalsIgnoreCase("reload"))
            {
                Main.getInstance().reload();
                System.out.println(MiscUtils.chat(ConfigData.reload));
            } else
            {
                sender.sendMessage(MiscUtils.chat(ConfigData.invalidCmdUsage));
            }
        } else
        {
            sender.sendMessage(MiscUtils.chat(ConfigData.noPerm));
        }
        return true;
    }
}
