package io.katharsis.utils.java;

import java.util.NoSuchElementException;

import com.google.common.base.Objects;

public class Nullable<T> {
	
	private static final Nullable<?> EMPTY = new Nullable<>();
	private static final Nullable<?> NULL = new Nullable<>(null);

	private final T value;

	private boolean present;

	private Nullable() {
		value = null;
		present = false;
	}

	private Nullable(T value) {
		this.value = value;
		this.present = true;
	}

	public boolean isPresent() {
		return present;
	}

	public T get() {
		if (!present) {
			throw new NoSuchElementException("No value present");
		}
		return value;
	}

	@SuppressWarnings("unchecked")
	public static <T> Nullable<T> empty() {
		return (Nullable<T>) EMPTY;
	}

	public static <T> Nullable<T> of(T value) {
		return new Nullable<>(value);
	}

	public static <T> Nullable<T> ofNullable(T value) {
		return value == null ? Nullable.<T>empty() : of(value);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (present ? 1231 : 1237);
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Nullable)) {
			return false;
		}
		Nullable<?> other = (Nullable<?>) obj;
		return Objects.equal(present, other.present) && Objects.equal(value, other.value);
	}

	public static <T> Nullable<T> nullValue() {
		return (Nullable<T>) NULL;
	}
}
