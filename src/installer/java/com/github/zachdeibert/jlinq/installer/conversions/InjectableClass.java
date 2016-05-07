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

	@SuppressWarnings("unchecked")
	public InjectableClass(final AbstractClassConverter converter) {
		this.converter = converter;
		try {
			this.cls = (Class<? extends T>) Class.forName(getClass().getTypeParameters()[0].getBounds()[0].getTypeName());
		} catch ( final ClassNotFoundException ex ) {
			throw new RuntimeException(ex);
		}
	}
}
