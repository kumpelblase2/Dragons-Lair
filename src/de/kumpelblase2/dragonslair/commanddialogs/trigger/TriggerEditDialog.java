package de.kumpelblase2.dragonslair.commanddialogs.trigger;

import org.bukkit.ChatColor;
import org.bukkit.conversations.*;
import de.kumpelblase2.dragonslair.DragonsLairMain;
import de.kumpelblase2.dragonslair.api.*;
import de.kumpelblase2.dragonslair.utilities.GeneralUtilities;

public class TriggerEditDialog extends ValidatingPrompt
{
	private Trigger t;
	private final String[] options = new String[] { "add eventid", "delete eventid", "type", "add option", "delete option", "edit option" };
	
	@Override
	public String getPromptText(ConversationContext arg0)
	{
		if(arg0.getSessionData("trigger_id") == null)
		{
			return ChatColor.GREEN + "Please enter the trigger id you want to edit:";
		}
		else
		{
			if(arg0.getSessionData("option") == null)
			{
				arg0.getForWhom().sendRawMessage(ChatColor.GREEN + "What do you want to do?");
				return ChatColor.AQUA + "add eventid, delete eventid, type, add option, delete option, edit option";
			}
			else
			{
				String option = (String)arg0.getSessionData("option");
				if(option.equals("add eventid"))
				{
					return ChatColor.GREEN + "Please enter a new event id to add:";
				}
				else if(option.equals("delete eventid"))
				{
					arg0.getForWhom().sendRawMessage("Please enter an event id to remove:");
					StringBuilder sb = new StringBuilder();
					for(Integer id : t.getEventIDs().toArray(new Integer[0]))
					{
						sb.append(id + ", ");
					}
					if(sb.length() >= 2)
						sb.substring(0, sb.length() - 2);
					
					return ChatColor.GREEN + "Current ids: " + ChatColor.WHITE + sb.toString();
				}
				else if(option.equals("type"))
				{
					arg0.getForWhom().sendRawMessage(ChatColor.GREEN + "Please enter a new type of the trigger:");
					StringBuilder sb = new StringBuilder();
					for(TriggerType type : TriggerType.values())
					{
						sb.append(type.toString() + ", ");
					}
					if(sb.length() >= 2)
						sb.substring(0, sb.length() - 2);
					
					return ChatColor.AQUA + sb.toString();
				}
				else if(option.equals("add option"))
				{
					if(arg0.getSessionData("trigger_option_type") == null)
					{
						arg0.getForWhom().sendRawMessage(ChatColor.GREEN + "Please enter the type of the option:");
						return ChatColor.AQUA + TriggerTypeOptions.valueOf(t.getType().toString()).getOptionString();
					}
					else
					{
						return ChatColor.GREEN + "Please enter a value for the option:";
					}
				}
				else if(option.equals("delete option"))
				{
					arg0.getForWhom().sendRawMessage(ChatColor.GREEN + "Which option you want to remove?");
					return ChatColor.YELLOW + t.getOptionString();
				}
				else if(option.equals("edit option"))
				{
					if(arg0.getSessionData("trigger_option_type") == null)
					{
						arg0.getForWhom().sendRawMessage(ChatColor.GREEN + "Which option you want to edit?");
						return ChatColor.AQUA + t.getOptionString();
					}
					else
					{
						return ChatColor.GREEN + "Please enter a new value for the option:";
					}
				}
			}
		}
		return null;
	}

	@Override
	protected Prompt acceptValidatedInput(ConversationContext arg0, String arg1)
	{
		if(arg1.equals("cancel"))
		{
			arg0.setSessionData("trigger_type", null);
			arg0.setSessionData("option", null);
			arg0.setSessionData("trigger_option_type", null);
			return new TriggerManageDialog();
		}
		
		if(arg0.getSessionData("trigger_id") == null)
		{
			if(arg1.equals("back"))
				return new TriggerManageDialog();
			
			Integer id = Integer.parseInt(arg1);
			arg0.setSessionData("trigger_id", id);
			this.t = DragonsLairMain.getSettings().getTriggers().get(id);
		}
		else
		{
			if(arg0.getSessionData("option") == null)
			{
				if(arg1.equals("back"))
				{
					arg0.setSessionData("trigger_id", null);
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
				
				String option = (String)arg0.getSessionData("option");
				if(option.equals("add eventid"))
				{
					t.getEventIDs().add(Integer.parseInt(arg1));
					t.save();
					arg0.setSessionData("trigger_id", null);
					arg0.setSessionData("option", null);
					return new TriggerManageDialog();
				}
				else if(option.equals("delete eventid"))
				{
					t.getEventIDs().remove((Integer)Integer.parseInt(arg1));
					t.save();
					arg0.setSessionData("trigger_id", null);
					arg0.setSessionData("option", null);
					return new TriggerManageDialog();
				}
				else if(option.equals("type"))
				{
					t.setType(TriggerType.valueOf(arg1));
					t.save();
					arg0.setSessionData("trigger_id", null);
					arg0.setSessionData("option", null);
					return new TriggerManageDialog();
				}
				else if(option.equals("add option"))
				{
					if(arg0.getSessionData("trigger_option_type") == null)
					{
						arg0.setSessionData("trigger_option_type", arg1);
					}
					else
					{
						this.t.setOption((String)arg0.getSessionData("trigger_option_type"), arg1);
						GeneralUtilities.recalculateOptions(t);
						t.save();
						arg0.setSessionData("trigger_id", null);
						arg0.setSessionData("option", null);
						arg0.setSessionData("trigger_option_type", null);
						return new TriggerManageDialog();
					}
				}
				else if(option.equals("delete option"))
				{
					if(TriggerTypeOptions.byType(this.t.getType()).isRequired(arg1))
					{
						arg0.getForWhom().sendRawMessage(ChatColor.RED + "You can't remove a required option.");
						arg0.setSessionData("option", null);
						return this;
					}
					
					t.removeOption(arg1);
					t.save();
					arg0.setSessionData("trigger_id", null);
					arg0.setSessionData("option", null);
					return new TriggerManageDialog();
				}
				else if(option.equals("edit option"))
				{
					if(arg0.getSessionData("trigger_option_type") == null)
					{
						arg0.setSessionData("trigger_option_type", arg1);
					}
					else
					{
						t.setOption((String)arg0.getSessionData("trigger_option_type"), arg1);
						GeneralUtilities.recalculateOptions(t);
						t.save();
						arg0.setSessionData("trigger_id", null);
						arg0.setSessionData("option", null);
						arg0.setSessionData("trigger_option_type", null);
						return new TriggerManageDialog();
					}
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
		
		if(arg0.getSessionData("trigger_id") == null)
		{
			try
			{
				Integer id = Integer.parseInt(arg1);
				if(!DragonsLairMain.getSettings().getTriggers().containsKey(id))
				{
					arg0.getForWhom().sendRawMessage(ChatColor.RED + "A trigger with that id doesn't exist.");
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
		else
		{
			if(arg0.getSessionData("option") == null)
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
				String option = (String)arg0.getSessionData("option");
				if(option.equals("add eventid"))
				{
					try
					{
						Integer eid = Integer.parseInt(arg1);
						if(!DragonsLairMain.getSettings().getEvents().containsKey(eid))
						{
							arg0.getForWhom().sendRawMessage(ChatColor.RED + "An event with that id doesn't exist.");
							return false;
						}
						
						if(t.getEventIDs().contains(eid))
						{
							arg0.getForWhom().sendRawMessage(ChatColor.RED + "The trigger already contains this event id.");
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
				else if(option.equals("delete eventid"))
				{
					try
					{
						Integer eid = Integer.parseInt(arg1);				
						if(!t.getEventIDs().contains(eid))
						{
							arg0.getForWhom().sendRawMessage(ChatColor.RED + "The trigger doesn't contain this event id.");
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
				else if(option.equals("type"))
				{
					for(TriggerType type : TriggerType.values())
					{
						if(type.toString().equals(arg1.toUpperCase().replace(" ", "_")))
							return true;
					}
					return false;
				}
				else if(option.equals("add option"))
				{
					if(arg0.getSessionData("trigger_option_type") == null)
					{
						TriggerTypeOptions typeOptions = TriggerTypeOptions.valueOf(t.getType().toString());
						if(typeOptions.hasOption(arg1))
							return true;
						
						arg0.getForWhom().sendRawMessage(ChatColor.RED + "The option type is not available for the trigger type.");
						return false;
					}
					else
					{
						String option_type = (String)arg0.getSessionData("trigger_option_type");
						return GeneralUtilities.isValidOptionInput(arg0, arg1, option_type);
					}
				}
				else if(option.equals("delete option"))
				{
					if(t.getOption(arg1) == null)
					{
						arg0.getForWhom().sendRawMessage(ChatColor.RED + "The trigger doesn't contain such an option.");
						return false;
					}
					
					return true;
				}
				else if(option.equals("edit option"))
				{
					if(arg0.getSessionData("trigger_option_type") == null)
					{
						if(t.getOption(arg1) == null)
						{
							arg0.getForWhom().sendRawMessage(ChatColor.RED + "The trigger doesn't contain such an option.");
							return false;
						}
						
						return true;
					}
					else
					{
						String option_type = (String)arg0.getSessionData("trigger_option_type");
						return GeneralUtilities.isValidOptionInput(arg0, arg1, option_type);
					}
				}
			}
		}
		return false;
	}
}
