package de.lv1871.dms.tester.test.function;

import java.util.function.Function;
import java.util.function.Predicate;

public class LambdaExtension {

	public static <T, V> Predicate<T> notNull(Function<T, V> supplier) {
		return (value) -> notNull().test(supplier.apply(value));
	}

	public static <T> Predicate<T> notNull() {
		return (value) -> value != null;
	}
}
