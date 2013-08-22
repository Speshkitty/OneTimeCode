package com.mctoybox.onetimecode;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

public class LoginListener implements Listener {
	private MainClass mainClass;
	
	public LoginListener(MainClass mainClass) {
		this.mainClass = mainClass;
	}
	
	@EventHandler
	public void onPlayerLogin(PlayerLoginEvent event) {
		
	}
}
