package com.mctoybox.onetimecode.utils;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import com.mctoybox.onetimecode.MainClass;
import com.mctoybox.onetimecode.exceptions.NotOTCBookException;
import com.mctoybox.onetimecode.exceptions.OTCBookSealedException;

public class OTCBook {
	public static boolean AddCommand(ItemStack book, String command) throws NotOTCBookException, OTCBookSealedException {
		if (IsSealed(book))
			throw new OTCBookSealedException();
		
		BookMeta bookMeta = (BookMeta) book.getItemMeta();
		String commandPage = bookMeta.getPage(2);
		commandPage += command + "\n";
		bookMeta.setPage(2, commandPage);
		
		return book.setItemMeta(bookMeta);
	}
	
	public static ItemStack CreateOTC(String title) {
		return CreateOTC(title, new ArrayList<String>(), false, 1, MainClass.getVersion());
	}
	
	public static ItemStack CreateOTC(String title, List<String> commands) {
		return CreateOTC(title, commands, false, 1, MainClass.getVersion());
	}
	
	public static ItemStack CreateOTC(String title, List<String> commands, boolean sealed) {
		return CreateOTC(title, commands, sealed, 1, MainClass.getVersion());
	}
	
	public static ItemStack CreateOTC(String title, List<String> commands, int uses) {
		return CreateOTC(title, commands, false, uses, MainClass.getVersion());
	}
	
	public static ItemStack CreateOTC(String title, List<String> commands, boolean sealed, int uses) {
		return CreateOTC(title, commands, sealed, uses, MainClass.getVersion());
	}
	
	public static ItemStack CreateOTC(String title, List<String> commands, boolean sealed, int uses, double version) {
		ItemStack createdBook = new ItemStack(Material.WRITTEN_BOOK);
		
		BookMeta bookMeta = (BookMeta) createdBook.getItemMeta();
		
		bookMeta.setAuthor("One Time Code");
		bookMeta.setTitle(title);
		
		String[] pages = new String[2];
		
		pages[0] = "To use this book:\nHold it and use /otc\n" + (sealed ? "Sealed" : "Unsealed") + "\nVersion: " + version + "\nUses: " + uses;
		
		pages[1] = "";
		
		for (String s : commands) {
			pages[1] += s + "\n";
		}
		
		bookMeta.setPages(pages);
		
		createdBook.setItemMeta(bookMeta);
		return createdBook;
	}
	
	public static List<String> GetCommands(ItemStack book) throws NotOTCBookException {
		if (!IsOTCBook(book)) {
			throw new NotOTCBookException();
		}
		
		List<String> commandList = new ArrayList<String>();
		BookMeta bookMeta = (BookMeta) book.getItemMeta();
		
		for (String s : bookMeta.getPage(2).split("\n")) {
			commandList.add(s);
		}
		
		return commandList;
	}
	
	public static String GetTitle(ItemStack book) throws NotOTCBookException {
		if (!IsOTCBook(book)) {
			throw new NotOTCBookException();
		}
		
		BookMeta bookMeta = (BookMeta) book.getItemMeta();
		return bookMeta.getTitle();
	}
	
	public static int GetUses(ItemStack book) throws NotOTCBookException {
		if (!IsOTCBook(book))
			throw new NotOTCBookException();
		
		BookMeta bookMeta = (BookMeta) book.getItemMeta();
		
		String amount;
		try {
			amount = GetLine(5, bookMeta.getPage(1));
			return Integer.parseInt(amount.split(":")[1].substring(1));
		}
		catch (Exception e) {
			return 1;
		}
	}
	
	public static double GetVersion(ItemStack book) throws NotOTCBookException {
		if (!IsOTCBook(book)) {
			throw new NotOTCBookException();
		}
		
		BookMeta bookMeta = (BookMeta) book.getItemMeta();
		String version;
		try {
			version = GetLine(4, bookMeta.getPage(1));
			return Double.parseDouble(version.split(":")[1].substring(1));
		}
		catch (Exception e) {
			return 0;
		}
	}
	
	public static boolean InsertCommand(ItemStack book, int lineNumber, String command) throws NotOTCBookException, OTCBookSealedException {
		if (IsSealed(book)) {
			throw new OTCBookSealedException();
		}
		
		BookMeta bookMeta = (BookMeta) book.getItemMeta();
		
		String[] oldPages = bookMeta.getPage(2).split("\n");
		String[] bookPages = new String[oldPages.length + 1];
		
		for (int i = 0; i < bookPages.length; i++) {
			bookPages[i] = (i < lineNumber) ? oldPages[i] : (i == lineNumber) ? command : oldPages[i - 1];
		}
		String page2 = "";
		for (String s : bookPages) {
			page2 = s + "\n";
		}
		bookMeta.setPage(2, page2);
		
		return book.setItemMeta(bookMeta);
	}
	
	public static boolean IsOTCBook(ItemStack book) {
		if (!book.getType().equals(Material.WRITTEN_BOOK)) {
			return false;
		}
		
		BookMeta bookMeta = (BookMeta) book.getItemMeta();
		if (!bookMeta.hasAuthor() || !bookMeta.getAuthor().equals("One Time Code")) {
			return false;
		}
		
		return true;
	}
	
	public static boolean IsSealed(ItemStack book) throws NotOTCBookException {
		if (!IsOTCBook(book)) {
			throw new NotOTCBookException();
		}
		
		if (GetVersion(book) < 1.8) {
			// If it's from before we stored version numbers, we assume the book is sealed.
			return true;
		}
		
		BookMeta bookMeta = (BookMeta) book.getItemMeta();
		
		return GetLine(3, bookMeta.getPage(1)).equalsIgnoreCase("sealed");
	}
	
	public static boolean SealBook(ItemStack book) throws OTCBookSealedException, NotOTCBookException {
		if (IsSealed(book))
			throw new OTCBookSealedException();
		
		BookMeta bookMeta = (BookMeta) book.getItemMeta();
		bookMeta.setPage(1, "To use this book:\nHold it and use /otc\n" + "Sealed" + "\nVersion: " + GetVersion(book) + "\nUses: " + GetUses(book));
		
		return book.setItemMeta(bookMeta);
	}
	
	public static boolean SetCommand(ItemStack book, int lineNumber, String command) throws OTCBookSealedException, NotOTCBookException {
		if (IsSealed(book))
			throw new OTCBookSealedException();
		
		BookMeta bookMeta = (BookMeta) book.getItemMeta();
		
		String[] pageData = bookMeta.getPage(2).split("\n");
		pageData[lineNumber - 1] = command;
		
		String page = "";
		
		for (String s : pageData) {
			page += s.trim() + "\n";
		}
		
		bookMeta.setPage(2, page);
		return book.setItemMeta(bookMeta);
	}
	
	public static boolean SetCommands(ItemStack book, List<String> commands) throws OTCBookSealedException, NotOTCBookException {
		String[] toPass = new String[commands.size()];
		for (int i = 0; i < commands.size(); i++) {
			toPass[i] = commands.get(i);
		}
		
		return SetCommands(book, toPass);
	}
	
	public static boolean SetCommands(ItemStack book, String... commands) throws OTCBookSealedException, NotOTCBookException {
		if (IsSealed(book))
			throw new OTCBookSealedException();
		
		BookMeta bookMeta = (BookMeta) book.getItemMeta();
		
		String page2 = "";
		
		for (String s : commands) {
			page2 += s + "\n";
		}
		
		bookMeta.setPage(2, page2.substring(0, page2.lastIndexOf('\n')));
		return book.setItemMeta(bookMeta);
	}
	
	public static boolean SetTitle(ItemStack book, String newTitle) throws OTCBookSealedException, NotOTCBookException {
		if (IsSealed(book))
			throw new OTCBookSealedException();
		
		BookMeta bookMeta = (BookMeta) book.getItemMeta();
		bookMeta.setTitle(newTitle);
		
		return book.setItemMeta(bookMeta);
	}
	
	public static boolean SetUses(ItemStack book, int uses, boolean bypassSeal) throws NotOTCBookException, OTCBookSealedException {
		if (!bypassSeal && IsSealed(book))
			throw new OTCBookSealedException();
		return SetUses(book, uses);
	}
	
	public static boolean SetUses(ItemStack book, int uses) throws NotOTCBookException {
		if (!IsOTCBook(book))
			throw new NotOTCBookException();
		
		BookMeta bookMeta = (BookMeta) book.getItemMeta();
		
		bookMeta.setPage(1, "To use this book:" + "\n" + "Hold it and use /otc" + "\n" + (IsSealed(book) ? "Sealed" : "Unsealed") + "\n" + "Version: " + GetVersion(book) + "\n" + "Uses: " + uses);
		return book.setItemMeta(bookMeta);
	}
	
	private static String GetLine(int lineNumber, String page) {
		return page.split("\n")[lineNumber - 1];
	}
}
