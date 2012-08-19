package de.kumpelblase2.dragonslair.logging;

import java.util.Map;
import org.bukkit.Location;
import org.bukkit.block.BlockState;
import de.kumpelblase2.dragonslair.api.ActiveDungeon;

public class BlockPlaceEntry extends BlockEntry
{
	public BlockPlaceEntry(BlockState inState, ActiveDungeon ad)
	{
		super(inState, ad);
		this.m_before.clear();
		this.m_before.put("block_type", "0");
		this.m_before.put("data", "" + ((byte)0));
	}
	
	public BlockPlaceEntry(String inDungeon, int inParty, Location inLoc, Map<String, String> inBefore, Map<String, String> inNew)
	{
		super(inDungeon, inParty, inLoc, inBefore, inNew);
	}

	@Override
	public LogType getType()
	{
		return LogType.BLOCK_PLACE;
	}
	
	@Override
	public boolean isNegotiation(Recoverable inEntry)
	{
		if(inEntry.getType() != LogType.BLOCK_REMOVE)
			return false;
		
		if(inEntry.getNewData().get("block_type").equals(this.m_before.get("block_type")))
			return true;
		else
			return false;
	}
}
