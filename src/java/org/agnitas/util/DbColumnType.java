package org.agnitas.util;

public class DbColumnType {
	private String typeName;
	private int characterLength; // only for VARCHAR and VARCHAR2 types
	private int numericPrecision; // only for numeric types
	private int numericScale; // only for numeric types
	private boolean nullable;
	
	public DbColumnType(String typeName, int characterLength, int numericPrecision, int numericScale, boolean nullable) {
		this.typeName = typeName;
		this.characterLength = characterLength;
		this.numericPrecision = numericPrecision;
		this.numericScale = numericScale;
		this.nullable = nullable;
	}
	
	public String getTypeName() {
		return typeName;
	}
	
	public int getCharacterLength() {
		return characterLength;
	}
	
	public int getNumericPrecision() {
		return numericPrecision;
	}
	
	public int getNumericScale() {
		return numericScale;
	}
	
	public boolean isNullable() {
		return nullable;
	}
}
