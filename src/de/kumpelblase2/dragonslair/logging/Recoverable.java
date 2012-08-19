package de.kumpelblase2.dragonslair.logging;

import java.util.Map;
import org.bukkit.Location;

public interface Recoverable
{
	public void recover();
	public void save();
	public void remove();
	public void setNew();
	public LogType getType();
	public Map<String, String> getOldData();
	public void setOldData(Map<String, String> inOld);
	public Map<String, String> getNewData();
	public void setNewData(Map<String, String> inNew);
	public boolean isNegotiation(Recoverable inEntry);
	public Location getLocation();
}
