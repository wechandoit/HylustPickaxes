package net.hylustpickaxes.src.commands;

import net.hylustpickaxes.src.Main;
import net.hylustpickaxes.src.config.ConfigData;
import net.hylustpickaxes.src.gui.GUIManager;
import net.hylustpickaxes.src.profiles.Profile;
import net.hylustpickaxes.src.profiles.ProfileManager;
import net.hylustpickaxes.src.utils.MiscUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Tokens implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            Player player = (Player) sender;
            String type = "pickaxe";

            Profile profile = Main.getProfileManager().getProfile(player);
            player.sendMessage(MiscUtils.chat(ConfigData.getTokensMessage.replaceAll("<player>", player.getName()).replaceAll("<amount>", String.valueOf(profile.getTokens(type)))));
        } else if (args.length == 1) {
            if (args[0].equalsIgnoreCase("shop")) {
                Player player = (Player) sender;
                player.openInventory(GUIManager.getShopMenu());
                return true;
            } else {
                Player target;
                String type = "pickaxe";

                try {
                    target = Bukkit.getPlayer(args[0]);
                } catch (NullPointerException e) {
                    sender.sendMessage(MiscUtils.chat(ConfigData.notOnline));
                    return true;
                }

                Profile profile = Main.getProfileManager().getProfile(target);
                sender.sendMessage(MiscUtils.chat(ConfigData.getTokensMessage.replaceAll("<player>", target.getName()).replaceAll("<amount>", String.valueOf(profile.getTokens(type)))));
            }
        } else if (args.length == 3) {
            if (args[0].equalsIgnoreCase("pay")) {
                try {
                    ProfileManager manager = Main.getProfileManager();
                    Profile profile = manager.getProfile((Player) sender);
                    if (Bukkit.getPlayer(args[1]) != null && manager.getProfile(Bukkit.getPlayer(args[1])) != null && !(sender.getName().equalsIgnoreCase(args[1]))) {
                        Player target = Bukkit.getPlayer(args[1]);
                        Profile targetProfile = manager.getProfile(target);
                        if (Integer.parseInt(args[2]) > 0 && Integer.parseInt(args[2]) <= profile.getTokens("pickaxe")) {
                            targetProfile.setTokens(targetProfile.getTokens("pickaxe") + Integer.parseInt(args[2]), "pickaxe");
                            sender.sendMessage(MiscUtils.chat(ConfigData.getTokensMessage.replaceAll("<player>", target.getName()).replaceAll("<amount>", String.valueOf(targetProfile.getTokens("pickaxe")))));
                            sender.sendMessage(MiscUtils.chat(ConfigData.getTokensMessage.replaceAll("<player>", sender.getName()).replaceAll("<amount>", String.valueOf(profile.getTokens("pickaxe")))));
                            profile.setTokens(profile.getTokens("pickaxe") - Integer.parseInt(args[2]), "pickaxe");
                            target.sendMessage(MiscUtils.chat(ConfigData.getTokensMessage.replaceAll("<player>", target.getName()).replaceAll("<amount>", String.valueOf(targetProfile.getTokens("pickaxe")))));

                        } else {
                            sender.sendMessage(MiscUtils.chat(ConfigData.invalidNumber.replaceAll("<amount>", args[2])));
                        }

                    } else {
                        sender.sendMessage(MiscUtils.chat(ConfigData.playerInvalid.replaceAll("<player>", args[1])));
                    }
                } catch (NumberFormatException e) {
                    sender.sendMessage(MiscUtils.chat(ConfigData.invalidNumber.replaceAll("<amount>", args[2])));
                }
            } else if (sender.hasPermission("hylustpickaxes.admin") || sender.isOp()) {
                if (args[0].equalsIgnoreCase("add") || args[0].equalsIgnoreCase("give")) {
                    Player player;
                    int amount;
                    String type = "pickaxe";

                    try {
                        player = Bukkit.getPlayer(args[1]);
                    } catch (NullPointerException e) {
                        sender.sendMessage(MiscUtils.chat(ConfigData.notOnline));
                        return true;
                    }

                    try {
                        amount = Integer.parseInt(args[2]);
                    } catch (IllegalArgumentException e) {
                        sender.sendMessage(MiscUtils.chat(ConfigData.invalidNumber));
                        return true;
                    }

                    Profile profile = Main.getProfileManager().getProfile(player);
                    profile.addTokens(amount, type);
                } else if (args[0].equalsIgnoreCase("remove")) {
                    Player player;
                    int amount;
                    String type = "pickaxe";

                    try {
                        player = Bukkit.getPlayer(args[1]);
                    } catch (NullPointerException e) {
                        sender.sendMessage(MiscUtils.chat(ConfigData.notOnline));
                        return true;
                    }

                    try {
                        amount = Integer.parseInt(args[2]);
                    } catch (IllegalArgumentException e) {
                        sender.sendMessage(MiscUtils.chat(ConfigData.invalidNumber));
                        return true;
                    }

                    Profile profile = Main.getProfileManager().getProfile(player);
                    profile.removeTokens(amount, type);
                } else if (args[0].equalsIgnoreCase("set")) {
                    Player player;
                    int amount;
                    String type = "pickaxe";

                    try {
                        player = Bukkit.getPlayer(args[1]);
                    } catch (NullPointerException e) {
                        sender.sendMessage(MiscUtils.chat(ConfigData.notOnline));
                        return true;
                    }

                    try {
                        amount = Integer.parseInt(args[2]);
                    } catch (IllegalArgumentException e) {
                        sender.sendMessage(MiscUtils.chat(ConfigData.invalidNumber));
                        return true;
                    }

                    Profile profile = Main.getProfileManager().getProfile(player);
                    profile.setTokens(amount, type);
                } else {
                    sender.sendMessage(MiscUtils.chat(ConfigData.playerInvalid));
                }
            }
        }
        return true;
    }
}

