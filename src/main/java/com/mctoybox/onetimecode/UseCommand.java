package com.mctoybox.onetimecode;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.defaults.VanillaCommand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.permissions.Permission;

import com.sk89q.bukkit.util.DynamicPluginCommand;

public class UseCommand implements CommandExecutor {
	private MainClass mainClass;
	
	public UseCommand(MainClass mainClass) {
		this.mainClass = mainClass;
	}
	
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage("You need to be a player to do that!");
			return true;
		}
		Player p = (Player) sender;
		if (p.getItemInHand().getType().equals(Material.WRITTEN_BOOK)) {
			if (!p.hasPermission("otc.use")) {
				p.sendMessage(ChatColor.RED + "You don't have permission to do that!");
				return false;
			}
			BookMeta bMeta = (BookMeta) p.getItemInHand().getItemMeta();
			if (bMeta.hasAuthor() && bMeta.getAuthor().equals("One Time Code")) {
				Command cmd = mainClass.getCmd(bMeta.getPage(2).split(" ")[0]);
				if (cmd == null) {
					p.sendMessage("Command not found");
					return true;
				}
				// Vanilla Commands
				if (cmd instanceof VanillaCommand) {
					p.addAttachment(mainClass, cmd.getPermission(), true, mainClass.ticksToAllowPermissions);
				}
				// WorldEdit + WorldGuard Commands
				else if (mainClass.getServer().getPluginManager().getPlugin("WorldEdit") != null && cmd instanceof DynamicPluginCommand) {
					DynamicPluginCommand dpCommand = ((DynamicPluginCommand) cmd);
					if (dpCommand.getPermissions() == null) {
						for (Permission perm : dpCommand.getPlugin().getDescription().getPermissions()) {
							p.addAttachment(mainClass, perm.getName(), true, mainClass.ticksToAllowPermissions);
						}
					}
					else {
						for (String perm : dpCommand.getPermissions()) {
							p.addAttachment(mainClass, perm, true, mainClass.ticksToAllowPermissions);
						}
					}
				}
				// All none specified commands
				else {
					for (Permission perm : ((PluginCommand) cmd).getPlugin().getDescription().getPermissions()) {
						p.addAttachment(mainClass, perm.getName(), true, mainClass.ticksToAllowPermissions);
					}
				}
				
				p.setItemInHand(null);
				
				p.chat("/" + bMeta.getPage(2).replaceAll("%player%", p.getName()));
				p.sendMessage(ChatColor.GREEN + "OneTimeCode book used!");
				Bukkit.broadcast(ChatColor.GRAY + "" + ChatColor.ITALIC + "[" + p.getName() + " used a one time code!]", Server.BROADCAST_CHANNEL_ADMINISTRATIVE);
				Bukkit.broadcast(ChatColor.GRAY + "" + ChatColor.ITALIC + "[Command: " + bMeta.getPage(2) + "]", Server.BROADCAST_CHANNEL_ADMINISTRATIVE);
				
			}
			else
				p.sendMessage(ChatColor.RED + "That is not a OneTimeCode book!");
		}
		else
			p.sendMessage(ChatColor.RED + "That is not a OneTimeCode book!");
		
		return true;
	}
	
}