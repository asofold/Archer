package me.asofold.bpl.archer.config.properties;

/**
 * Auxiliary class for in place changing of properties.
 * @author mc_dev
 *
 */
public class Property {
	public final String name;
	public double value = 0.0;
	public final double min, max;
	
	public Property(String name, double min, double max){
		this(name, min, max, 0.0);
	}
	
	public Property(String name, double min, double max, double value){
		this.name = name;
		this.min = min;
		this.max = max;
		set(value);
	}
	
	public void set(double value){
		this.value = Math.min(this.max, Math.max(this.min, value));
	}
	
	/**
	 * 
	 * @param value Also negative values.
	 */
	public void add(double value){
		this.value = Math.min(this.max, Math.max(this.min, this.value + value));
	}
	
	/**
	 * Increment by 1.
	 */
	public void increment(){
		this.value = Math.min(this.max, this.value + 1.0);
	}
	
	/**
	 * Decrement by 1.
	 */
	public void decrement(){
		this.value = Math.max(this.min, this.value - 1.0);
	}
	
	public boolean nonzero(){
		return value != 0.0;
	}
	
	public long getLong(){
		return (long) value;
	}
	
	public int getInt(){
		return (int) value;
	}

	public void fromString(String valDef) {
		valDef = valDef.trim().toLowerCase();
		if (valDef.equals("true")){
			set(1); // max ?
		}
		else if (valDef.equals("false")){
			set(0); // min ?
		}
		try{
			set(Double.parseDouble(valDef));
		}
		catch(Throwable t){
			// TODO: Failure policy.
		}
	}
	
	// (Read from config, write to config.)
	// Read from command !
	// Output methods (info commands etc.)
}
