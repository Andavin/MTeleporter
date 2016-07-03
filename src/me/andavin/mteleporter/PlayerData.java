package me.andavin.mteleporter;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.entity.Player;

public class PlayerData {
	
	private static Map<UUID, Data> dataMap = new HashMap<UUID, Data>();
	
	/**
	 * Get the personal MWarp player data for
	 * the specified player.
	 * 
	 * @param player The player to get the data for.
	 * @return This player's MWarp data file.
	 */
	public static Data getData(Player player){
		
		UUID uuid = player.getUniqueId();
		if(dataMap.containsKey(uuid))
			return dataMap.get(uuid);
		
		Data data = new Data(uuid.toString() + ".yml");
		dataMap.put(uuid, data);
		return data;
	}
	
	/**
	 * Saves all player MWarp data to their datafiles.
	 * Use this on server shutdown to ensure all data
	 * is saved.
	 */
	public static void saveAll(){
		for(Data data : dataMap.values())
			data.save();
	}
}
