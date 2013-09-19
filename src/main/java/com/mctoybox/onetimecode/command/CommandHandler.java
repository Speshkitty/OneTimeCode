package com.mctoybox.onetimecode.command;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import com.mctoybox.onetimecode.MainClass;
import com.mctoybox.onetimecode.exceptions.CommandNotFoundException;
import com.mctoybox.onetimecode.exceptions.NotOTCBookException;
import com.mctoybox.onetimecode.exceptions.OTCBookSealedException;
import com.mctoybox.onetimecode.utils.OTCBook;

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
			case "sealotc":
				seal(sender, command, args);
				break;
			default:
				otc(sender, command, args);
				break;
		}
		return true;
	}
	
	private void otc(CommandSender sender, Command command, String[] args) {
		if ((sender instanceof Player) && OTCBook.IsOTCBook(((Player) sender).getItemInHand())) {
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
		
		ConfigurationSection temp = mainClass.getConfig().getConfigurationSection("books." + player.getName());
		if (temp == null) {
			player.sendMessage(ChatColor.GREEN + "You have no unclaimed OTC books!");
			return;
		}
		Set<String> books = temp.getKeys(false);
		
		if (books.size() == 0) {
			player.sendMessage(ChatColor.GREEN + "You have no unclaimed OTC books!");
			return;
		}
		int booksAddedCount = 0;
		for (String s : books) {
			String path = "books." + player.getName() + "." + s;
			String title = mainClass.getConfig().getString(path + ".title", "OTC Book");
			double version = mainClass.getConfig().getDouble(path + ".version", MainClass.getVersion());
			List<String> commands = mainClass.getConfig().getStringList(path + ".commands");
			int uses = mainClass.getConfig().getInt(path + ".uses", 1);
			
			boolean playerHasFreeSpaces = player.getInventory().firstEmpty() != -1;
			
			if (playerHasFreeSpaces) {
				player.getInventory().addItem(OTCBook.CreateOTC(title, commands, true, uses, version));
				mainClass.getConfig().set(path, null);
				booksAddedCount += 1;
			}
			else {
				player.sendMessage(ChatColor.RED + "You need more free space to claim all your OTC books!");
				break;
			}
		}
		mainClass.saveConfig();
		player.sendMessage(ChatColor.GREEN + "You have claimed " + booksAddedCount + " OTC books!");
	}
	
	private void create(CommandSender sender, Command command, String[] args) {
		String playerName = "", title = "";
		int uses = 1;
		List<String> commands = new ArrayList<String>();
		
		for (int i = 0; i < args.length; i++) {
			if (args[i].startsWith("c:")) {
				String tempCommand = args[i].substring(2);
				
				for (int j = i + 1; j < args.length; j++) {
					if (args[j].length() >= 3 && args[j].substring(1, 2).equals(":")) {
						break;
					}
					tempCommand += " " + args[j];
				}
				
				commands.add(tempCommand);
			}
			else if (args[i].startsWith("p:")) {
				playerName = args[i].split(":")[1];
			}
			else if (args[i].startsWith("u:")) {
				uses = Integer.parseInt(args[i].split(":")[1]);
			}
			else if (args[i].startsWith("t:")) {
				String tempTitle = args[i].substring(2);
				
				for (int j = i + 1; j < args.length; j++) {
					if (args[j].length() >= 3 && args[j].substring(1, 2).equals(":")) {
						break;
					}
					tempTitle += " " + args[j];
				}
				
				title = tempTitle;
			}
		}
		
		if (!(sender instanceof Player) && playerName.equals("")) {
			sender.sendMessage(ChatColor.RED + "You need to specify a player!");
			sender.sendMessage(command.getUsage());
			return;
		}
		
		ItemStack newBook = OTCBook.CreateOTC(title, commands, !playerName.equals(""), uses);
		if (playerName.equals("")) {
			Player player = (Player) sender;
			HashMap<Integer, ItemStack> testAdd = player.getInventory().addItem(newBook);
			if (testAdd.size() == 0) {
				player.sendMessage(ChatColor.GREEN + "You have created an OTC book!");
				player.sendMessage(ChatColor.GREEN + "It has been placed in your inventory ready to modify or grant!");
				player.sendMessage(ChatColor.GREEN + "Use " + ChatColor.DARK_GREEN + "/modifyotc" + ChatColor.GREEN + " to modify it, or " + ChatColor.DARK_GREEN + "/grantotc" + ChatColor.GREEN
						+ " to grant it to someone!");
			}
			else {
				player.sendMessage(ChatColor.RED + "There was an issue giving you the OTC book!");
				player.sendMessage(ChatColor.RED + "Please free some inventory space and try again!");
			}
		}
		else {
			grant(playerName, (Player) sender, newBook);
		}
	}
	
	private void grant(CommandSender sender, Command command, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "You need to be a player to do that!");
			return;
		}
		if (args.length != 1) {
			sender.sendMessage(command.getUsage());
			return;
		}
		
		Player player = (Player) sender;
		
		grant(args[0], player);
	}
	
	private void modify(CommandSender sender, Command command, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "You need to be a player to do that!");
			return;
		}
		if (args.length < 2) {
			sender.sendMessage(command.getUsage());
			return;
		}
		
		Player player = (Player) sender;
		try {
			String stringData = "";
			int intData = 0;
			switch (args[0].toLowerCase()) {
				case "title":
					for (int i = 1; i < args.length; i++) {
						stringData += (i == 1 ? "" : " ") + args[i];
					}
					OTCBook.SetTitle(player.getItemInHand(), stringData);
					player.sendMessage(ChatColor.GREEN + "Your books title has been changed!");
					break;
				case "addcommand":
					for (int i = 1; i < args.length; i++) {
						stringData += (i == 1 ? "" : " ") + args[i];
					}
					OTCBook.AddCommand(player.getItemInHand(), stringData.trim());
					player.sendMessage(ChatColor.GREEN + "Command has been added!");
					break;
				case "insertcommand":
					for (int i = 2; i < args.length; i++) {
						stringData += (i == 1 ? "" : " ") + args[i];
					}
					intData = Integer.parseInt(args[1]);
					OTCBook.InsertCommand(player.getItemInHand(), intData, stringData);
					player.sendMessage(ChatColor.GREEN + "Command has been inserted on line " + intData + "!");
					break;
				case "setcommand":
					for (int i = 2; i < args.length; i++) {
						stringData += (i == 1 ? "" : " ") + args[i];
					}
					intData = Integer.parseInt(args[1]);
					if (intData < 1) {
						player.sendMessage(ChatColor.RED + "Line number needs to be 1 or greater!");
						return;
					}
					OTCBook.SetCommand(player.getItemInHand(), intData, stringData);
					player.sendMessage(ChatColor.GREEN + "Command has been set on line " + intData + "!");
					break;
				case "uses":
					intData = Integer.parseInt(args[1]);
					OTCBook.SetUses(player.getItemInHand(), intData, false);
					player.sendMessage(ChatColor.GREEN + "You OTC book's uses has been set to " + intData + "!");
					break;
				default:
					player.sendMessage(ChatColor.RED + "Unknown command!");
					break;
			}
		}
		catch (OTCBookSealedException e) {
			player.sendMessage(ChatColor.RED + "That OTC book is sealed!");
		}
		catch (NotOTCBookException e) {
			player.sendMessage(ChatColor.RED + "That is not an OTC book!");
		}
		catch (NumberFormatException e) {
			player.sendMessage(ChatColor.RED + "Argument invalid: " + args[1] + "! Must be a number!");
		}
	}
	
	private void use(CommandSender sender, Command command, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "You need to be a player to do that!");
			return;
		}
		if (args.length != 0) {
			sender.sendMessage(command.getUsage());
			return;
		}
		
		Player p = (Player) sender;
		
		ItemStack book = p.getItemInHand();
		
		List<String> perms = new ArrayList<String>();
		
		List<String> commands;
		
		try {
			if (!OTCBook.IsSealed(book)) {
				p.sendMessage(ChatColor.RED + "That book needs to be sealed!");
				return;
			}
			commands = OTCBook.GetCommands(book);
			
			for (String s : commands) {
				perms.addAll(mainClass.getPermissionForCommand(s));
			}
			
			for (String s : perms) {
				MainClass.perms.playerAddTransient(p, s);
			}
			
			for (String s : commands) {
				p.chat(createCommand(p, s));
			}
			for (String s : perms) {
				MainClass.perms.playerRemoveTransient(p, s);
			}
			
			OTCBook.SetUses(book, OTCBook.GetUses(book) - 1);
			
			if (OTCBook.GetUses(book) <= 0) {
				p.setItemInHand(new ItemStack(0));
				p.sendMessage(ChatColor.GREEN + "Your OTC book crumbles to dust as its mystical charge dissipates!");
			}
		}
		catch (NotOTCBookException e) {
			p.sendMessage(ChatColor.RED + "That isn't an OTC book!");
		}
		catch (CommandNotFoundException e1) {
			p.sendMessage(ChatColor.RED + "The command in this book could not be found!");
			p.sendMessage(ChatColor.RED + e1.getMessage());
			
			mainClass.log(ChatColor.RED + "Error using a OTC Book!");
			mainClass.log(ChatColor.RED + "Command used: " + e1.getMessage());
		}
	}
	
	private void seal(CommandSender sender, Command command, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "You need to be a player to do that!");
			return;
		}
		
		Player player = (Player) sender;
		
		try {
			OTCBook.SealBook(player.getItemInHand());
			player.sendMessage(ChatColor.GREEN + "Your OTC book has been sealed!");
		}
		catch (NotOTCBookException e) {
			player.sendMessage(ChatColor.RED + "That is not an OTC book!");
		}
		catch (OTCBookSealedException e) {
			player.sendMessage(ChatColor.RED + "That OTC book is already sealed!");
		}
	}
	
	private void grant(String playerTo, Player playerFrom) {
		if (!OTCBook.IsOTCBook(playerFrom.getItemInHand())) {
			playerFrom.sendMessage(ChatColor.RED + "You need to be holding a OTC book!");
			return;
		}
		
		ConfigurationSection temp = mainClass.getConfig().getConfigurationSection("books." + playerFrom.getName());
		String bookNumber = "";
		if (temp == null) {
			bookNumber = "book1";
		}
		else {
			Set<String> books = temp.getKeys(false);
			for (int i = 1; i <= books.size() + 1; i++) {
				if (!books.contains("book" + i)) {
					bookNumber = "book" + i;
					break;
				}
			}
		}
		
		String path = "books." + playerTo + "." + bookNumber;
		try {
			mainClass.getConfig().set(path + ".title", OTCBook.GetTitle(playerFrom.getItemInHand()));
			mainClass.getConfig().set(path + ".commands", OTCBook.GetCommands(playerFrom.getItemInHand()));
			mainClass.getConfig().set(path + ".version", OTCBook.GetVersion(playerFrom.getItemInHand()));
			mainClass.getConfig().set(path + ".uses", OTCBook.GetUses(playerFrom.getItemInHand()));
			mainClass.saveConfig();
		}
		catch (NotOTCBookException e) {
			e.printStackTrace();
		}
		
		playerFrom.getInventory().setItemInHand(new ItemStack(0));
		playerFrom.sendMessage(ChatColor.GREEN + "Your OTC book was gifted to " + playerTo + "!");
		Player target = mainClass.getServer().getPlayer(playerTo);
		if (target != null) {
			target.sendMessage(ChatColor.GREEN + "You have been gifted a OTC book by " + playerFrom.getName() + "!");
			target.sendMessage(ChatColor.GREEN + "Use " + ChatColor.DARK_GREEN + "/claimotc" + ChatColor.GREEN + " to claim it!");
		}
	}
	
	private void grant(String playerTo, Player playerFrom, ItemStack book) {
		if (!OTCBook.IsOTCBook(book)) {
			playerFrom.sendMessage(ChatColor.RED + "You need to be holding a OTC book!");
			return;
		}
		
		ConfigurationSection temp = mainClass.getConfig().getConfigurationSection("books." + playerFrom.getName());
		String bookNumber = "";
		if (temp == null) {
			bookNumber = "book1";
		}
		else {
			Set<String> books = temp.getKeys(false);
			for (int i = 1; i <= books.size() + 1; i++) {
				if (!books.contains("book" + i)) {
					bookNumber = "book" + i;
					break;
				}
			}
		}
		
		String path = "books." + playerTo + "." + bookNumber;
		try {
			mainClass.getConfig().set(path + ".title", OTCBook.GetTitle(book));
			mainClass.getConfig().set(path + ".commands", OTCBook.GetCommands(book));
			mainClass.getConfig().set(path + ".version", OTCBook.GetVersion(book));
			mainClass.getConfig().set(path + ".uses", OTCBook.GetUses(book));
			mainClass.saveConfig();
		}
		catch (NotOTCBookException e) {
			e.printStackTrace();
		}
		
		playerFrom.sendMessage(ChatColor.GREEN + "Your OTC book was gifted to " + playerTo + "!");
		Player target = mainClass.getServer().getPlayer(playerTo);
		if (target != null) {
			target.sendMessage(ChatColor.GREEN + "You have been gifted a OTC book by " + playerFrom.getName() + "!");
			target.sendMessage(ChatColor.GREEN + "Use " + ChatColor.DARK_GREEN + "/claimotc" + ChatColor.GREEN + " to claim it!");
		}
	}
	
	private String createCommand(Player player, String base) {
		String output = (base.startsWith("/") ? "" : "/") + base;
		output = output.replaceAll("%player%", player.getName());
		output = output.replaceAll("%world%", player.getWorld().getName());
		
		return output;
	}
}
