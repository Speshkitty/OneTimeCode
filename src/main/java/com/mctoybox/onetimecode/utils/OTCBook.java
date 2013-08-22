package com.mctoybox.onetimecode.utils;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import com.mctoybox.onetimecode.MainClass;

public class OTCBook {
	public static ItemStack CreateOTC(String title) {
		return CreateOTC(title, new ArrayList<String>(), false);
	}
	
	public static ItemStack CreateOTC(String title, List<String> commands) {
		return CreateOTC(title, commands, false);
	}
	
	public static ItemStack CreateOTC(String title, List<String> commands, boolean sealed) {
		ItemStack createdBook = new ItemStack(Material.WRITTEN_BOOK);
		
		BookMeta bookMeta = (BookMeta) createdBook.getItemMeta();
		
		bookMeta.setAuthor("One Time Code");
		bookMeta.setTitle(title);
		
		bookMeta.setPage(1, "To use this book:\nHold it and use /otc\n" + (sealed ? "Sealed" : "Unsealed") + "\nVersion:" + MainClass.getVersion());
		
		String page2 = "";
		
		for (String s : commands) {
			page2 += s + "\n";
		}
		
		bookMeta.setPage(2, page2.substring(0, page2.lastIndexOf('\n')));
		
		createdBook.setItemMeta(bookMeta);
		return createdBook;
	}
	
	public static boolean IsSealed(ItemStack book) {
		if (!book.getType().equals(Material.WRITTEN_BOOK)) {
			return false;
		}
		
		BookMeta bookMeta = (BookMeta) book.getItemMeta();
		
		return GetLine(3, bookMeta.getPage(1)).equalsIgnoreCase("Sealed");
	}
	
	public static double GetVersion(ItemStack book) {
		if (!book.getType().equals(Material.WRITTEN_BOOK)) {
			return 0;
		}
		
		BookMeta bookMeta = (BookMeta) book.getItemMeta();
		String version = GetLine(4, bookMeta.getPage(1));
		
		return Double.parseDouble(version.split(":")[1]);
	}
	
	public static boolean SetCommands(ItemStack book, List<String> commands) {
		String[] toPass = new String[commands.size()];
		for (int i = 0; i < commands.size(); i++) {
			toPass[i] = commands.get(i);
		}
		
		return SetCommands(book, toPass);
	}
	
	public static boolean SetCommands(ItemStack book, String... commands) {
		if (!book.getType().equals(Material.WRITTEN_BOOK)) {
			return false;
		}
		
		BookMeta bookMeta = (BookMeta) book.getItemMeta();
		
		String page2 = "";
		
		for (String s : commands) {
			page2 += s + "\n";
		}
		
		bookMeta.setPage(2, page2.substring(0, page2.lastIndexOf('\n')));
		return true;
	}
	
	public static List<String> GetCommands(ItemStack book) {
		if (!book.getType().equals(Material.WRITTEN_BOOK)) {
			return null;
		}
		
		List<String> commandList = new ArrayList<String>();
		BookMeta bookMeta = (BookMeta) book.getItemMeta();
		
		for (String s : bookMeta.getPage(2).split("\n")) {
			commandList.add(s);
		}
		
		return commandList;
	}
	
	private static String GetLine(int lineNumber, String page) {
		return page.split("\n")[lineNumber - 1];
	}
}
