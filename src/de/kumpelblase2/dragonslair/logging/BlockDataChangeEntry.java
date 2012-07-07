package de.kumpelblase2.dragonslair.logging;

import org.bukkit.block.BlockState;
import de.kumpelblase2.dragonslair.api.ActiveDungeon;

public class BlockDataChangeEntry extends BlockEntry
{
	public BlockDataChangeEntry(BlockState inState, ActiveDungeon ad)
	{
		super(inState, ad);
	}
	
	@Override
	public LogType getType()
	{
		return LogType.DATA_CHANGE;
	}
	
	@Override
	public boolean isNegotiation(Recoverable r)
	{
		if(!(r instanceof BlockDataChangeEntry))
			return false;		
		
		
		//sanity check, just to make sure
		if(!r.getNewData().get("data").equals(this.m_before.get("data")))
			return false;
		
		return true;
	}
}
