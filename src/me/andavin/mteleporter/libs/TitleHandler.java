package me.andavin.mteleporter.libs;

import org.bukkit.entity.Player;

public final class TitleHandler {

	public static void sendTitle(Player player, String title){
		sendTitle(player, title, null, -1, -1, -1);
	}
	
	public static void sendTitle(Player player, String title, String subtitle){
		sendTitle(player, title, subtitle, -1, -1, -1);
	}
	
	public static void sendSubtitle(Player player, String subtitle){
		sendTitle(player, null, subtitle, -1, -1, -1);
	}
	
	public static void sendTitle(Player player, String title, String subtitle, int a, int b, int c){
		
		Object t = null;
        Object sub = null;
        
		if(title == null)
			title = "";
		
		t = Reflection.getInstance(
				Reflection.getConstructor(
	            	"PacketPlayOutTitle", 
	            	Reflection.getMCClass("PacketPlayOutTitle").getDeclaredClasses()[0], 
	            	Reflection.getMCClass("IChatBaseComponent"), 
	         		int.class, 
	           		int.class, 
	            	int.class
	            ),
	            Reflection.getEnumTitleAction("TITLE"),
	            ChatHandler.toComp(title), 
	            a, 
	            b, 
	      		c
			);
		
        if(subtitle != null)
        	sub = Reflection.getInstance(
        		Reflection.getConstructor(
        			"PacketPlayOutTitle", 
        			Reflection.getMCClass("PacketPlayOutTitle").getDeclaredClasses()[0], 
        			Reflection.getMCClass("IChatBaseComponent"), 
     				int.class, 
       				int.class, 
        			int.class
        		),
        		Reflection.getEnumTitleAction("SUBTITLE"),
        		ChatHandler.toComp(subtitle), 
        		a, 
        		b, 
        		c
        	);
        
        Reflection.sendPacket(player, t);
        
        if(sub != null)
        	Reflection.sendPacket(player, sub);
	}
	
	public static void sendTimes(Player player, int a, int b, int c){
		
		Object packet = Reflection.getInstance(
				Reflection.getConstructor(
		            	"PacketPlayOutTitle",
		         		int.class, 
		           		int.class, 
		            	int.class
		            ),
		            a, 
		            b, 
		      		c
				);
		
		Reflection.sendPacket(player, packet);
	}
	
	public static void reset(Player player){
		
		Object packet = Reflection.getInstance(
				Reflection.getConstructor(
		            	"PacketPlayOutTitle", 
		            	Reflection.getMCClass("PacketPlayOutTitle").getDeclaredClasses()[0], 
		            	Reflection.getMCClass("IChatBaseComponent")
		            ),
		            Reflection.getEnumTitleAction("RESET"),
		            null
				);
		
		Reflection.sendPacket(player, packet);
		
	}
}
