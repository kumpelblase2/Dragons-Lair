package de.kumpelblase2.dragonslair.api.eventexecutors;

import org.bukkit.entity.Player;
import de.kumpelblase2.dragonslair.DragonsLairMain;
import de.kumpelblase2.dragonslair.api.Event;
import de.kumpelblase2.dragonslair.api.NPC;

public class NPCDespawnExecutor implements EventExecutor
{
	@Override
	public boolean executeEvent(final Event e, final Player p)
	{
		final String npcid = e.getOption("npc_id");
		try
		{
			NPC n = DragonsLairMain.getSettings().getNPCByName(npcid);
			if(n == null)
			{
				final int id = Integer.parseInt(npcid);
				n = DragonsLairMain.getSettings().getNPCs().get(id);
				if(n == null)
					return false;
			}

			DragonsLairMain.getDungeonManager().despawnNPC(n.getID());
			return true;
		}
		catch(final Exception ex)
		{
			DragonsLairMain.Log.warning("Unable to spawn npc from event: " + e.getID());
			DragonsLairMain.Log.warning(ex.getMessage());
		}

		return false;
	}
}