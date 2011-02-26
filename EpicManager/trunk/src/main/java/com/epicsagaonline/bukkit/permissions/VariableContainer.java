package com.epicsagaonline.bukkit.permissions;

public interface VariableContainer {
	
	/**
	 * Set the variable
	 */
	void set(String variable, Object val);
	
	/**
	 * return the variable as the type
	 * @throws NotFoundError if variable doesn't exist
	 */
	Object getObject(String variable);
	/**
	 * return the variable as the type
	 * @return null if not found, otherwise the value
	 * @throws ClassCastException if variable exists, but isn't castable to type
	 */
	String getString(String variable);
	/**
	 * return the variable as the type
	 * @return null if not found, otherwise the value
	 * @throws ClassCastException if variable exists, but isn't castable to type
	 */
	Boolean getBoolean(String variable);
	/**
	 * return the variable as the type
	 * @return null if not found, otherwise the value
	 * @throws ClassCastException if variable exists, but isn't castable to type
	 */
	Integer getInteger(String variable);
	/**
	 * return the variable as the type
	 * @return null if not found, otherwise the value
	 * @throws ClassCastException if variable exists, but isn't castable to type
	 */
	Double getDouble(String variable);
}
