package org.agnitas.util;

public class Triple<T1, T2, T3> {
	private T1 value1;
	private T2 value2;
	private T3 value3;
	
	public Triple(T1 value1, T2 value2, T3 value3) {
		this.value1 = value1;
		this.value2 = value2;
		this.value3 = value3;
	}
	
	public T1 getFirst() {
		return value1;
	}
	
	public T2 getSecond() {
		return value2;
	}
	
	public T3 getThird() {
		return value3;
	}
}
