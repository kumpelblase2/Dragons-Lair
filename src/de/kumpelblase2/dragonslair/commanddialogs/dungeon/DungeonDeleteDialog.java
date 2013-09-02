package de.kumpelblase2.dragonslair.commanddialogs.dungeon;

import org.bukkit.ChatColor;
import org.bukkit.conversations.*;
import de.kumpelblase2.dragonslair.DragonsLairMain;
import de.kumpelblase2.dragonslair.api.ActiveDungeon;
import de.kumpelblase2.dragonslair.api.Dungeon;

public class DungeonDeleteDialog extends ValidatingPrompt
{
	@Override
	public String getPromptText(final ConversationContext arg0)
	{
		if(arg0.getSessionData("dungeon_name") == null)
			return ChatColor.GREEN + "Please enter the name of the dungeon to remove:";
		else
			return ChatColor.YELLOW + "Are you sure you want to delete the dungeon? Write 'delete' in the chat to confirm.";
	}

	@Override
	protected Prompt acceptValidatedInput(final ConversationContext arg0, final String arg1)
	{
		if(arg1.equals("cancel"))
		{
			arg0.setSessionData("dungeon_name", null);
			return new DungeonManageDialog();
		}
		if(arg0.getSessionData("dungeon_name") == null)
		{
			if(arg1.equals("back")) return new DungeonManageDialog();
			arg0.setSessionData("dungeon_name", arg1);
			return this;
		}
		else if(arg1.equals("back"))
		{
			arg0.setSessionData("dungeon_name", null);
			return this;
		}
		else if(arg1.equals("delete"))
		{
			final Dungeon d = DragonsLairMain.getSettings().getDungeonByName(arg1);
			DragonsLairMain.debugLog("Deleted dungeon '" + d.getName() + "'");
			d.remove();
			DragonsLairMain.getSettings().getDungeons().remove(d.getID());
		}
		arg0.setSessionData("dungeon_name", null);
		return new DungeonManageDialog();
	}

	@Override
	protected boolean isInputValid(final ConversationContext arg0, final String arg1)
	{
		if(arg0.getSessionData("dungeon_name") == null)
		{
			if(arg1.equals("back")) return true;
			final Dungeon d = DragonsLairMain.getSettings().getDungeonByName(arg1);
			if(d == null)
			{
				arg0.getForWhom().sendRawMessage(ChatColor.RED + "The dungeon does not exist.");
				return false;
			}
			for(final ActiveDungeon ad : DragonsLairMain.getDungeonManager().getActiveDungeons())
			{
				if(ad.getInfo().getID() == d.getID())
				{
					arg0.getForWhom().sendRawMessage(ChatColor.RED + "The dungeon is currently in use.");
					return false;
				}
			}
			return true;
		}
		return true;
	}
}
