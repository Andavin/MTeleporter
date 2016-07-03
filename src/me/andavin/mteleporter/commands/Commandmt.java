package me.andavin.mteleporter.commands;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.andavin.mteleporter.Config;
import me.andavin.mteleporter.MTeleporter;
import me.andavin.mteleporter.Message;
import me.andavin.mteleporter.libs.ChatHandler;
import me.andavin.mteleporter.libs.TitleHandler;

public class Commandmt implements CommandExecutor {
	
	protected MTeleporter plugin;
	
	public Commandmt(){
		this.plugin = MTeleporter.plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		
		if(!(sender instanceof Player)) {
			sender.sendMessage(Message.IN_GAME_ONLY.toString());
			return true;
		}
		
		Player player = (Player) sender;
		if(!player.hasPermission("mt.teleporter")) {
			TitleHandler.sendTitle(player, ChatColor.DARK_RED + "No Permission!", Message.NO_PERMISSION.toString());
			return true;
		}
		
		String wn = player.getWorld().getName();
		if(!Config.enabledWorlds.contains(wn)) {
			TitleHandler.sendTitle(player, ChatColor.RED + "Warning!");
			player.sendMessage(Message.WORLD_DISABLED.format(wn));
			return true;
		}
		
		if(args.length < 1 || args[0].equalsIgnoreCase("help"))
			return this.help(player);
		
		if(args.length == 1 && args[0].equalsIgnoreCase("reload")) {
			MTeleporter.plugin.setupConfig();
			return true;
		}
		
		if(args.length == 3)
			return this.tpCoords(player, args);
		
		if(args.length == 4 && args[0].equalsIgnoreCase("check"))
			return this.checkCoords(player, args);
		
		return false;
	}

	/**
	 * <pre>   -----[ MTeleporter ]-----
	 * Teleports the player for $3/block
	 *  ---> Check Coordinates <---
	 *     --> Teleport Coordinates <--
	 *  ---> Check Player <---
	 *       --> Teleport Player <--
	 *  ---> MWarp Help <---</pre>
	 */
	protected boolean help(Player player){
		
		ChatHandler.sendCommand(player, ChatColor.AQUA + "    -----[ ", ChatColor.BLUE + "MTeleporter", "/mt", null, ChatColor.AQUA + " ]-----");
		player.sendMessage(ChatColor.DARK_AQUA + " Teleports the player for " + ChatColor.BLUE + Config.ecoSymbol + ChatColor.DARK_AQUA + Config.pricePerBlock + "/block");
		
		ChatHandler.sendSuggestion(player, ChatColor.DARK_AQUA + "  ---> ", ChatColor.DARK_GREEN + "Check Coordinates", "/mt check <x> <y> <z>", ChatColor.DARK_AQUA + "Check how far a location is from you", ChatColor.DARK_AQUA + " <---");
		ChatHandler.sendSuggestion(player, ChatColor.DARK_AQUA + "     --> ", ChatColor.BLUE + "Teleport Coordinates", "/mt <x> <y> <z>", ChatColor.DARK_AQUA + "Teleport to a location", ChatColor.DARK_AQUA + " <--");
		ChatHandler.sendSuggestion(player, ChatColor.DARK_AQUA + "  ---> ", ChatColor.DARK_GREEN + "Check Player", "/mta check <player>", ChatColor.DARK_AQUA + "Check how far a player is from you", ChatColor.DARK_AQUA + " <---");
		ChatHandler.sendSuggestion(player, ChatColor.DARK_AQUA + "       --> ", ChatColor.BLUE + "Teleport Player", "/mta <player>", ChatColor.DARK_AQUA + "Request a teleport to a player", ChatColor.DARK_AQUA + " <--");
		ChatHandler.sendCommand(player, ChatColor.DARK_AQUA + "  ---> ", ChatColor.DARK_GREEN + "MWarp Help", "/mwarp help", ChatColor.DARK_AQUA + "Show MWarp help page", ChatColor.DARK_AQUA + " <---");
		return true;
	}
	
	/**
	 * Teleports the player as well as charges them
	 * for the teleport. If the player has insufficient
	 * funds to teleport this method will not charge
	 * them nor teleport them. Instead the player will
	 * be warned of their insufficient funds.
	 * 
	 * @param player Player to teleport.
	 * @param args Arguments given to parse into teleport location.
	 * @return Whether usage was correct.
	 */
	public boolean tpCoords(Player player, String[] args){
		
		Location tpLoc = this.getLocation(player, args);
		if(tpLoc == null)
			return false;

		int cost = this.getCost(player.getLocation(), tpLoc);
		
		if(Config.tpDelayEnabled) {
			MTeleporter.plugin.runTpDelay(player, tpLoc, cost);
			return true;
		}
		
		if(!MTeleporter.plugin.charge(player, cost))
			return true;
		
		player.teleport(tpLoc);
		return true;
	}
	
	/**
	 * Checks the location a player would like to teleport to
	 * and lets the player know how much it would cost them
	 * to teleport there as well as letting them know if
	 * they can afford to do so.
	 * 
	 * @param player Player to teleport.
	 * @param args Arguments given to parse into teleport location.
	 * @return Whether usage was correct.
	 */
	public boolean checkCoords(Player player, String[] args){
		
		args = new String[]{ args[1], args[2], args[3] };
		Location tpLoc = this.getLocation(player, args);
		if(tpLoc == null)
			return false;
		
		int cost = this.getCost(player.getLocation(), tpLoc);
		
		player.sendMessage(Message.CHECK_TP.format(Config.ecoSymbol, cost));
		
		if(plugin.economy().has(player, cost))
			player.sendMessage(Message.SUFFICIENT_FUNDS.toString());
		else
			player.sendMessage(Message.INSUFFICIENT_FUNDS.toString());
		
		return true;
	}
	
	/**
	 * Gets a location off of a string array of arguments.
	 * There should only be 3 values inside of the string
	 * array matching x y and z.
	 * 
	 * @param player The player sending the command.
	 * @param args Arguments to turn to location.
	 * @return A location matching the given arguments.
	 */
	private Location getLocation(Player player, String[] args){
		
		Location loc = player.getLocation();
		
		if(args.length < 3)
			return null;
		
		for(String arg : args)
			if(!validate(arg)) {
				TitleHandler.sendTitle(player, ChatColor.RED + "Error!");
				player.sendMessage(Message.INTEGER_ONLY.format(arg));
				return null;
			}
		
		loc.setX(this.get(loc.getX(), args[0]));
		loc.setY(this.get(loc.getY(), args[1]));
		loc.setZ(this.get(loc.getZ(), args[2]));
		
		return loc;
	}
	
	/**
	 * Validates whether a string argument is a valid
	 * number that can be parsed or starts with
	 * a '~' character.
	 * 
	 * @param s String to validate.
	 * @return Whether this string can be parsed.
	 */
	private boolean validate(String s){
		
		try {
			Double.parseDouble(s);
			return true;
		} catch (NumberFormatException e) {
			if(s.startsWith("~"))
				try {
					Double.parseDouble(s.substring(1));
					return true;
				} catch (NumberFormatException e1) {
					return false;
				}
			else
				return false;
		}
	}
	
	/**
	 * Gets the coordinate double based off of
	 * the given string and the current location.
	 * If the string starts with '~' the current 
	 * coordinate will be added to the value of the
	 * string in front of the '~'. If the string
	 * has non-parsable characters besides '~' 
	 * this method will return zero.
	 * 
	 * @param loc Current coordinate
	 * @param s parsable string.
	 * @return Coordinate parsed correctly.
	 */
	private double get(double loc, String s){
		
		if(!validate(s))
			return 0D;
		
		if(s.startsWith("~"))
			return loc + Double.parseDouble(s.substring(1));
		else 
			return Double.parseDouble(s);
	}
	
	/**
	 * Gets the cost it would be to teleport from
	 * the first location to the second.
	 * 
	 * @param loc Current location.
	 * @param toLoc Location you wish to teleport to.
	 * @return The cost of teleportation to this point.
	 */
	protected int getCost(Location loc, Location toLoc){
		int distance = (int) toLoc.distance(loc);
		return (int) (distance * Config.pricePerBlock);
	}
}
