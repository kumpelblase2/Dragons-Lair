package de.kumpelblase2.dragonslair.commanddialogs.trigger;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.conversations.*;
import de.kumpelblase2.dragonslair.DragonsLairMain;
import de.kumpelblase2.dragonslair.api.*;
import de.kumpelblase2.dragonslair.conversation.AnswerConverter;
import de.kumpelblase2.dragonslair.conversation.AnswerType;
import de.kumpelblase2.dragonslair.utilities.GeneralUtilities;

public class TriggerCreateDialog extends ValidatingPrompt
{

	@Override
	public String getPromptText(ConversationContext arg0)
	{
		if(arg0.getSessionData("trigger_type") == null)
		{
			arg0.getForWhom().sendRawMessage(ChatColor.GREEN + "Please enter a type of the trigger:");
			StringBuilder sb = new StringBuilder();
			for(int i = 0; i < TriggerType.values().length; i++)
			{
				sb.append(ChatColor.AQUA + TriggerType.values()[i].toString() + ChatColor.WHITE);
				if(i != TriggerType.values().length - 1)
					sb.append(", ");
			}
			return ChatColor.GREEN  + "Avaible types: " + sb.toString();
		}
		else if(arg0.getSessionData("event_ids") == null)
		{
			return ChatColor.GREEN + "Please enter the event ids that should be excuted (separated by a komma):";
		}
		else if(arg0.getSessionData("add_option") == null)
		{
			return ChatColor.GREEN + "Do you want to add another option to the trigger?";
		}
		else if((Boolean)arg0.getSessionData("add_option"))
		{
			arg0.getForWhom().sendRawMessage(ChatColor.GREEN + "Please enter the type of the option:");
			return ChatColor.GREEN + "Available options: " + ChatColor.AQUA + TriggerTypeOptions.valueOf((String)arg0.getSessionData("trigger_type")).getOptionString();
		}
		else
		{
			return ChatColor.GREEN + "Please enter a value for the option:";
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	protected Prompt acceptValidatedInput(ConversationContext arg0, String arg1)
	{
		if(arg1.equals("cancel"))
		{
			arg0.setSessionData("trigger_type", null);
			arg0.setSessionData("event_ids", null);
			arg0.setSessionData("option", null);
			arg0.setSessionData("options", null);
			arg0.setSessionData("trigger_option_type", null);
			return new TriggerManageDialog();
		}
		
		if(arg0.getSessionData("trigger_type") == null)
		{
			if(arg1.equals("back"))
				return new TriggerManageDialog();
			
			arg0.setSessionData("trigger_type", arg1.toUpperCase().replace(" ", "_"));
		}
		else if(arg0.getSessionData("event_ids") == null)
		{
			if(arg1.equals("back"))
			{
				arg0.setSessionData("trigger_type", null);
				return this;
			}
			
			arg0.setSessionData("event_ids", arg1);
		}
		else if(arg0.getSessionData("add_option") == null)
		{
			if(arg1.equals("back"))
			{
				arg0.setSessionData("event_ids", null);
				return this;
			}
			
			if(arg0.getSessionData("options") == null)
				arg0.setSessionData("options", new ArrayList<Option>());
			
			AnswerType answer = new AnswerConverter(arg1).convert();
			if(answer == AnswerType.AGREEMENT || answer == AnswerType.CONSIDERING_AGREEMENT || answer == AnswerType.CONSIDERING)
			{
				arg0.setSessionData("add_option", true);
			}
			else
			{
				List<Option> options = (List<Option>)arg0.getSessionData("options");
				TriggerType type = TriggerType.valueOf((String)arg0.getSessionData("trigger_type"));
				for(String required : TriggerTypeOptions.byType(type).getRequiredOptions())
				{
					boolean found = false;
					for(Option option : options)
					{
						if(option.getType().equalsIgnoreCase(required))
						{
							found = true;
							break;
						}
					}
					
					if(!found)
					{
						arg0.setSessionData("add_option", null);
						arg0.getForWhom().sendRawMessage(ChatColor.RED + "A required option is missing.");
						return this;
					}
				}
				
				Trigger t = new Trigger();
				t.setType(type);
				String[] idString = ((String)arg0.getSessionData("event_ids")).split(",");
				List<Integer> ids = new ArrayList<Integer>();
				for(int i = 0; i < idString.length; i++)
				{
					Integer id = Integer.parseInt(idString[i]);
					if(!ids.contains((Integer)id) && id != 0);
						ids.add(id);
				}
				t.setEventIDs(ids);
				t.setOptions((options).toArray(new Option[0]));
				GeneralUtilities.recalculateOptions(t);
				t.save();
				DragonsLairMain.debugLog("Created trigger with id '" + t.getID() + "'");
				DragonsLairMain.getSettings().getTriggers().put(t.getID(), t);
				DragonsLairMain.getInstance().getEventHandler().reloadTriggers();
				arg0.setSessionData("trigger_type", null);
				arg0.setSessionData("add_option", null);
				arg0.setSessionData("options", null);
				arg0.setSessionData("event_ids", null);
				return new TriggerManageDialog();
			}
		}
		else if((Boolean)arg0.getSessionData("add_option"))
		{
			if(arg1.equals("back"))
			{
				arg0.setSessionData("add_option", null);
				return this;
			}
			
			arg0.setSessionData("trigger_option_type", arg1);
			arg0.setSessionData("add_option", false);
		}
		else
		{
			if(arg1.equals("back"))
			{
				arg0.setSessionData("add_option", true);
				return this;
			}
			
			Option option = new Option((String)arg0.getSessionData("trigger_option_type"), arg1);
			((List<Option>)arg0.getSessionData("options")).add(option);
			arg0.setSessionData("trigger_option_type", null);
			arg0.setSessionData("add_option", null);
		}
		return this;
	}

	@Override
	protected boolean isInputValid(ConversationContext arg0, String arg1)
	{
		if(arg1.equals("back") || arg1.equals("cancel"))
			return true;
		
		if(arg0.getSessionData("trigger_type") == null)
		{
			for(TriggerType type : TriggerType.values())
			{
				if(type.toString().equals(arg1.toUpperCase().replace(" ", "_")))
					return true;
			}
			return false;
		}
		else if(arg0.getSessionData("event_ids") == null)
		{
			arg1 = arg1.trim();
			if(arg1.contains(","))
			{
				String[] ids = arg1.split(",");
				for(String id : ids)
				{
					try
					{
						Integer eid = Integer.parseInt(id);
						if(DragonsLairMain.getSettings().getEvents().get(eid) == null && eid != 0)
						{
							arg0.getForWhom().sendRawMessage(ChatColor.RED + "An event with the id '" + id + "' doesn't exist.");
							return false;
						}
					}
					catch(Exception e)
					{
						arg0.getForWhom().sendRawMessage(ChatColor.RED + "The string '" + id + "' is not a valid number.");
						return false;
					}
				}
				return true;
			}
			else
			{
				try
				{
					Integer id = Integer.parseInt(arg1);
					if(DragonsLairMain.getSettings().getEvents().get(id) == null && id != 0)
					{
						arg0.getForWhom().sendRawMessage(ChatColor.RED + "An event with that id doesn't exist.");
						return false;
					}
				}
				catch(Exception e)
				{
					arg0.getForWhom().sendRawMessage(ChatColor.RED + "Not a valid number.");
					return false;
				}
				return true;
			}
		}
		else if(arg0.getSessionData("add_option") == null)
		{
			AnswerType type = new AnswerConverter(arg1).convert();
			if(type == AnswerType.NOTHING)
				return false;
			
			return true;
		}
		else if((Boolean)arg0.getSessionData("add_option"))
		{
			TriggerTypeOptions options = TriggerTypeOptions.valueOf((String)arg0.getSessionData("trigger_type"));
			if(options.hasOption(arg1))
				return true;
			
			arg0.getForWhom().sendRawMessage(ChatColor.RED + "The option is not avaiable for this trigger type.");
			return false;
		}
		else
		{
			String option = (String)arg0.getSessionData("trigger_option_type");
			return GeneralUtilities.isValidOptionInput(arg0, arg1, option);
		}
	}
}
