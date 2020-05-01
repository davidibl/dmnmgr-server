package de.lv1871.oss.tester.test.function;

import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;

@FunctionalInterface
public interface ExtendedBiPredicate<T, F> extends BiPredicate<T, F> {

    default Function<T, Predicate<F>> curry() {
		return a -> b -> this.test(a, b);
	}

	default Predicate<F> curryWith(T value) {
		return curry().apply(value);
	}
}