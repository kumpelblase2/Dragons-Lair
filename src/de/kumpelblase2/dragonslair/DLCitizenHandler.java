package de.kumpelblase2.dragonslair;

import net.citizensnpcs.api.event.NPCRightClickEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import de.kumpelblase2.dragonslair.api.NPC;
import de.kumpelblase2.remoteentities.api.events.RemoteEntityInteractEvent;

public class DLCitizenHandler implements Listener
{
	@EventHandler(ignoreCancelled = true)
	public void onNPCTalk(final NPCRightClickEvent event)
	{
		final NPC npc = DragonsLairMain.getSettings().getNPCByName(event.getNPC().getName());
		if(npc == null)
			return;

		if(!DragonsLairMain.isWorldEnabled(event.getClicker().getWorld().getName()))
			return;

		final RemoteEntityInteractEvent event2 = new RemoteEntityInteractEvent(DragonsLairMain.getDungeonManager().getNPCByEntity(event.getNPC().getBukkitEntity()), event.getClicker());
		Bukkit.getPluginManager().callEvent(event2);
		event.setCancelled(!event2.isCancelled());
	}
}