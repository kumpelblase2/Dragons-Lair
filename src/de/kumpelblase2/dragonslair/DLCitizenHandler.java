package de.kumpelblase2.dragonslair;

import net.citizensnpcs.api.event.NPCRightClickEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTargetEvent;
import com.topcat.npclib.nms.NpcEntityTargetEvent;
import de.kumpelblase2.dragonslair.api.NPC;

public class DLCitizenHandler implements Listener
{
	@EventHandler(ignoreCancelled = true)
	public void onNPCTalk(NPCRightClickEvent event)
	{
		NPC npc = DragonsLairMain.getSettings().getNPCByName(event.getNPC().getName());
		if(npc == null)
			return;
		
		if(!DragonsLairMain.isWorldEnabled(event.getPlayer().getWorld().getName()))
			return;
		
		EntityTargetEvent event2 = new NpcEntityTargetEvent(event.getNPC().getPlayer(), event.getPlayer(), NpcEntityTargetEvent.NpcTargetReason.NPC_RIGHTCLICKED);
		Bukkit.getPluginManager().callEvent(event2);
		event.setCancelled(!event2.isCancelled());
	}
}
