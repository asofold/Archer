package me.asofold.bpl.archer.config.properties;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;


/**
 * Holding multiple properties.
 * @author mc_dev
 *
 */
public class PropertyHolder {
	/**
	 * Map them by name.
	 */
	protected final Map<String, Property> properties = new LinkedHashMap<String, Property>();
	
	public Property setProperty(String name, double min, double max, double value){
		return setProperty(new Property(name, min, max, value));
	}
	
	public Property setProperty(Property property){
		properties.put(property.name, property);
		return property;
	}
	
	public Property getProperty(String name){
		return properties.get(name);
	}
	
	public double getValue(String name){
		return properties.get(name).value;
	}
	
	public double getDouble(String name){
		return properties.get(name).value;
	}
	
	public long getLong(String name){
		return (long) properties.get(name).value;
	}
	
	public int getInt(String name){
		return (int) properties.get(name).value;
	}
	
	public boolean getBoolean(String name){
		return properties.get(name).nonzero();
	}
	
	public boolean hasProperty(String name){
		return properties.containsKey(name);
	}
	
	public void setAliases(){
		String[] from = new String[]{"-", "_"};
		String[] to = new String[]{"-", "_", ""};
		Set<String> candidates = new LinkedHashSet<String>(40);
		for (final Property property : new ArrayList<Property>(properties.values())){
			String key = property.name;
			for (String find : from){
				for (String replace : to){
					String newKey = key.replace(find, replace);
					candidates.add(newKey);
					candidates.add(newKey.toLowerCase());
					candidates.add(newKey.toUpperCase());
				}
			}
			candidates.add(key.toLowerCase());
			candidates.add(key.toUpperCase());
			for (String newKey : candidates){
				if (!newKey.equals(key) && !properties.containsKey(newKey)){
					properties.put(newKey, property);
				}
			}
			candidates.clear();
		}
	}
	
}
