package me.andavin.mteleporter.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.andavin.mteleporter.Config;
import me.andavin.mteleporter.Data;
import me.andavin.mteleporter.MAction;
import me.andavin.mteleporter.MListener;
import me.andavin.mteleporter.MTeleporter;
import me.andavin.mteleporter.MWarp;
import me.andavin.mteleporter.Message;
import me.andavin.mteleporter.PlayerData;
import me.andavin.mteleporter.libs.ChatHandler;
import me.andavin.mteleporter.libs.Reflection;
import me.andavin.mteleporter.libs.TitleHandler;

public final class Commandmwarp implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		
		if(!(sender instanceof Player)) {
			sender.sendMessage(Message.IN_GAME_ONLY.toString());
			return true;
		}

		String cmd = command.getName();
		Player player = (Player) sender;
		
		String wn = player.getWorld().getName();
		if(!Config.enabledWorlds.contains(wn)) {
			player.sendMessage(Message.WORLD_DISABLED + wn);
			return true;
		}
		
		if(args.length > 0 && args[0].equalsIgnoreCase("help"))
			return this.help(player);
		
		if(args.length == 0 && cmd.equalsIgnoreCase("mwarp"))
			return this.listMWarps(player);
		
		if(args.length == 0 && cmd.equalsIgnoreCase("delmwarp"))
			return this.deleteListMWarps(player);
		
		if(args.length > 0 && cmd.equalsIgnoreCase("mwarp"))
			return this.useMWarp(player, args);
		
		if(args.length > 0 && cmd.equalsIgnoreCase("setmwarp"))
			return this.createMWarp(player, args);
		
		if(args.length > 0 && cmd.equalsIgnoreCase("delmwarp"))
			return this.deleteMWarp(player, args);
			
		return false;
	}
	
	/**
	 * <pre>-----[ MWarp Help ]-----
	 *   ---> New MWarp <---
	 *   --> Delete MWarp <--
	 *   ---> MWarp List <---</pre>
	 */
	private boolean help(Player player){
		
		ChatHandler.sendCommand(player, ChatColor.AQUA + " -----[ ", ChatColor.BLUE + "MWarp Help", "/mwarp help", null, ChatColor.AQUA + " ]-----");
		ChatHandler.sendSuggestion(player, ChatColor.DARK_AQUA + "   ---> ", ChatColor.DARK_GREEN + "New MWarp", "/setmwarp <name>", ChatColor.DARK_AQUA + "Create a new MWarp", ChatColor.DARK_AQUA + " <---");
		ChatHandler.sendCommand(player, ChatColor.DARK_AQUA + "   --> ", ChatColor.DARK_GREEN + "Delete MWarp", "/delmwarp", ChatColor.DARK_AQUA + "Delete an MWarp", ChatColor.DARK_AQUA + " <--");
		ChatHandler.sendCommand(player, ChatColor.DARK_AQUA + "   ---> ", ChatColor.DARK_GREEN + "MWarp List", "/mwarp", ChatColor.DARK_AQUA + "List of your MWarps", ChatColor.DARK_AQUA + " <---");
		return true;
	}
	
	/**
	 * List all of a player's personally owned MWarps.
	 * 
	 * @param player
	 * @return Whether the command was correctly used.
	 */
	private boolean listMWarps(Player player){
	
		if(!player.hasPermission("mt.mwarp.use")) {
			TitleHandler.sendTitle(player, ChatColor.DARK_RED + "No Permission!", Message.NO_PERMISSION.toString());
			return true;
		}
		
		Data data = PlayerData.getData(player);
		
		if(data.getInteger("mwarp_counter") == 0) {
			player.sendMessage(Message.NO_MWARPS.toString());
			return true;
		}

		ChatHandler.sendCommand(player, ChatColor.DARK_AQUA + " >>>>> ", ChatColor.BLUE + "Use MWarps", "/mwarp", null, ChatColor.DARK_AQUA + " <<<<<");
		for(String name : data.getSection("mwarps"))
			ChatHandler.sendCommand(player, ChatColor.DARK_AQUA + " - ", ChatColor.DARK_AQUA + name, "/mwarp " + name, ChatColor.DARK_AQUA + 
					"Teleport to " + ChatColor.BLUE + name + ChatColor.DARK_AQUA + " MWarp", "");
		return true;
	}
	
	/**
	 * List all MWarps belonging to this player, but
	 * instead of giving them the click option of warping
	 * to that MWarp give them the option of deleting it.
	 * 
	 * @param player The player to get MWarps.
	 * @return Whether the command was correctly used.
	 */
	private boolean deleteListMWarps(Player player){
	
		if(!player.hasPermission("mt.mwarp.delete")) {
			TitleHandler.sendTitle(player, ChatColor.DARK_RED + "No Permission!", Message.NO_PERMISSION.toString());
			return true;
		}
		
		Data data = PlayerData.getData(player);
		
		if(data.getInteger("mwarp_counter") == 0) {
			player.sendMessage(Message.NO_MWARPS.toString());
			return true;
		}

		ChatHandler.sendCommand(player, ChatColor.RED + " >>>>> ", ChatColor.DARK_RED + "Delete MWarps", "/delmwarp", null, ChatColor.RED + " <<<<<");
		for(String name : data.getSection("mwarps"))
			ChatHandler.sendCommand(player, ChatColor.RED + " - ", ChatColor.RED + name, "/delmwarp " + name, ChatColor.RED + 
						"Delete " + ChatColor.DARK_RED + name + ChatColor.RED + " MWarp", "");
		return true;
	}
	
	/**
	 * Teleport to an MWarp.
	 * 
	 * @param player Player to teleport.
	 * @param args Arguments to extract the MWarp from.
	 * @return Whether the command was used correctly.
	 */
	private boolean useMWarp(Player player, String[] args){
	
		if(!player.hasPermission("mt.mwarp.use")) {
			TitleHandler.sendTitle(player, ChatColor.DARK_RED + "No Permission!", Message.NO_PERMISSION.toString());
			return true;
		}
		
		MWarp mwarp = this.getMWarp(player, args[0]);
		if(mwarp == null){
			TitleHandler.sendTitle(player, ChatColor.RED + "Error!", Message.NO_MWARP.toString());
			return true;
		}
		
		if(!MTeleporter.plugin.economy().has(player, Config.mwarpPrice)) {
			TitleHandler.sendTitle(player, ChatColor.RED + "Error!");
			player.sendMessage(Message.INSUFFICIENT_FUNDS.toString());
			return true;
		}
		
		Data data = PlayerData.getData(player);
		if(!data.getBoolean("confirmed")) {
			this.confirm(new MAction(mwarp, player, MAction.A.USE));
			return true;
		}
			
		this.useMWarp(new MAction(mwarp, player, MAction.A.USE));
		return true;
	}
	
	private boolean createMWarp(Player player, String[] args){
	
		if(!player.hasPermission("mt.mwarp.create")) {
			TitleHandler.sendTitle(player, ChatColor.DARK_RED + "No Permission!", Message.NO_PERMISSION.toString());
			return true;
		}
		
		if(!MTeleporter.plugin.economy().has(player, Config.setMWarpPrice)) {
			TitleHandler.sendTitle(player, ChatColor.RED + "Error!");
			player.sendMessage(Message.INSUFFICIENT_FUNDS.toString());
			return true;
		}
		
		if(this.getMWarp(player, args[0]) != null) {
			TitleHandler.sendTitle(player, ChatColor.DARK_RED + "Error!", ChatColor.RED + "That MWarp already exists!");
			return true;
		}
		
		MWarp mwarp = new MWarp(args[0], player, player.getLocation());
		Data data = PlayerData.getData(player);
		if(!data.getBoolean("confirmed")) {
			this.confirm(new MAction(mwarp, player, MAction.A.CREATE));
			return true;
		}
			
		this.createMWarp(new MAction(mwarp, player, MAction.A.CREATE));
		return true;
	}
	
	private boolean deleteMWarp(Player player, String[] args){
	
		if(!player.hasPermission("mt.mwarp.delete")) {
			TitleHandler.sendTitle(player, ChatColor.DARK_RED + "No Permission!", Message.NO_PERMISSION.toString());
			return true;
		}
		
		MWarp mwarp = this.getMWarp(player, args[0]);
		if(mwarp == null){
			TitleHandler.sendTitle(player, ChatColor.RED + "Error!", Message.NO_MWARP.toString());
			return true;
		}
		
		this.confirm(new MAction(mwarp, player, MAction.A.DELETE));
		return true;
	}
	
	/**
	 * Use the specified MWarp contained within the MAction.
	 * 
	 * @param action
	 */
	public void useMWarp(MAction action){
		this.confirm(action.player);
		action.mwarp.teleport();
	}
	
	/**
	 * Create a new MWarp with the variables contained within the MAction.
	 * 
	 * @param action
	 */
	public void createMWarp(MAction action){
		
		Player player = action.player;
		this.confirm(player);
		
		MWarp mwarp = action.mwarp;
		Data data = PlayerData.getData(action.player);
		data.set("mwarp_counter", data.getInteger("mwarp_counter") + 1);
		data.set("mwarps." + mwarp.name + ".world", mwarp.location.getWorld().getName());
		data.set("mwarps." + mwarp.name + ".x", mwarp.location.getX());
		data.set("mwarps." + mwarp.name + ".y", mwarp.location.getY());
		data.set("mwarps." + mwarp.name + ".z", mwarp.location.getZ());
		data.set("mwarps." + mwarp.name + ".yaw", mwarp.location.getYaw());
		data.set("mwarps." + mwarp.name + ".pitch", mwarp.location.getPitch());
		
		MTeleporter.plugin.economy().withdrawPlayer(player, Config.setMWarpPrice);
		TitleHandler.sendTitle(player, ChatColor.BLUE + "Created " + ChatColor.DARK_AQUA + mwarp.name, 
				Message.TPED.format(Config.ecoSymbol, Config.setMWarpPrice));
	}
	
	/**
	 * Delete the specified MWarp contained within the MAction.
	 * 
	 * @param action
	 */
	public void deleteMWarp(MAction action){
		Data data = PlayerData.getData(action.player);
		data.set("mwarps." + action.mwarp.name, null);
		data.set("mwarp_counter", data.getInteger("mwarp_counter") - 1);
		action.player.sendMessage(Message.DELETE_SUCCESS.format(action.mwarp.name));
	}
	
	/**
	 * Add some follow up messages for the player denying that
	 * they would like to use MWarps.
	 * 
	 * @param action
	 */
	public void declineMessage(MAction action){
		action.player.sendMessage(Message.CONFIRM_DENY.toString());
	}
	
	/**
	 * Cancel the deletion of the MWarp contained
	 * within the MAction.
	 * 
	 * @param action
	 */
	public void cancelDelete(MAction action){
		action.player.sendMessage(Message.DELETE_CANCEL.format(action.mwarp.name));
	}
	
	/**
	 * Sets the player's confirmed value to true
	 * if it is false so that they will not be asked
	 * to confirm their status on MWarps anymore.
	 * 
	 * @param player
	 */
	private void confirm(Player player){
		Data data = PlayerData.getData(player);
		boolean confirm = data.getBoolean("confirmed");
		if(!confirm) {
			player.sendMessage(Message.CONFIRM_ACCEPT.toString());
			data.set("confirmed", true);
		}
	}
	
	/**
	 * Get an MWarp object by name. If the MWarp does
	 * not exist {@code null} will be returned.
	 * 
	 * @param player The player the MWarp belongs to.
	 * @param name The name of the MWarp.
	 * @return An MWarp object for the name.
	 */
	private MWarp getMWarp(Player player, String name){
		
		String mwarp = null;
		Data data = PlayerData.getData(player);
		for(String m : data.getSection("mwarps")) {
			
			if(m.equalsIgnoreCase(name))
				mwarp = m;
			if(m.length() >= name.length()
					&& m.startsWith(name))
				mwarp = m;
		}
		
		if(mwarp == null)
			return null;
		
		return new MWarp(
				name,
				player,
				Bukkit.getWorld(data.getString("mwarps." + mwarp + ".world")),
				data.getDouble("mwarps." + mwarp + ".x"),
				data.getDouble("mwarps." + mwarp + ".y"),
				data.getDouble("mwarps." + mwarp + ".z"),
				data.getFloat("mwarps." + mwarp + ".yaw"),
				data.getFloat("mwarps." + mwarp + ".pitch")
			);
	}
	
	/**
	 * Have the player confirm that they are okay
	 * with the prices of MWarps.
	 * 
	 * @param player The player to get confirmation from.
	 */
	private void confirm(MAction action){
		
		if(!MListener.confirming.contains(action))
			MListener.confirming.add(action);
		
		action.player.sendMessage(String.format(action.action.message(), action.mwarp.name));
		ChatHandler.sendMessage(action.player, 
				Reflection.addSibling(
					ChatHandler.createButton("        ", ChatColor.DARK_GREEN + "Yes", "MTELEPORTER_" + action.action + "_YES", ChatColor.DARK_GREEN + "Confirm"),
					ChatHandler.createButton("     ", ChatColor.RED + "No", "MTELEPORTER_" + action.action + "_NO", ChatColor.RED + "Deny")
				));
	}
}
