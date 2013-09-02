package de.kumpelblase2.dragonslair.conversation;

import org.bukkit.conversations.*;
import org.bukkit.entity.Player;
import de.kumpelblase2.dragonslair.DragonsLairMain;
import de.kumpelblase2.dragonslair.api.ActiveDungeon;

public class SafeWordPrompt extends FixedSetPrompt
{
	public SafeWordPrompt()
	{
		super("cancel", "pause", "stop");
	}

	@Override
	public String getPromptText(final ConversationContext arg0)
	{
		return "<Voice>Do you want to pause the dungeon or stop it? Say either 'stop', 'pause' or 'cancel' depending on your decision.";
	}

	@Override
	protected Prompt acceptValidatedInput(final ConversationContext arg0, final String arg1)
	{
		if(arg1.equals("cancel"))
			arg0.getForWhom().sendRawMessage("<Voice>Lucky you. Otherwise you wouldn't be able to player again. BUHAHAHAHA!");
		else if(arg1.equals("stop"))
		{
			arg0.getForWhom().sendRawMessage("<Voice>Pff. Aren't tough enough? I knew it. Derp.");
			final ActiveDungeon playerDungeon = DragonsLairMain.getDungeonManager().getDungeonOfPlayer(((Player)arg0.getForWhom()).getName());
			if(playerDungeon != null)
				DragonsLairMain.getDungeonManager().stopDungeon(playerDungeon.getInfo().getID(), false);
		}
		else
		{
			arg0.getForWhom().sendRawMessage("<Voice>If you don't come back, something unexpected and really bad thing will happen. I have warned you.");
			DragonsLairMain.getDungeonManager().stopDungeon(DragonsLairMain.getDungeonManager().getDungeonOfPlayer(((Player)arg0.getForWhom()).getName()).getInfo().getName(), true);
		}

		DragonsLairMain.getInstance().getConversationHandler().removeSafeWordConversation((Player)arg0.getForWhom());
		return END_OF_CONVERSATION;
	}
}