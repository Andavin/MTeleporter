package me.andavin.mteleporter.libs;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public final class Reflection {

	public static void sendPacket(Player player, Object packet){
		try {
			Object handle = player.getClass().getMethod("getHandle").invoke(player);
			Object playerConnection = handle.getClass().getField("playerConnection").get(handle);
			playerConnection.getClass().getMethod("sendPacket", getMCClass("Packet")).invoke(playerConnection, packet);
		} catch (IllegalAccessException | IllegalArgumentException 
				| InvocationTargetException | NoSuchMethodException
				| SecurityException | NoSuchFieldException e) {
			Bukkit.getLogger().log(Level.SEVERE, e.getMessage(), e);
		}
	}
	
	public static Object getEnumTitleAction(String action){
		try {
			return getMCClass("PacketPlayOutTitle").getDeclaredClasses()[0].getField(action).get(null);
		} catch (IllegalArgumentException | IllegalAccessException 
				| NoSuchFieldException | SecurityException e) {
			Bukkit.getLogger().log(Level.SEVERE, e.getMessage(), e);
			return null;
		}
	}
	
	public static Object getEnumHoverAction(String action){
		try {
			return getMCClass("ChatHoverable").getDeclaredClasses()[0].getField(action).get(null);
		} catch (IllegalArgumentException | IllegalAccessException 
				| NoSuchFieldException | SecurityException e) {
			Bukkit.getLogger().log(Level.SEVERE, e.getMessage(), e);
			return null;
		}
	}
	
	public static Object getEnumClickAction(String action){
		try {
			return getMCClass("ChatClickable").getDeclaredClasses()[0].getField(action).get(null);
		} catch (IllegalArgumentException | IllegalAccessException 
				| NoSuchFieldException | SecurityException e) {
			Bukkit.getLogger().log(Level.SEVERE, e.getMessage(), e);
			return null;
		}
	}
	
	public static Object setChatHoverable(Object cm, Object ch){
		try {
			return cm.getClass().getMethod("setChatHoverable", getMCClass("ChatHoverable")).invoke(cm, ch);
		} catch (IllegalAccessException | IllegalArgumentException 
				| InvocationTargetException | NoSuchMethodException
				| SecurityException e) {
			Bukkit.getLogger().log(Level.SEVERE, e.getMessage(), e);
			return null;
		}
	}
	
	public static Object setChatClickable(Object cm, Object cc){
		try {
			return cm.getClass().getMethod("setChatClickable", getMCClass("ChatClickable")).invoke(cm, cc);
		} catch (IllegalAccessException | IllegalArgumentException 
				| InvocationTargetException | NoSuchMethodException
				| SecurityException e) {
			Bukkit.getLogger().log(Level.SEVERE, e.getMessage(), e);
			return null;
		}
	}
	
	public static Object setChatModifier(Object icbc, Object cm){
		try {
			return icbc.getClass().getSuperclass().getMethod("setChatModifier", getMCClass("ChatModifier")).invoke(icbc, cm);
		} catch (IllegalAccessException | IllegalArgumentException 
				| InvocationTargetException | NoSuchMethodException
				| SecurityException e) {
			Bukkit.getLogger().log(Level.SEVERE, e.getMessage(), e);
			return null;
		}
	}
	
	public static Object addSibling(Object icbc, Object sibling){
		try {
			return icbc.getClass().getSuperclass().getMethod("addSibling", getMCClass("IChatBaseComponent")).invoke(icbc, sibling);
		} catch (IllegalAccessException | IllegalArgumentException 
				| InvocationTargetException | NoSuchMethodException
				| SecurityException e) {
			Bukkit.getLogger().log(Level.SEVERE, e.getMessage(), e);
			return null;
		}
	}
	
	public static Object getInstance(String clazz, Object... parameters){
		List<Class<?>> cl = new ArrayList<Class<?>>();
		for(Object param : parameters) cl.add(param.getClass());
		Class<?>[] c = new Class<?>[cl.size()];
		Constructor<?> con = getConstructor(clazz, cl.toArray(c));
		try {
			return con.newInstance(parameters);
		} catch (InstantiationException | IllegalAccessException 
				| IllegalArgumentException | InvocationTargetException e) {
			Bukkit.getLogger().log(Level.SEVERE, e.getMessage(), e);
			return null;
		}
	}
	
	public static Object getInstance(Constructor<?> con, Object... parameters){
		try {
			return con.newInstance(parameters);
		} catch (InstantiationException | IllegalAccessException 
				| IllegalArgumentException | InvocationTargetException e) {
			Bukkit.getLogger().log(Level.SEVERE, e.getMessage(), e);
			return null;
		}
	}
	
	public static Constructor<?> getConstructor(String clazz, Class<?>... parameters){
		try {
			return getMCClass(clazz).getConstructor(parameters);
		} catch (NoSuchMethodException | SecurityException e) {
			Bukkit.getLogger().log(Level.SEVERE, e.getMessage(), e);
			return null;
		}
	}
	
	public static Class<?> getMCClass(String clazz){
		String v = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
		try {
			return Class.forName("net.minecraft.server." + v + "." + clazz);
		} catch (ClassNotFoundException e) {
			Bukkit.getLogger().log(Level.SEVERE, e.getMessage(), e);
			return null;
		}
	}
	
}
