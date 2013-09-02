package de.kumpelblase2.dragonslair.api.eventexecutors;

import org.bukkit.entity.Player;
import de.kumpelblase2.dragonslair.DragonsLairMain;
import de.kumpelblase2.dragonslair.api.Event;
import de.kumpelblase2.dragonslair.api.NPC;
import de.kumpelblase2.remoteentities.api.Fightable;
import de.kumpelblase2.remoteentities.api.RemoteEntity;

public class NPCStopAttackEventExecutor implements EventExecutor
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
				final Integer id = Integer.parseInt(npcid);
				n = DragonsLairMain.getSettings().getNPCs().get(id);
				if(n == null)
					return false;
			}

			final RemoteEntity npc = DragonsLairMain.getDungeonManager().getNPCManager().getByDatabaseID(n.getID());
			if(npc != null && npc instanceof Fightable)
				((Fightable)npc).loseTarget();

			return true;
		}
		catch(final Exception ex)
		{
			DragonsLairMain.Log.warning("Unable to stop npc attack from event: " + e.getID());
			DragonsLairMain.Log.warning(ex.getMessage());
		}

		return false;
	}
}