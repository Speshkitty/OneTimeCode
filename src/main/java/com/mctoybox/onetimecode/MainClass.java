package com.mctoybox.onetimecode;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import com.mctoybox.onetimecode.command.CommandHandler;
import com.mctoybox.onetimecode.listeners.LoginListener;

public class MainClass extends JavaPlugin {
	public void onEnable() {
		saveDefaultConfig();
		
		getServer().getPluginManager().registerEvents(new LoginListener(this), this);
		
		CommandHandler ch = new CommandHandler(this);
		getCommand("otc").setExecutor(ch);
		getCommand("claimotc").setExecutor(ch);
		getCommand("createotc").setExecutor(ch);
		getCommand("grantotc").setExecutor(ch);
		getCommand("modifyotc").setExecutor(ch);
	}
	
	public static String getVersion() {
		return Bukkit.getServer().getPluginManager().getPlugin("OneTimeCode").getDescription().getVersion();
	}
}
