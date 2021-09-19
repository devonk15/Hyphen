package dev.quantumfusion.hyphen.scan.poly.classes;

import dev.quantumfusion.hyphen.annotation.SerSubclasses;
import dev.quantumfusion.hyphen.annotation.Serialize;

public class RecursiveC<T> extends C1<T> {
	@Serialize
	@SerSubclasses({C1.class, RecursiveC.class})
	public C1<T> foo;

	public RecursiveC(T t, C1<T> foo) {
		super(t);
		this.foo = foo;
	}
}

