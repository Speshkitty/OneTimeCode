package com.mctoybox.onetimecode.listeners;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;

import com.mctoybox.onetimecode.MainClass;

public class LoginListener implements Listener {
	private MainClass mainClass;
	
	public LoginListener(Plugin plugin) {
		this.mainClass = (MainClass) plugin;
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		int numUnclaimedBooks = mainClass.getConfig().getInt("books." + player.getName() + ".amount", 0);
		if (numUnclaimedBooks != 0) {
			player.sendMessage(ChatColor.GREEN + "You have " + numUnclaimedBooks + " unclaimed OTC books!");
			player.sendMessage(ChatColor.GREEN + "Make sure you have enough inventory space and use " + ChatColor.DARK_GREEN + "/claimotc" + ChatColor.GREEN + " to claim them!");
		}
	}
}
