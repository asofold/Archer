package me.asofold.bpl.archer.config.properties;

import java.util.HashSet;
import java.util.Set;

import me.asofold.bpl.archer.config.compatlayer.CompatConfig;

/**
 * Extend PropertyHolder to reading properties from a config.
 * @author mc_dev
 *
 */
public class ConfigPropertyHolder extends PropertyHolder{
	
	/**
	 * Read properties from the config by their original name (no double reading no aliases).<br>
	 * TODO: Some failure policy.
	 * @param cfg
	 * @param prefix
	 */
	public void fromConfig(CompatConfig cfg, String prefix){
		Set<String> done = new HashSet<String>(properties.size());
		for (Property property : properties.values()){
			if (done.contains(property.name)) continue;
			String valDef = cfg.getString(prefix + property.name);
			if (valDef != null){
				property.fromString(valDef);
			}
			done.add(property.name);
		}
	}
	
}
