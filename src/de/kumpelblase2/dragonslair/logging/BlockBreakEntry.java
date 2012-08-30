package de.kumpelblase2.dragonslair.logging;

import java.util.Map;
import org.bukkit.Location;
import org.bukkit.block.*;
import de.kumpelblase2.dragonslair.api.ActiveDungeon;

public class BlockBreakEntry extends BlockEntry
{
	public BlockBreakEntry(final BlockState inBroken, final ActiveDungeon ad)
	{
		super(inBroken, ad);
		this.m_new.clear();
		this.m_new.put("block_type", "0");
		this.m_new.put("data", "" + ((byte)0));
	}

	public BlockBreakEntry(final String inDungeon, final int inParty, final Location inLoc, final Map<String, String> inBefore, final Map<String, String> inNew)
	{
		super(inDungeon, inParty, inLoc, inBefore, inNew);
	}

	@Override
	public LogType getType()
	{
		return LogType.BLOCK_REMOVE;
	}

	@Override
	public boolean isNegotiation(final Recoverable inEntry)
	{
		if(inEntry.getType() != LogType.BLOCK_PLACE)
			return false;
		if(inEntry.getNewData().get("block_type").equals(this.m_before.get("block_type")))
			return true;
		else
			return false;
	}
}
