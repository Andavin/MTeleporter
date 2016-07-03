package me.andavin.mteleporter.commands;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.andavin.mteleporter.Config;
import me.andavin.mteleporter.MTeleporter;
import me.andavin.mteleporter.Message;
import me.andavin.mteleporter.libs.ChatHandler;
import me.andavin.mteleporter.libs.Reflection;
import me.andavin.mteleporter.libs.TitleHandler;

public final class Commandmta extends Commandmt {
	
	public static Map<Player, Player> confirming = new HashMap<Player, Player>();

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		
		if(!(sender instanceof Player)) {
			sender.sendMessage(Message.IN_GAME_ONLY.toString());
			return true;
		}

		String cmd = command.getName();
		
		Player player = (Player) sender;
		if(!player.hasPermission("mt.ask")) {
			TitleHandler.sendTitle(player, ChatColor.DARK_RED + "No Permission!", Message.NO_PERMISSION.toString());
			return true;
		}
		
		String wn = player.getWorld().getName();
		if(!Config.enabledWorlds.contains(wn)) {
			player.sendMessage(Message.WORLD_DISABLED + wn);
			return true;
		}
		
		if(args.length == 0 && cmd.equalsIgnoreCase("mtaccept"))
			return this.accept(player);
		
		if(args.length == 0 && cmd.equalsIgnoreCase("mtdeny"))
			return this.deny(player);
		
		if(args.length == 0 || args[0].equalsIgnoreCase("help"))
			return super.help(player);
		
		if(args.length == 1 && cmd.equalsIgnoreCase("mta"))
			return this.askPlayer(player, args);
		
		if(args.length == 2 && cmd.equalsIgnoreCase("mta")
				&& args[0].equalsIgnoreCase("check"))
			return this.checkPlayer(player, args);
		
		return false;
	}
	
	/**
	 * Request to teleport to another player using
	 * the MTeleporter.
	 * 
	 * @param player Reqesting player.
	 * @param args Arguments parsed to find the target player.
	 * @return Whether the command was in the correct syntax.
	 */
	public boolean askPlayer(Player player, String[] args){
		
		Player target = Bukkit.getPlayer(args[0]);
		
		if(target == null){
			TitleHandler.sendTitle(player, ChatColor.RED + "Error!");
			player.sendMessage(Message.PLAYER_NOT_FOUND.format(args[0]));
			return true;
		}
		
		int cost = super.getCost(player.getLocation(), target.getLocation());
		
		if(!plugin.economy().has(player, cost)) {
			TitleHandler.sendTitle(player, ChatColor.RED + "Error!");
			player.sendMessage(Message.INSUFFICIENT_FUNDS.toString());
			return false;
		}
		
		timeout(player, target);
		player.sendMessage(Message.ASK_NOTIFY.format(target.getName()));
		target.sendMessage(Message.ASK.format(player.getName()));
		target.sendMessage(ChatColor.DARK_AQUA + "Would you like to allow this?");
		
		ChatHandler.sendMessage(target, 
				Reflection.addSibling(
					ChatHandler.createButton("        ", ChatColor.DARK_GREEN + "Yes", "/mtaccept", ChatColor.DARK_GREEN + "Accept " + player.getName() + "'s request."),
					ChatHandler.createButton("     ", ChatColor.RED + "No", "/mtdeny", ChatColor.RED + "Deny " + player.getName() + "'s request.")
				));
		return true;
	}
	
	/**
	 * Checks how much it will cost to teleport to
	 * a target player's location and notifies of player
	 * of these amounts.
	 * 
	 * @param player To notify.
	 * @param args To extract the target player from.
	 * @return Whether the command was used correctly.
	 */
	public boolean checkPlayer(Player player, String[] args){
		
		Player target = Bukkit.getPlayer(args[1]);
		
		if(target == null){
			TitleHandler.sendTitle(player, ChatColor.RED + "Error!");
			player.sendMessage(Message.PLAYER_NOT_FOUND.format(args[1]));
			return true;
		}

		int cost = super.getCost(player.getLocation(), target.getLocation());
		
		player.sendMessage(Message.CHECK_TP.format(Config.ecoSymbol, cost));
		
		if(plugin.economy().has(player, cost))
			player.sendMessage(Message.SUFFICIENT_FUNDS.toString());
		else
			player.sendMessage(Message.INSUFFICIENT_FUNDS.toString());
		return true;
	}
	
	/**
	 * Accepts a teleport request.
	 * 
	 * @param target
	 */
	public boolean accept(Player target){
		
		if(!confirming.containsKey(target)){
			target.sendMessage(Message.TP_TIMEOUT.toString());
			return true;
		}
		
		Player player = confirming.get(target);
		
		int cost = super.getCost(player.getLocation(), target.getLocation());
		
		player.sendMessage(Message.ACCEPT_OTHER.format(target.getName()));
		target.sendMessage(Message.ACCEPT.format(player.getName()));
		
		if(Config.tpDelayEnabled) {
			MTeleporter.plugin.runTpDelay(player, target.getLocation(), cost);
			confirming.remove(target);
			return true;
		}
		
		if(!MTeleporter.plugin.charge(player, cost))
			return true;
		
		player.teleport(target);
		confirming.remove(target);
		return true;
	}
	
	/**
	 * Denies a teleport request.
	 * 
	 * @param target
	 */
	public boolean deny(Player target){
		
		if(!confirming.containsKey(target)){
			target.sendMessage(Message.TP_TIMEOUT.toString());
			return true;
		}
		
		Player player = confirming.get(target);
		
		player.sendMessage(Message.DENY_OTHER.format(target.getName()));
		target.sendMessage(Message.DENY.format(player.getName()));
		confirming.remove(target);
		return true;
	}
	
	/**
	 * Initiates a timeout of 1 minute for the MTeleporter ask request.
	 * 
	 * @param player The player that requested.
	 * @param target The player that got the request.
	 */
	private void timeout(Player player, Player target){
		
		confirming.put(target, player);
		Bukkit.getScheduler().runTaskLaterAsynchronously(super.plugin, new Runnable(){
		
			@Override
			public void run(){
				if(confirming.containsKey(target) && confirming.get(target).getUniqueId().equals(player.getUniqueId()))
					confirming.remove(target);
			}
		}, 1200L);
	}
}
