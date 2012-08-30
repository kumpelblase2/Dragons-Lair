package de.kumpelblase2.dragonslair.api.eventexecutors;

import java.util.*;
import org.bukkit.entity.*;
import de.kumpelblase2.dragonslair.DragonsLairMain;
import de.kumpelblase2.dragonslair.api.Event;
import de.kumpelblase2.dragonslair.api.NPC;
import de.kumpelblase2.dragonslair.utilities.WorldUtility;
import de.kumpelblase2.npclib.entity.HumanNPC;

public class NPCAttackEventExecutor implements EventExecutor
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
			final String target = e.getOption("target");
			final HumanNPC npc = DragonsLairMain.getDungeonManager().getNPCByID(n.getID());
			List<EntityType> types = new ArrayList<EntityType>();
			if(target == null || target.equals("enemy"))
				types = Arrays.asList(new EntityType[] { EntityType.PLAYER });
			else if(target.equals("player"))
			{
				types = new ArrayList<EntityType>(Arrays.asList(EntityType.values()));
				types.remove(EntityType.PLAYER);
			}
			final LivingEntity nearest = WorldUtility.getNearestEntity(npc.getBukkitEntity().getLocation(), npc.getBukkitEntity().getNearbyEntities(10, 3, 10), types);
			if(nearest != null && npc != null)
				npc.startAttacking(nearest);
			return true;
		}
		catch(final Exception ex)
		{
			DragonsLairMain.Log.warning("Unable to let npc starting to attack from event: " + e.getID());
			DragonsLairMain.Log.warning(ex.getMessage());
		}
		return false;
	}
}
