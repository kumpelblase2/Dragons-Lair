package de.kumpelblase2.dragonslair;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import de.kumpelblase2.dragonslair.api.NPC;
import de.kumpelblase2.remoteentities.EntityManager;
import de.kumpelblase2.remoteentities.api.RemoteEntity;

public class DLEntityManager extends EntityManager
{
	private final Map<Integer, NPC> m_npcs = new HashMap<Integer, NPC>();

	public DLEntityManager()
	{
		super(DragonsLairMain.getInstance(), false);
	}

	public RemoteEntity createNPC(NPC inNPC)
	{
		RemoteEntity entity = null;
		try
		{
			if(inNPC.getType().isNamed())
				entity = this.createNamedEntity(inNPC.getType(), inNPC.getLocation(), inNPC.getName(), false);
			else
				entity = this.createEntity(inNPC.getType(), inNPC.getLocation(), false);

			this.m_npcs.put(entity.getID(), inNPC);
		}
		catch(Exception e)
		{
			DragonsLairMain.Log.warning("Unable to spawn NPC with id " + inNPC.getID());
			DragonsLairMain.Log.warning(e.getMessage());
		}

		return entity;
	}

	public RemoteEntity getByDatabaseID(int inID)
	{
		int id = this.getEntityIDFromDatabaseID(inID);
		if(id == -1)
			return null;

		return this.getRemoteEntityByID(id);
	}

	public int getEntityIDFromDatabaseID(int inID)
	{
		for(Entry<Integer, NPC> entry : this.m_npcs.entrySet())
		{
			if(entry.getValue().getID() == inID)
				return entry.getKey();
		}

		return -1;
	}

	public int getDatabaseIDFromEntity(RemoteEntity inEntity)
	{
		NPC npc = this.getNPCFromEntity(inEntity);
		if(npc != null)
			return npc.getID();

		return -1;
	}

	public NPC getNPCFromEntity(RemoteEntity inEntity)
	{
		return this.m_npcs.get(inEntity.getID());
	}

	public boolean isSpawned(int inID)
	{
		for(NPC npc : this.m_npcs.values())
		{
			if(npc.getID() == inID)
				return true;
		}

		return false;
	}
}
