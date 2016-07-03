package me.andavin.mteleporter;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import me.andavin.mteleporter.commands.Commandmt;
import me.andavin.mteleporter.commands.Commandmta;
import me.andavin.mteleporter.commands.Commandmwarp;
import me.andavin.mteleporter.libs.TitleHandler;
import net.milkbowl.vault.economy.Economy;

public class MTeleporter extends JavaPlugin {

	private Config c;
	private Economy eco;
	private int delay;
	private int taskID;

	public static MTeleporter plugin;
	public List<Player> move = new ArrayList<Player>();
	
	@Override
	public void onEnable(){
		
		MTeleporter.plugin = this;
		
		this.setupConfig();
		this.initializeCmds();
		this.registerListener();
		this.initializeEconomy();
	}
	
	@Override
	public void onDisable(){
		PlayerData.saveAll();
	}
	
	public Config config(){
		return this.c;
	}
	
	public Economy economy(){
		return this.eco;
	}
	
	public void setupConfig(){
		this.c = new Config();
	}
	
	private void initializeCmds(){
		super.getCommand("mt").setExecutor(new Commandmt());
		super.getCommand("mta").setExecutor(new Commandmta());
		super.getCommand("mtaccept").setExecutor(new Commandmta());
		super.getCommand("mtdeny").setExecutor(new Commandmta());
		super.getCommand("mwarp").setExecutor(new Commandmwarp());
		super.getCommand("setmwarp").setExecutor(new Commandmwarp());
		super.getCommand("delmwarp").setExecutor(new Commandmwarp());
	}
	
	private void registerListener(){
		super.getServer().getPluginManager().registerEvents(new MListener(), this);
	}
	
	private void initializeEconomy(){
		RegisteredServiceProvider<Economy> economyProvider = super.getServer().getServicesManager().getRegistration(Economy.class);
        if (economyProvider != null)
        	this.eco = economyProvider.getProvider();
        
        if(this.eco == null) {
        	super.getServer().getPluginManager().disablePlugin(this);
        	Bukkit.getLogger().log(Level.SEVERE, "[MTeleporter] MTeleporter requires Vault to run, but we couldn't find it in the plugins folder!");
        }
	}

	/**
	 * Runs a delayed teleport after which the player will be teleported.
	 * 
	 * @param player Player to teleport.
	 * @param loc Location to teleport to.
	 * @param time Time in seconds to delay.
	 */
	public void runTpDelay(Player player, Location loc, int cost){
		
		TitleHandler.reset(player);
		TitleHandler.sendTimes(player, 10, 30, 10);
		
		this.delay = Config.tpDelay;
		if(move.contains(player))
			move.remove(player);
		
		TitleHandler.sendTitle(player, 
				ChatColor.DARK_AQUA + "Delay...", ChatColor.DARK_AQUA + 
				"You will teleport in " + ChatColor.BLUE + delay + 
				ChatColor.DARK_AQUA + " seconds.");
		
		this.taskID = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable(){
			
			@Override
			public void run(){
				
				if(move.contains(player)) {
					Bukkit.getScheduler().cancelTask(taskID);
					TitleHandler.sendTitle(player, ChatColor.DARK_RED + "Teleport Canceled!", ChatColor.RED + "Don't Move!");
					return;
				}
				
				if(delay == 1) {
					Bukkit.getScheduler().cancelTask(taskID);
					player.teleport(loc);
					charge(player, cost);
					return;
				}
				// Blank subtitle to clear the previously sent one
				TitleHandler.sendTitle(player, ChatColor.RED + String.valueOf(--delay) + "...", "");
			}
		}, 20L, 20L);
	}

	/**
	 * Charge the player the specified amount as well
	 * as notifying them that they have been charged.
	 * Returns whether the method successfully charged
	 * the player.
	 * 
	 * @param player Player to charge.
	 * @param amount Amount to charge.
	 * @return Whether the method was successful.
	 */
	public boolean charge(Player player, int amount){
		
		if(!economy().has(player, amount)) {
			TitleHandler.sendTitle(player, ChatColor.RED + "Error!");
			player.sendMessage(Message.INSUFFICIENT_FUNDS.toString());
			return false;
		}
		
		plugin.economy().withdrawPlayer(player, amount);
		TitleHandler.sendTitle(player, ChatColor.BLUE + "Teleporting...", Message.TPED.format(Config.ecoSymbol, amount));
		return true;
	}
}
