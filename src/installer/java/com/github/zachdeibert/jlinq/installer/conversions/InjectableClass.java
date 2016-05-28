package com.github.zachdeibert.jlinq.installer.conversions;

import com.github.zachdeibert.jlinq.installer.conversions.classes.AbstractClassConverter;

public class InjectableClass<T> {
	private final AbstractClassConverter converter;
	private final Class<? extends T> cls;

	public AbstractClassConverter getConverter() {
		return converter;
	}

	public Class<? extends T> getTarget() {
		return cls;
	}

	public InjectableClass(final Class<T> cls, final AbstractClassConverter converter) {
		this.converter = converter;
		this.cls = cls;
	}
}
