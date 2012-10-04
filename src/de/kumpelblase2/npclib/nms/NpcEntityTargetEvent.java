package de.kumpelblase2.npclib.nms;

// original provided by Topcat, modified by kumpelblase2
import org.bukkit.entity.Entity;
import org.bukkit.event.entity.EntityTargetEvent;

public class NpcEntityTargetEvent extends EntityTargetEvent
{
	public static enum NpcTargetReason
	{
		CLOSEST_PLAYER,
		NPC_RIGHTCLICKED,
		NPC_BOUNCED,
		NPC_DAMAGED
	}

	private final NpcTargetReason reason;

	public NpcEntityTargetEvent(final Entity entity, final Entity target, final NpcTargetReason reason)
	{
		super(entity, target, TargetReason.CUSTOM);
		this.reason = reason;
	}

	public NpcTargetReason getNpcReason()
	{
		return this.reason;
	}
}