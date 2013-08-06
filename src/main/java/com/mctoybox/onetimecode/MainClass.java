package com.mctoybox.onetimecode;

import java.lang.reflect.Field;

import org.bukkit.command.Command;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.plugin.SimplePluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class MainClass extends JavaPlugin {
	protected int ticksToAllowPermissions;
	
	@Override
	public void onEnable() {
		getCommand("otc").setExecutor(new UseCommand(this));
		getCommand("createotc").setExecutor(new CreateCommand(this));
		
		saveDefaultConfig();
		ticksToAllowPermissions = getConfig().getInt("ticksToAllowPermissions", 2);
	}
	
	protected Command getCmd(String commandName) {
		SimplePluginManager spm = (SimplePluginManager) getServer().getPluginManager();
		
		Field f = null;
		try {
			f = SimplePluginManager.class.getDeclaredField("commandMap");
		}
		catch (NoSuchFieldException e) {
			e.printStackTrace();
		}
		catch (SecurityException e) {
			e.printStackTrace();
		}
		f.setAccessible(true);
		
		SimpleCommandMap scm;
		try {
			scm = (SimpleCommandMap) f.get(spm);
			return scm.getCommand(commandName);
			
		}
		catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
		catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
		
	}
}
