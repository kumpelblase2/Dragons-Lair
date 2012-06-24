package de.kumpelblase2.dragonslair.commanddialogs.event;

import org.bukkit.ChatColor;
import org.bukkit.conversations.*;
import de.kumpelblase2.dragonslair.DragonsLairMain;
import de.kumpelblase2.dragonslair.api.*;
import de.kumpelblase2.dragonslair.utilities.GeneralUtilities;

public class EventEditDialog extends ValidatingPrompt
{
	private final String[] options = new String[] { "type", "add option", "delete option", "edit option" };
	private Event e;
	
	@Override
	public String getPromptText(ConversationContext arg0)
	{
		if(arg0.getSessionData("event_id") == null)
		{
			return ChatColor.GREEN + "Please enter the event id you want to edit:";
		}
		else if(arg0.getSessionData("option") == null)
		{
			arg0.getForWhom().sendRawMessage("What do you want to edit?");
			StringBuilder sb = new StringBuilder();
			for(int i = 0; i < this.options.length; i++)
			{
				sb.append(this.options[i]);
				if(i != this.options.length - 1)
					sb.append(", ");
			}
			return ChatColor.AQUA + sb.toString();
		}
		else if(arg0.getSessionData("option").equals("type"))
		{
			arg0.getForWhom().sendRawMessage(ChatColor.GREEN + "Please enter a new type for this event:");
			return ChatColor.GREEN + "Current type: " + ChatColor.WHITE + this.e.getActionType().toString();
		}
		else if(arg0.getSessionData("option").equals("add option"))
		{
			if(arg0.getSessionData("option_value") == null)
				return ChatColor.GREEN + "Please enter a new option:";
			else
				return ChatColor.GREEN + "Please enter a value for the option:";
		}
		else if(arg0.getSessionData("option").equals("delete option"))
		{
			arg0.getForWhom().sendRawMessage(ChatColor.GREEN + "Please enter the option to delete:");
			return ChatColor.GREEN + "Current options: " + ChatColor.WHITE + e.getOptionString();
		}
		else
		{
			if(arg0.getSessionData("option_value") == null)
			{
				arg0.getForWhom().sendRawMessage(ChatColor.GREEN + "Please enter the option to edit:");
				return ChatColor.GREEN + "Current options: " + ChatColor.WHITE + e.getOptionString();
			}
			else
				return ChatColor.GREEN + "Please enter a new value for the option:";
		}
	}

	@Override
	protected Prompt acceptValidatedInput(ConversationContext arg0, String arg1)
	{
		if(arg1.equals("cancel"))
		{
			arg0.setSessionData("event_id", null);
			arg0.setSessionData("option", null);
			arg0.setSessionData("option_value", null);
			return new EventManageDialog();
		}
		
		if(arg0.getSessionData("event_id") == null)
		{
			if(arg1.equals("back"))
				return new EventManageDialog();
			
			Integer id = Integer.parseInt(arg1);
			arg0.setSessionData("event_id", id);
			this.e = DragonsLairMain.getSettings().getEvents().get(id);
		}
		else if(arg0.getSessionData("option") == null)
		{
			if(arg1.equals("back"))
			{
				arg0.setSessionData("event_id", null);
				return this;
			}
			
			arg0.setSessionData("option", arg1);
		}
		else
		{
			if(arg1.equals("back"))
			{
				arg0.setSessionData("option", null);
				return this;
			}
			
			if(arg0.getSessionData("option").equals("type"))
			{
				this.e.setActionType(EventActionType.valueOf(arg1.toUpperCase().replace(" ", "_")));
				this.e.save();
				arg0.setSessionData("event_id", null);
				arg0.setSessionData("option", null);
				return new EventManageDialog();
			}
			else if(arg0.getSessionData("option").equals("delete option"))
			{
				if(EventActionOptions.byType(this.e.getActionType()).isRequired(arg1))
				{
					arg0.getForWhom().sendRawMessage(ChatColor.RED + "You can't remove a required option.");
					arg0.setSessionData("option", null);
					return this;
				}
				
				this.e.removeOption(arg1);
				this.e.save();
				arg0.setSessionData("event_id", null);
				arg0.setSessionData("option", null);
				return new EventManageDialog();
			}
			else if(arg0.getSessionData("option").equals("edit option"))
			{
				if(arg0.getSessionData("option_value") == null)
				{
					arg0.setSessionData("option_value", arg1);
				}
				else
				{
					String option = (String)arg0.getSessionData("option_value");
					this.e.setOption(option, arg1);
					this.e.save();
					arg0.setSessionData("event_id", null);
					arg0.setSessionData("option", null);
					arg0.setSessionData("option_value", null);
					return new EventManageDialog();
				}
			}
			else
			{
				if(arg0.getSessionData("option_value") == null)
				{
					arg0.setSessionData("option_value", arg1);
				}
				else
				{
					String option = (String)arg0.getSessionData("option_value");
					this.e.setOption(option, arg1);
					this.e.save();
					arg0.setSessionData("event_id", null);
					arg0.setSessionData("option", null);
					arg0.setSessionData("option_value", null);
					return new EventManageDialog();
				}
			}
		}
		return this;
	}

	@Override
	protected boolean isInputValid(ConversationContext arg0, String arg1)
	{
		if(arg1.equals("back") || arg1.equals("cancel"))
			return true;
		
		if(arg0.getSessionData("event_id") == null)
		{
			try
			{
				int id = Integer.parseInt(arg1);
				if(!DragonsLairMain.getSettings().getEvents().containsKey((Integer)id))
				{
					arg0.getForWhom().sendRawMessage(ChatColor.RED + "An event with that id doesn't exist.");
					return false;
				}
				return true;
			}
			catch(Exception e)
			{
				arg0.getForWhom().sendRawMessage(ChatColor.RED + "Not a valid number.");
				return false;
			}
		}
		else if(arg0.getSessionData("option") == null)
		{
			for(String option : this.options)
			{
				if(option.equals(arg1))
					return true;
			}
			return false;
		}
		else
		{
			if(arg0.getSessionData("option").equals("type"))
			{
				for(EventActionType type : EventActionType.values())
				{
					if(type.toString().toLowerCase().equals(arg1.replace(" ", "_").toLowerCase()))
						return true;
				}
				arg0.getForWhom().sendRawMessage(ChatColor.RED + "That type doesn't exist.");
				return false;
			}
			else if(arg0.getSessionData("option").equals("delete option"))
			{
				if(e.getOption(arg1) == null)
				{
					arg0.getForWhom().sendRawMessage(ChatColor.RED + "The event doesn't have this option.");
					return false;
				}
				return true;
			}
			else if(arg0.getSessionData("option").equals("edit option"))
			{
				if(arg0.getSessionData("option_value") == null)
				{
					if(e.getOption(arg1) == null)
					{
						arg0.getForWhom().sendRawMessage(ChatColor.RED + "The event doesn't have this option.");
						return false;
					}
					return true;
				}
				else
				{
					String option = (String)arg0.getSessionData("option_value");
					return GeneralUtilities.isValidOptionInput(arg0, arg1, option);
				}
			}
			else
			{
				if(arg0.getSessionData("option_value") == null)
				{
					EventActionOptions options = EventActionOptions.valueOf(e.getActionType().toString());
					for(String option : options.getRequiredTypes())
					{
						if(option.equals(arg1))
							return true;
					}
					
					for(String option : options.getOptionalTypes())
					{
						if(option.equals(arg1))
							return true;
					}
					
					arg0.getForWhom().sendRawMessage(ChatColor.RED + "There is no such option for this event type.");
					return false;
				}
				else
				{
					String option = (String)arg0.getSessionData("option_value");
					return GeneralUtilities.isValidOptionInput(arg0, arg1, option);
				}
			}
		}
	}
}
