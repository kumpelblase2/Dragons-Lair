package de.kumpelblase2.dragonslair.logging;

import java.util.Map;
import org.bukkit.Location;
import org.bukkit.block.*;
import de.kumpelblase2.dragonslair.api.ActiveDungeon;

public class BlockBreakEntry extends BlockEntry
{	
	public BlockBreakEntry(BlockState inBroken, ActiveDungeon ad)
	{
		super(inBroken, ad);
		this.m_new.clear();
		this.m_new.put("block_type", "0");
		this.m_new.put("data", "" + ((byte)0));
	}
	
	public BlockBreakEntry(String inDungeon, int inParty, Location inLoc, Map<String, String> inBefore, Map<String, String> inNew)
	{
		super(inDungeon, inParty, inLoc, inBefore, inNew);
	}

	@Override
	public LogType getType()
	{
		return LogType.BLOCK_REMOVE;
	}
	
	@Override
	public boolean isNegotiation(Recoverable inEntry)
	{
		if(inEntry.getType() != LogType.BLOCK_PLACE)
			return false;
		
		if(inEntry.getNewData().get("block_type").equals(this.m_before.get("block_type")))
			return true;
		else
			return false;
	}
}
