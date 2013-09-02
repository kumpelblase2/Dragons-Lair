package de.kumpelblase2.dragonslair.commanddialogs.dungeon;

import org.bukkit.ChatColor;
import org.bukkit.conversations.*;
import org.bukkit.entity.Player;
import de.kumpelblase2.dragonslair.DragonsLairMain;
import de.kumpelblase2.dragonslair.api.ActiveDungeon;
import de.kumpelblase2.dragonslair.api.Dungeon;
import de.kumpelblase2.dragonslair.conversation.AnswerConverter;
import de.kumpelblase2.dragonslair.conversation.AnswerType;
import de.kumpelblase2.dragonslair.utilities.GeneralUtilities;
import de.kumpelblase2.dragonslair.utilities.WorldUtility;

public class DungeonEditDialog extends ValidatingPrompt
{
	private final String[] options = new String[]{ "name", "starting objective", "starting chapter", "starting pos", "safe word", "min players", "max players", "starting message", "ending message", "ready message", "breakable blocks" };

	@Override
	public String getPromptText(final ConversationContext context)
	{
		if(context.getSessionData("dungeon") == null)
			return ChatColor.GREEN + "Please enter the name of the dungeon you want to edit:";
		else if(context.getSessionData("option") != null && context.getSessionData("value") == null)
		{
			final String option = (String)context.getSessionData("option");
			if(option.equals("name"))
				return ChatColor.GREEN + "Please enter then new name:";
			else if(option.equals("starting objective"))
				return ChatColor.GREEN + "Please enter the id of the new objective:";
			else if(option.equals("starting chapter"))
				return ChatColor.GREEN + "Please enter the id of the new chapter:";
			else if(option.equals("starting pos"))
				return ChatColor.GREEN + "Please specify the new position:";
			else if(option.equals("safe word"))
				return ChatColor.GREEN + "Please enter a new safe word:";
			else if(option.equals("min players"))
				return ChatColor.GREEN + "Please enter a new minimum amount of players:";
			else if(option.equals("max players"))
				return ChatColor.GREEN + "Please enter a new maximum amount of players:";
			else if(option.equals("starting message"))
				return ChatColor.GREEN + "Please enter a new starting message:";
			else if(option.equals("ending message"))
				return ChatColor.GREEN + "Please enter a new ending message:";
			else if(option.equals("ready message"))
				return ChatColor.GREEN + "Please enter a new ready message:";
			else
				return ChatColor.GREEN + "Should blocks be breakable?";
		}

		context.getForWhom().sendRawMessage("What do you want to edit?");
		final StringBuilder sb = new StringBuilder();
		for(int i = 0; i < this.options.length; i++)
		{
			sb.append(ChatColor.AQUA).append(this.options[i]);
			if(i != this.options.length - 1)
				sb.append(",");
		}

		return sb.toString();
	}

	@Override
	protected Prompt acceptValidatedInput(final ConversationContext arg0, final String arg1)
	{
		if(arg1.equals("cancel"))
		{
			arg0.setSessionData("dungeon", null);
			arg0.setSessionData("option", null);
			return new DungeonManageDialog();
		}

		if(arg0.getSessionData("dungeon") == null)
		{
			if(arg1.equals("back"))
				return new DungeonManageDialog();

			try
			{
				final Integer id = Integer.parseInt(arg1);
				arg0.setSessionData("dungeon", id);
			}
			catch(final Exception e)
			{
				final Dungeon d = DragonsLairMain.getSettings().getDungeonByName(arg1);
				arg0.setSessionData("dungeon", d.getID());
			}
		}
		else if(arg0.getSessionData("option") == null)
		{
			if(arg1.equals("back"))
			{
				arg0.setSessionData("dungeon", null);
				return this;
			}

			arg0.setSessionData("option", arg1);
		}
		else if(arg0.getSessionData("option") != null)
		{
			if(arg1.equals("back"))
			{
				arg0.setSessionData("option", null);
				return this;
			}

			final Dungeon d = DragonsLairMain.getSettings().getDungeons().get(arg0.getSessionData("dungeon"));
			if(d == null)
			{
				arg0.getForWhom().sendRawMessage(ChatColor.RED + "An error occurred.");
				return END_OF_CONVERSATION;
			}

			final String option = (String)arg0.getSessionData("option");
			if(option.equals("name"))
				d.setName(arg1);
			else if(option.equals("starting objective"))
				d.setStartingObjective(Integer.parseInt(arg1));
			else if(option.equals("starting chapter"))
				d.setStartingChapter(Integer.parseInt(arg1));
			else if(option.equals("starting pos"))
			{
				if(arg1.equals("here")) d.setStartingLocation(((Player)arg0.getForWhom()).getLocation());
				else d.setStartingLocation(WorldUtility.stringToLocation(arg1));
			}
			else if(option.equals("safe word"))
				d.setSafeWord(arg1);
			else if(option.equals("min players"))
				d.setMinPlayers(Integer.parseInt(arg1));
			else if(option.equals("max players"))
				d.setMaxPlayers(Integer.parseInt(arg1));
			else if(option.equals("starting message"))
				d.setStartingMessage(arg1);
			else if(option.equals("ending message"))
				d.setEndMessage(arg1);
			else if(option.equals("ready message"))
				d.setPartyReadyMessage(arg1);
			else
			{
				final AnswerType answer = new AnswerConverter(arg1).convert();
				d.setBlocksBreakable((answer == AnswerType.AGREEMENT || answer == AnswerType.CONSIDERING_AGREEMENT));
			}

			d.save();
			arg0.getForWhom().sendRawMessage(ChatColor.GREEN + "Dungeon '" + arg0.getSessionData("dungeon") + "' edited!");
			arg0.setSessionData("dungeon", null);
			arg0.setSessionData("option", null);
			return new DungeonManageDialog();
		}
		return this;
	}

	@Override
	protected boolean isInputValid(final ConversationContext arg0, final String arg1)
	{
		if(arg1.equals("back") || arg1.equals("cancel"))
			return true;

		if(arg0.getSessionData("dungeon") == null)
		{
			Dungeon d;
			try
			{
				final Integer id = Integer.parseInt(arg1);
				d = DragonsLairMain.getSettings().getDungeons().get(id);
				if(d == null)
				{
					arg0.getForWhom().sendRawMessage(ChatColor.RED + "A dungeon with that id doesn't exists.");
					return false;
				}
			}
			catch(final Exception e)
			{
				d = DragonsLairMain.getSettings().getDungeonByName(arg1);
				if(d == null)
				{
					arg0.getForWhom().sendRawMessage(ChatColor.RED + "The dungeon does not exist.");
					return false;
				}
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
		else if(arg0.getSessionData("option") == null)
		{
			for(final String option : this.options)
			{
				if(option.equals(arg1))
					return true;
			}

			return false;
		}
		else
			return GeneralUtilities.isValidOptionInput(arg0, arg1, (String)arg0.getSessionData("option"));
	}
}