package de.kumpelblase2.dragonslair.api.eventexecutors;

import org.bukkit.entity.Player;
import de.kumpelblase2.dragonslair.DragonsLairMain;
import de.kumpelblase2.dragonslair.api.Event;
import de.kumpelblase2.dragonslair.api.NPC;

public class NPCDespawnExecutor implements EventExecutor
{

	@Override
	public boolean executeEvent(Event e, Player p)
	{
		String npcid = e.getOption("npc_id");
		try
		{
			int id = Integer.parseInt(npcid);
			NPC n = DragonsLairMain.getSettings().getNPCs().get(id);
			if(n == null)
				return false;
				
			DragonsLairMain.getDungeonManager().despawnNPC(n.getName());
			
			return true;
		}
		catch(Exception ex)
		{
			DragonsLairMain.Log.warning("Unable to spawn npc from event: " + e.getID());
			DragonsLairMain.Log.warning(ex.getMessage());
		}
		return false;
	}

}
