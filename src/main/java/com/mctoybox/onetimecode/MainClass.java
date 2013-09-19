package com.mctoybox.onetimecode;

import java.lang.reflect.Field;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.command.defaults.VanillaCommand;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.SimplePluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.mctoybox.onetimecode.command.CommandHandler;
import com.mctoybox.onetimecode.exceptions.CommandNotFoundException;
import com.mctoybox.onetimecode.listeners.LoginListener;
import com.sk89q.bukkit.util.DynamicPluginCommand;

public class MainClass extends JavaPlugin {
	public static net.milkbowl.vault.permission.Permission perms = null;
	
	public void onEnable() {
		saveDefaultConfig();
		
		getServer().getPluginManager().registerEvents(new LoginListener(this), this);
		
		CommandHandler ch = new CommandHandler(this);
		getCommand("otc").setExecutor(ch);
		getCommand("claimotc").setExecutor(ch);
		getCommand("createotc").setExecutor(ch);
		getCommand("grantotc").setExecutor(ch);
		getCommand("modifyotc").setExecutor(ch);
		getCommand("sealotc").setExecutor(ch);
		
		perms = getServer().getServicesManager().getRegistration(net.milkbowl.vault.permission.Permission.class).getProvider();
	}
	
	public static double getVersion() {
		return Double.parseDouble(Bukkit.getServer().getPluginManager().getPlugin("OneTimeCode").getDescription().getVersion());
	}
	
	public void log(String message) {
		getServer().getConsoleSender().sendMessage(message);
	}
	
	public List<String> getPermissionForCommand(String command) throws CommandNotFoundException {
		List<String> toReturn;
		toReturn = getConfig().getStringList("permissions." + command.split(" ")[0]);
		
		Command cmd = getCmd(command.substring(1).split(" ")[0]);
		
		if (cmd == null) {
			throw new CommandNotFoundException(command);
		}
		if (getServer().getPluginManager().getPlugin("WorldEdit") != null && cmd instanceof DynamicPluginCommand) {
			DynamicPluginCommand dpCommand = ((DynamicPluginCommand) cmd);
			if (dpCommand.getPermissions() == null) {
				for (Permission perm : dpCommand.getPlugin().getDescription().getPermissions()) {
					if (perm.getName().contains(";")) {
						String[] toAdd = perm.getName().split(";");
						for (String s : toAdd) {
							toReturn.add(s);
						}
					}
					else {
						toReturn.add(perm.getName());
					}
				}
			}
			else {
				for (String perm : dpCommand.getPermissions()) {
					if (perm.contains(";")) {
						String[] toAdd = perm.split(";");
						for (String s : toAdd) {
							toReturn.add(s);
						}
					}
					else {
						toReturn.add(perm);
					}
				}
			}
		}
		else if (cmd instanceof PluginCommand) {
			for (Permission perm : ((PluginCommand) cmd).getPlugin().getDescription().getPermissions()) {
				if (perm.getName().contains(";")) {
					String[] toAdd = perm.getName().split(";");
					for (String s : toAdd) {
						toReturn.add(s);
					}
				}
				else {
					toReturn.add(perm.getName());
				}
			}
		}
		else if (cmd instanceof VanillaCommand) {
			VanillaCommand vCmd = (VanillaCommand) cmd;
			if (vCmd.getPermission().contains(";")) {
				String[] toAdd = vCmd.getPermission().split(";");
				for (String s : toAdd) {
					toReturn.add(s);
				}
			}
			else {
				toReturn.add(vCmd.getPermission());
			}
			toReturn.add(vCmd.getPermission());
		}
		
		return toReturn;
		
	}
	
	private Command getCmd(String commandName) {
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
