package me.andavin.mteleporter;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public final class Data {
	
	private File dfile;
	private FileConfiguration d;
	
	public Data(String filename){
		this.dfile = new File(MTeleporter.plugin.getDataFolder(), filename);
		this.checkFile(dfile);
		this.d = YamlConfiguration.loadConfiguration(this.dfile);
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
	
	public Set<String> getSection(String key){
		return this.getConfigurationSection(key).getKeys(false);
	}
	
	public ConfigurationSection getConfigurationSection(String key){
		return d.getConfigurationSection(key);
	}
	
	public void set(String key, Object value){
		d.set(key, value);
	}
	
	public void save(){
		try {
			d.save(dfile);
		} catch (IOException e) {
			Bukkit.getLogger().log(Level.SEVERE, "[MTeleporter] Failed to save file " + dfile.getName());
		}
	}
	
	private void checkFile(File file){
		try {
			if(!file.exists()) {
				Bukkit.getLogger().log(Level.INFO, "[MTeleporter] The file " + file.getName() + " does not exist. Creating one for you...");
				file.getParentFile().mkdirs();
				file.createNewFile();
			}
		} catch (IOException e) {
			Bukkit.getLogger().log(Level.SEVERE, "[MTeleporter] Failed to create file " + file.getName());
		}
	}
}
