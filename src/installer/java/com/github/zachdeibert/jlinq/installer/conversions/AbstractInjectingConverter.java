package com.github.zachdeibert.jlinq.installer.conversions;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.github.zachdeibert.jlinq.installer.conversions.classes.InputStreamConverter;
import com.github.zachdeibert.jlinq.installer.conversions.classes.IterableConverter;
import com.github.zachdeibert.jlinq.installer.conversions.classes.StringConverter;

import jlinq.IEnumerable;
import jlinq.Jlinq;

abstract class AbstractInjectingConverter implements IConverter {
	@SuppressWarnings("rawtypes")
	public static IEnumerable<InjectableClass<?>> injections = Jlinq.from(
			new InjectableClass<?>[] { new InjectableClass<InputStream>(InputStream.class, new InputStreamConverter()),
					new InjectableClass<Iterable>(Iterable.class, new IterableConverter()),
					new InjectableClass<String>(String.class, new StringConverter()) });
	public static IEnumerable<String> classes = injections.select(c -> getClassname(c));

	private static String getClassname(final InjectableClass<?> cls) {
		return cls.getTarget().getName().replace('.', '/').concat(".class");
	}

	protected void convert(final InjectableClass<?> cls, final InputStream in, final OutputStream out)
			throws IOException {
		cls.getConverter().convert(in, out);
	}

	protected void convert(final String filename, final InputStream in, final OutputStream out) throws IOException {
		convert(injections.first(c -> getClassname(c).equals(filename)), in, out);
	}
}
