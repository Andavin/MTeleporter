package me.andavin.mteleporter;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class MWarp {

	public final String name;
	public final Player player;
	public final Location location;
	
	public MWarp(String name, Player player, Location location){
		this.name = name;
		this.player = player;
		this.location = location;
	}
	
	public MWarp(String name, Player player, World world, double x, double y, double z){
		this.name = name;
		this.player = player;
		this.location = new Location(world, x, y, z);
	}
	
	public MWarp(String name, Player player, World world, double x, double y, double z, float yaw, float pitch){
		this.name = name;
		this.player = player;
		this.location = new Location(world, x, y, z, yaw, pitch);
	}
	
	public void teleport(){
		
		if(Config.tpDelayEnabled) {
			MTeleporter.plugin.runTpDelay(player, location, Config.mwarpPrice);
		} else {
			MTeleporter.plugin.charge(player, Config.mwarpPrice);
			player.teleport(location);
		}
	}
	
	@Override
	public boolean equals(Object obj){
		
		if(!(obj instanceof MWarp))
			return false;
		
		MWarp m = (MWarp) obj;
		return m.hashCode() == this.hashCode();
	}
	
	@Override
	public int hashCode(){
		return name.hashCode() + player.getUniqueId().hashCode() + location.hashCode();
	}
}
