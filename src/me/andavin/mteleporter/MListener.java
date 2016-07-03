package me.andavin.mteleporter;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import me.andavin.mteleporter.commands.Commandmwarp;

public final class MListener implements Listener {

	public static List<MAction> confirming = new ArrayList<MAction>();
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onChat(AsyncPlayerChatEvent event){
		
		String msg = event.getMessage();
		Player player = event.getPlayer();
		
		if(!msg.startsWith("MTELEPORTER_"))
			return;
		
		event.setCancelled(true);
		MAction searchAction = new MAction(null, player, null);
		
		if(!confirming.contains(searchAction)) {
			player.sendMessage(Message.NO_CONFIRM.toString());
			return;
		}
		
		String[] parts = msg.split("_");
		MAction.A action = this.getAction(parts[1]);
		Method method = action.executionMethod(parts[2]);
		int index = confirming.indexOf(searchAction);
		invoke(method, new Commandmwarp(), confirming.get(index));
		confirming.remove(searchAction);
	}
	
	private MAction.A getAction(String str){
		
		for(MAction.A a : MAction.A.values())
			if(a.toString().equals(str))
				return a;
		return null;
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onMove(PlayerMoveEvent event){
		
		int x = event.getTo().getBlockX(),
			y = event.getTo().getBlockY(),
			z = event.getTo().getBlockZ(),
			fx = event.getFrom().getBlockX(),
			fy = event.getFrom().getBlockY(),
			fz = event.getFrom().getBlockZ();
		
		if((x != fx || y != fy || z != fz)
			&& !MTeleporter.plugin.move.contains(event.getPlayer()))
			MTeleporter.plugin.move.add(event.getPlayer());
	}
	
	private void invoke(Method method, Object obj, Object... args){
		try {
			method.invoke(obj, args);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
		}
	}
}
