package me.andavin.mteleporter;

import org.bukkit.ChatColor;

public enum Message {
	
	CONFIRM_ACCEPT(ChatColor.DARK_GREEN + "Thank you! You will not be asked again."),
	
	CONFIRM_DENY(ChatColor.RED + "Thank you! You will be asked to confirm again if you use MWarps again."),

	/** Takes a string MWarp name. Use the format method. */
	DELETE_SUCCESS(ChatColor.DARK_RED + "Successfully deleted MWarp " + ChatColor.RED + "%s"),
	
	/** Takes a string MWarp name. Use the format method. */
	DELETE_CANCEL(ChatColor.DARK_RED + "Deletion of " + ChatColor.RED + "%s" + ChatColor.DARK_RED + " successfully canceled."),
	
	/** Takes a string MWarp name. Use the format method. */
	NO_MWARP(ChatColor.DARK_RED + "MWarp " + ChatColor.RED + "%s" + ChatColor.DARK_RED + "doesn't exist!"),
	
	NO_MWARPS(ChatColor.DARK_RED + " You don't own any MWarps! Use " + ChatColor.RED + "/setmwarp <name>" + ChatColor.DARK_RED + " to create one."),
	
	/** Takes a string player name. Use format method. */
	ACCEPT_OTHER(ChatColor.BLUE + "%s" + ChatColor.DARK_AQUA + " has accepted your teleportation request."),

	/** Takes a string player name. Use format method. */
	ACCEPT(ChatColor.DARK_AQUA + "You have accepted " + ChatColor.BLUE + "%s's " + ChatColor.DARK_AQUA + "teleportation request."),
	
	/** Takes a string player name. Use format method. */
	DENY_OTHER(ChatColor.RED + "%s" + ChatColor.DARK_RED + " has denied your teleportation request."),

	/** Takes a string player name. Use format method. */
	DENY(ChatColor.DARK_AQUA + "You have denied " + ChatColor.BLUE + "%s's " + ChatColor.DARK_AQUA + "teleportation request."),
	
	/** Takes a string player name. Use format method. */
	ASK_NOTIFY(ChatColor.DARK_AQUA + "%s" + ChatColor.AQUA + " must accept your teleport request."),
	
	/** Takes a string player name. Use format method. */
	ASK(ChatColor.DARK_AQUA + "%s" + ChatColor.AQUA + " wants to teleport to your location."),

	TP_TIMEOUT(ChatColor.DARK_RED + " All teleport requests have timed out!"),
	
	/** Takes a string player name. Use format method. */
	PLAYER_NOT_FOUND(ChatColor.DARK_RED + " Cannot find player " + ChatColor.RED + "%s"),
	
	NO_CONFIRM(ChatColor.DARK_RED + " There is nothing to confirm!"),
	
	/** Takes a string economy symbol and an integer price. Use format method. */
	TPED(ChatColor.DARK_GREEN + "You have been charged " + ChatColor.BLUE + "%s" + ChatColor.DARK_AQUA + "%d"),
	
	/** Takes a string economy symbol and an integer price. Use format method. */
	CHECK_TP(ChatColor.BLUE + "It will cost you " + ChatColor.DARK_AQUA + "%s%d" + ChatColor.BLUE + " to carry out this transaction."),
	
	INSUFFICIENT_FUNDS(ChatColor.RED + "You do not have sufficient funds to carry out this transaction."),
	
	SUFFICIENT_FUNDS(ChatColor.DARK_GREEN + "You have sufficient funds to carry out this transaction."),
	
	/** Takes a string formatted argument that was not a valid integer. Use format method. */
	INTEGER_ONLY(ChatColor.DARK_RED + "The argument " + ChatColor.RED + "%s" + ChatColor.DARK_RED + " was not a valid integer!"),
	
	IN_GAME_ONLY(ChatColor.DARK_RED + " You can only use that command in-game."),
	
	NO_PERMISSION(ChatColor.RED + " You don't have permission to do that!"),
	
	/** Takes a world name to format. Use format method. */
	WORLD_DISABLED(ChatColor.DARK_RED + "Sorry, " + ChatColor.RED + "MTeleporter" + ChatColor.DARK_RED + " is not enabled in world: " + ChatColor.RED + "%s");
	
	private String message;
	
	private Message(String message){
		this.message = message;
	}
	
	/**
	 * An alternative to using Message.toString()
	 * within a String.format(String, Object...)
	 * method. This will just put the format into
	 * the enum string.
	 * 
	 * @param args
	 * @return Formatted string.
	 */
	public String format(Object... args){
		return String.format(this.message, args);
	}
	
	@Override
	public String toString(){
		return this.message;
	}
}
