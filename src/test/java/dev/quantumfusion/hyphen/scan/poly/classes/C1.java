package dev.quantumfusion.hyphen.scan.poly.classes;

import dev.quantumfusion.hyphen.scan.annotations.Data;

import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static dev.quantumfusion.hyphen.util.TestSupplierUtil.cross;

public class C1<A> extends C0 {
	@Data
	public A a;

	public C1(A a) {
		this.a = a;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || this.getClass() != o.getClass()) return false;

		C1<?> c1 = (C1<?>) o;

		return Objects.equals(this.a, c1.a);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(this.a);
	}

	public static <A> Supplier<? extends Stream<? extends C1<A>>> generateC1(Supplier<? extends Stream<? extends A>> aProvider) {
		return cross(aProvider, C1::new);
	}

	@Override
	public String toString() {
		assert this.getClass() == C1.class;
		return "C1{" +
				"a=" + this.a +
				'}';
	}
}
