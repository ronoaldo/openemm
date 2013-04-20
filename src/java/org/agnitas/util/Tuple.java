package org.agnitas.util;

public class Tuple<T1, T2> {
	private T1 value1;
	private T2 value2;
	
	public Tuple(T1 value1, T2 value2) {
		this.value1 = value1;
		this.value2 = value2;
	}
	
	public T1 getFirst() {
		return value1;
	}
	
	public T2 getSecond() {
		return value2;
	}
}
