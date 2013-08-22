package com.mctoybox.onetimecode.command;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.mctoybox.onetimecode.MainClass;

public class CommandHandler implements CommandExecutor {
	private MainClass mainClass;
	
	public CommandHandler(Plugin plugin) {
		this.mainClass = (MainClass) plugin;
	}
	
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		switch (command.getName().toLowerCase()) {
			case "claimotc":
				claim(sender, command, args);
				break;
			case "createotc":
				create(sender, command, args);
				break;
			case "grantotc":
				grant(sender, command, args);
				break;
			case "modifyotc":
				modify(sender, command, args);
				break;
			default:
				otc(sender, command, args);
				break;
		}
		return false;
	}
	
	private void otc(CommandSender sender, Command command, String[] args) {
		if ((sender instanceof Player) && ((Player) sender).getItemInHand().getType().equals(Material.WRITTEN_BOOK)) {
			use(sender, command, args);
			return;
		}
		sender.sendMessage(command.getUsage());
	}
	
	private void claim(CommandSender sender, Command command, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "You need to be a player to do that!");
			return;
		}
		Player player = (Player) sender;
		int numUnclaimedBooks = mainClass.getConfig().getInt("books." + player.getName() + ".amount", 0);
		
		if (numUnclaimedBooks != 0) {
			player.sendMessage(ChatColor.GREEN + "You have " + numUnclaimedBooks + " unclaimed OTC books!");
			player.sendMessage(ChatColor.GREEN + "Make sure you have enough inventory space and use " + ChatColor.DARK_GREEN + "/claimotc" + ChatColor.GREEN + " to claim them!");
		}
	}
	
	private void create(CommandSender sender, Command command, String[] args) {
		// TODO Auto-generated method stub
	}
	
	private void grant(CommandSender sender, Command command, String[] args) {
		// TODO Auto-generated method stub
	}
	
	private void modify(CommandSender sender, Command command, String[] args) {
		// TODO Auto-generated method stub
		
	}
	
	private void use(CommandSender sender, Command command, String[] args) {
		// TODO Auto-generated method stub
	}
	
}
