package me.andavin.mteleporter;

import java.util.List;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

public final class Config {

	private final FileConfiguration d;
	
	public static Integer tpDelay;
	public static String ecoSymbol;
	public static Integer mwarpPrice;
	public static Integer pricePerBlock;
	public static Integer setMWarpPrice;
	public static Boolean tpDelayEnabled;
	public static List<String> enabledWorlds;
	
	public Config(){
		MTeleporter.plugin.saveDefaultConfig();
		this.d = MTeleporter.plugin.getConfig();
		Config.tpDelay = this.getInteger("tp_delay");
		Config.ecoSymbol = this.getString("economy_symbol");
		Config.mwarpPrice = getInteger("mwarp_price");
		Config.pricePerBlock = this.getInteger("price_per_block");
		Config.setMWarpPrice = getInteger("setmwarp_price");
		Config.tpDelayEnabled = Config.tpDelay > 0;
		Config.enabledWorlds = getStringList("enabled_worlds");
	}
	
	public Boolean getBoolean(String key){
		return d.getBoolean(key);
	}
	
	public List<Boolean> getBooleanList(String key){
		return d.getBooleanList(key);
	}
	
	public Long getLong(String key){
		return d.getLong(key);
	}
	
	public List<Long> getLongList(String key){
		return d.getLongList(key);
	}
	
	public Integer getInteger(String key){
		return d.getInt(key);
	}
	
	public List<Integer> getIntegerList(String key){
		return d.getIntegerList(key);
	}
	
	public Short getShort(String key){
		return (short) d.getInt(key);
	}
	
	public List<Short> getShortList(String key){
		return d.getShortList(key);
	}
	
	public List<Byte> getByteList(String key){
		return d.getByteList(key);
	}
	
	public Double getDouble(String key){
		return d.getDouble(key);
	}
	
	public List<Double> getDoubleList(String key){
		return d.getDoubleList(key);
	}
	
	public Float getFloat(String key){
		return (float) d.getDouble(key);
	}
	
	public List<Float> getFloatList(String key){
		return d.getFloatList(key);
	}
	
	public String getString(String key){
		return d.getString(key);
	}
	
	public List<String> getStringList(String key){
		return d.getStringList(key);
	}
	
	public ConfigurationSection getConfigurationSection(String key){
		return d.getConfigurationSection(key);
	}
}
