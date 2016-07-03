package me.andavin.mteleporter;

import java.lang.reflect.Method;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import me.andavin.mteleporter.commands.Commandmwarp;

public class MAction {

	public final MWarp mwarp;
	public final Player player;
	public final MAction.A action;
	
	public MAction(MWarp mwarp, Player player, MAction.A action){
		this.mwarp = mwarp;
		this.player = player;
		this.action = action;
	}
	
	@Override
	public boolean equals(Object obj) {
		
		if(!(obj instanceof MAction))
			return false;
		
		MAction a = (MAction) obj;
		return this.hashCode() == a.hashCode();
	}
	
	@Override
	public int hashCode(){
		return player.getUniqueId().hashCode();
	}
	
	public static enum A {
		
		USE(ChatColor.BLUE + "To use MWarps it will cost you " + ChatColor.AQUA + "%s" + ChatColor.DARK_AQUA + "%d" + ChatColor.BLUE + ". Are you okay with this?", "useMWarp", "declineMessage", "mwarp_price"),
		CREATE(ChatColor.BLUE + "To create an MWarp it will cost you " + ChatColor.AQUA + "%s" + ChatColor.DARK_AQUA + "%d" + ChatColor.BLUE + ". Are you okay with this?", "createMWarp", "declineMessage", "setmwarp_price"),
		DELETE(ChatColor.DARK_GRAY + "Are you sure you want to delete " + ChatColor.GRAY + "%s" + ChatColor.DARK_GRAY + " permanently?", "deleteMWarp", "cancelDelete", null);

		private final String message, tExMethod, fExMethod, format;
		private A(String message, String tExMethod, String fExMethod, String format){
			this.message = message;
			this.tExMethod = tExMethod;
			this.fExMethod = fExMethod;
			this.format = format;
		}
		
		public String message(){
			return this.format != null ? 
				String.format(this.message, Config.ecoSymbol, 
				MTeleporter.plugin.config().getInteger(format))
				: this.message;
		}
		
		public Method executionMethod(String yesno){
			
			try {
				return yesno.equals("YES") ? 
					Commandmwarp.class.getMethod(tExMethod, MAction.class) :
					Commandmwarp.class.getMethod(fExMethod, MAction.class);
			} catch (NoSuchMethodException | SecurityException e) {
				e.printStackTrace();
				return null;
			}
		}
	}
}
