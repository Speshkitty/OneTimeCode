package com.mctoybox.onetimecode;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;

public class CreateCommand implements CommandExecutor {
	private MainClass mainClass;
	
	public CreateCommand(MainClass mainClass) {
		this.mainClass = mainClass;
	}
	
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (args.length <= 1) {
			sender.sendMessage(ChatColor.RED + "No command was specified!");
			return true;
		}
		
		Player p = mainClass.getServer().getPlayer(args[0]);
		
		if (p == null) {
			sender.sendMessage(ChatColor.RED + "That player could not be found!");
			return true;
		}
		ItemStack newBook = new ItemStack(387);
		
		ItemMeta iMeta = newBook.getItemMeta();
		
		BookMeta meta = (BookMeta) iMeta;
		meta.setAuthor("One Time Code");
		
		String usage = "To use this book:\nHold it and use /otc";
		
		String temp = "";
		for (int i = 1; i < args.length; i++) {
			temp += args[i] + " ";
		}
		meta.setPages(usage, temp);
		
		meta.setTitle("OTC - " + temp);
		meta.setDisplayName(meta.getTitle());
		
		newBook.setItemMeta(meta);
		
		p.getInventory().addItem(newBook);
		if ((sender instanceof Player) && !((Player) sender).equals(p)) {
			sender.sendMessage(ChatColor.GREEN + "You have granted " + p.getDisplayName() + " a OneTimeCode book!");
		}
		else if (!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.GREEN + "You have granted " + p.getDisplayName() + " a OneTimeCode book!");
		}
		p.sendMessage(ChatColor.GREEN + "You have receieved a OneTimeCode book!");
		return true;
	}
}
