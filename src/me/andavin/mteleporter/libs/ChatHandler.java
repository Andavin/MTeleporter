package me.andavin.mteleporter.libs;

import org.bukkit.entity.Player;

public final class ChatHandler {
	
	public static void sendMessage(Player player, Object msg){
		Object packet = Reflection.getInstance(Reflection.getConstructor("PacketPlayOutChat", Reflection.getMCClass("IChatBaseComponent")), msg);
		Reflection.sendPacket(player, packet);
	}
	
	public static Object createButton(String begin, String button, String command, String hover){
		return Reflection.addSibling(toComp(begin), Reflection.setChatModifier(toComp(button), runCmd(command, hover)));
	}
	
	public static void sendCommand(Player player, String begin, String clickable, String command, String hover, String end){
		Object msg = Reflection.addSibling(Reflection.addSibling(toComp(begin), Reflection.setChatModifier(toComp(clickable), runCmd(command, hover))), toComp(end));
		sendMessage(player, msg);
	}
	
	public static void sendSuggestion(Player player, String begin, String clickable, String command, String hover, String end){
		Object msg = Reflection.addSibling(Reflection.addSibling(toComp(begin), Reflection.setChatModifier(toComp(clickable), suggestCmd(command, hover))), toComp(end));
		sendMessage(player, msg);
	}
	
	public static void sendURL(Player player, String begin, String clickable, String url, String hover, String end){
		Object msg = Reflection.addSibling(Reflection.addSibling(toComp(begin), Reflection.setChatModifier(toComp(clickable), openURL(url, hover))), toComp(end));
		sendMessage(player, msg);
	}
	
	public static Object toComp(String str){
		return Reflection.getInstance("ChatComponentText", str);
	}
	
	public static Object runCmd(String command, String hover){
		
		Object style = Reflection.getInstance("ChatModifier");
		Object cc = Reflection.getInstance("ChatClickable", Reflection.getEnumClickAction("RUN_COMMAND"), command);
		style = Reflection.setChatClickable(style, cc);
		
		if(hover != null) {
			Object ch = Reflection.getInstance(
				Reflection.getConstructor(
					"ChatHoverable",
					Reflection.getMCClass("ChatHoverable").getDeclaredClasses()[0], 
					Reflection.getMCClass("IChatBaseComponent")
				), 
				Reflection.getEnumHoverAction("SHOW_TEXT"), toComp(hover)
			);
			style = Reflection.setChatHoverable(style, ch);
		}
		
		return style;
	}
	
	public static Object suggestCmd(String command, String hover){
		
		Object style = Reflection.getInstance("ChatModifier");
		Object cc = Reflection.getInstance("ChatClickable", Reflection.getEnumClickAction("SUGGEST_COMMAND"), command);
		style = Reflection.setChatClickable(style, cc);
		
		if(hover != null) {
			Object ch = Reflection.getInstance(
				Reflection.getConstructor(
					"ChatHoverable",
					Reflection.getMCClass("ChatHoverable").getDeclaredClasses()[0], 
					Reflection.getMCClass("IChatBaseComponent")
				), 
				Reflection.getEnumHoverAction("SHOW_TEXT"), toComp(hover)
			);
			style = Reflection.setChatHoverable(style, ch);
		}
		
		return style;
	}
	
	public static Object openURL(String url, String hover){
		
		Object style = Reflection.getInstance("ChatModifier");
		Object cc = Reflection.getInstance("ChatClickable", Reflection.getEnumClickAction("OPEN_URL"), url);
		style = Reflection.setChatClickable(style, cc);
		
		if(hover != null) {
			Object ch = Reflection.getInstance(
				Reflection.getConstructor(
					"ChatHoverable",
					Reflection.getMCClass("ChatHoverable").getDeclaredClasses()[0], 
					Reflection.getMCClass("IChatBaseComponent")
				), 
				Reflection.getEnumHoverAction("SHOW_TEXT"), toComp(hover)
			);
			style = Reflection.setChatHoverable(style, ch);
		}
		
		return style;
	}
}
