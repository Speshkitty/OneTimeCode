package com.mctoybox.onetimecode.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.mctoybox.onetimecode.MainClass;

public class ClaimCommand implements CommandExecutor {
	
	private MainClass mainClass;
	
	public ClaimCommand(MainClass mainClass) {
		this.mainClass = mainClass;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		// TODO Auto-generated method stub
		return false;
	}
	
}
