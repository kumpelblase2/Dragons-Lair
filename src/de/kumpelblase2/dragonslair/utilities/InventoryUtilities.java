package de.kumpelblase2.dragonslair.utilities;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import de.kumpelblase2.dragonslair.DragonsLairMain;

public final class InventoryUtilities
{
	public static boolean isInventoryEmpty(Player p, boolean checkArmor)
	{
		ItemStack[] items = p.getInventory().getContents();
		for(ItemStack item : items)
		{
			if(item != null)
			{
				if(item.getType() != Material.AIR)
					return false;
			}
		}
		
		if(checkArmor)
		{
			items = p.getInventory().getArmorContents();
			for(ItemStack item : items)
			{
				if(item != null)
				{
					if(item.getType() != Material.AIR)
						return false;
				}
			}
		}
		return true;
	}
	
	public static boolean removeItem(Player p, ItemStack toRemove)
    {
		ItemStack[] inv = p.getInventory().getContents();
		for(int i = 0; i < inv.length; i++)
		{
			if(inv[i] == null)
				continue;
			
			if(inv[i].getType() == toRemove.getType())
			{
				if(inv[i].getAmount() > toRemove.getAmount())
				{
					inv[i].setAmount(inv[i].getAmount() - toRemove.getAmount());
					toRemove.setAmount(0);
					break;
				}
				else
				{
					int oldAmount = inv[i].getAmount();
					inv[i] = null;
					if(oldAmount == toRemove.getAmount())
					{
						toRemove.setAmount(0);
						break;
					}
					else
						toRemove.setAmount(toRemove.getAmount() - oldAmount);					
				}
			}
		}
		
		if(toRemove.getAmount() > 0)
			return false;
		else
		{
			p.getInventory().setContents(inv);
			return true;
		}
    }
	
	public static String inventoryToString(Player p)
	{
		if(isInventoryEmpty(p, false))
			return "";
		
		ItemStack[] contents = p.getInventory().getContents();
		return itemsToString(contents);
	}
	
	public static String armorToString(Player p)
	{
		ItemStack[] armor = p.getInventory().getArmorContents();
		return itemsToString(armor);
	}
	
	public static ItemStack[] stringToItems(String inv)
	{
		ItemStack[] items;
		String[] itemSplitt = inv.split(";");
		items = new ItemStack[itemSplitt.length];
		for(int i = 0; i < itemSplitt.length; i++)
		{
			String[] itemdata = itemSplitt[i].split(":");
			try
			{
				if(itemdata.length >= 3)
				{
					items[i] = new ItemStack(Integer.parseInt(itemdata[0]), Integer.parseInt(itemdata[1]), Short.parseShort(itemdata[2]));
					if(itemdata.length > 3)
					{
						for(int i2 = 0; i2 <= (itemdata.length - 3); i2 += 2)
						{
							Enchantment e = Enchantment.getByName(itemdata[3 + i2]);
							items[i].addEnchantment(e, Integer.parseInt(itemdata[4 + i2]));
						}
					}
				}
				else if(itemdata.length == 2)
					items[i] = new ItemStack(Integer.parseInt(itemdata[0]), Integer.parseInt(itemdata[1]));
				else if(itemdata.length == 1)
					items[i] = new ItemStack(Integer.parseInt(itemdata[0]));
			}
			catch(Exception e)
			{
				DragonsLairMain.Log.warning("Unable to parse item: " + itemSplitt[i]);
				continue;
			}
		}
		return items;
	}
	
	public static String itemsToString(ItemStack[] items)
	{
		if(items.length == 0)
			return "";
		
		StringBuilder itemString = new StringBuilder();
		for(int i = 0; i < items.length; i++)
		{
			if(items[i] == null || items[i].getType() == Material.AIR || (items[i].getType() == Material.MAP && items[i].getEnchantments().get(Enchantment.ARROW_INFINITE) != null))
				itemString.append("0:0:0");
			else
			{
				itemString.append(items[i].getTypeId() + ":" + items[i].getAmount() + ":" + items[i].getDurability());
				if(items[i].getEnchantments().size() > 0)
				{
					for(Entry<Enchantment, Integer> enchantment : items[i].getEnchantments().entrySet())
					{
						itemString.append(":" + enchantment.getKey().getName() + ":" + enchantment.getValue());
					}
				}
			}
			
			if(i != items.length - 1)
				itemString.append(";");
		}

		return itemString.toString();
	}
	
	public static String itemToString(ItemStack item)
	{
		if(item == null)
			return "0:0:0";
		return item.getTypeId() + ":" + item.getAmount() + ":" + item.getDurability();
	}
	
	public static ItemStack stringToItem(String item)
	{
		String[] split = item.split(":");
		return new ItemStack(Material.getMaterial(Integer.parseInt(split[0])), Integer.parseInt(split[1]), Short.parseShort(split[1]));
	}
	
	public static int getDamageFromItem(ItemStack item)
	{
		switch(item.getType())
		{
			case WOOD_SWORD:
			case GOLD_SWORD:
			case STONE_AXE:
			case IRON_PICKAXE:
			case DIAMOND_SPADE:
				return 4;
			case STONE_SWORD:
			case IRON_AXE:
			case DIAMOND_PICKAXE:
				return 5;
			case IRON_SWORD:
			case DIAMOND_AXE:
				return 6;
			case DIAMOND_SWORD:
				return 7;
			case WOOD_AXE:
			case GOLD_AXE:
			case STONE_PICKAXE:
			case IRON_SPADE:
				return 3;
			case WOOD_PICKAXE:
			case GOLD_PICKAXE:
			case STONE_SPADE:
				return 2;
			default:
				return 1;
		}
	}
	
	public static Map<String, String> getChangedStates(Inventory inContents, ItemStack inNew)
	{
		Map<String, String> changes = new HashMap<String, String>();
		
		if(inContents.contains(inNew.getType()))
		{
			HashMap<Integer, ? extends ItemStack> items = inContents.all(inNew.getType());
			for(int i = 0; i < inContents.getSize(); i++)
			{
				if(!items.containsKey((Integer)i))
					continue;
				
				ItemStack item = items.get((Integer)i);
				if(item.getMaxStackSize() - item.getAmount() > 1)
				{
					int restMax = item.getMaxStackSize() - item.getAmount();
					if(inNew.getAmount() > restMax)
					{
						int toAdd = restMax - inNew.getAmount();
						inNew.setAmount(inNew.getAmount() - toAdd);
						ItemStack it = new ItemStack(item.getType(), item.getAmount() + toAdd, item.getDurability());
						changes.put("slot" + i, InventoryUtilities.itemToString(it));
					}
					else
					{
						item.setAmount(item.getAmount() + inNew.getAmount());
						ItemStack it = new ItemStack(item.getType(), item.getAmount() + inNew.getAmount(), item.getDurability());
						changes.put("slot" + i, InventoryUtilities.itemToString(it));
						inNew.setAmount(0);
						break;
					}
				}
			}
			
			if(inNew.getAmount() > 0)
			{
				if(inContents.firstEmpty() != -1)
					changes.put("slot" + inContents.firstEmpty(), InventoryUtilities.itemToString(inNew));
			}
		}
		else
		{
			if(inContents.firstEmpty() != -1)
				changes.put("slot" + inContents.firstEmpty(), InventoryUtilities.itemToString(inNew));
		}
		
		return changes;
	}
}
