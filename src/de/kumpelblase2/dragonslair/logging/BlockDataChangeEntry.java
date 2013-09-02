package de.kumpelblase2.dragonslair.logging;

import org.bukkit.block.BlockState;
import de.kumpelblase2.dragonslair.api.ActiveDungeon;

public class BlockDataChangeEntry extends BlockEntry
{
	public BlockDataChangeEntry(final BlockState inState, final ActiveDungeon ad)
	{
		super(inState, ad);
	}

	@Override
	public LogType getType()
	{
		return LogType.DATA_CHANGE;
	}

	@Override
	public boolean isNegotiation(final Recoverable r)
	{
		if(!(r instanceof BlockDataChangeEntry))
			return false;

		// sanity check, just to make sure
		return r.getNewData().get("data").equals(this.m_before.get("data"));
	}
}