package com.github.zachdeibert.jlinq.installer;

import java.util.function.Supplier;

import com.github.zachdeibert.jlinq.installer.conversions.BootClasspathGenerator;
import com.github.zachdeibert.jlinq.installer.conversions.IConverter;
import com.github.zachdeibert.jlinq.installer.conversions.JdkConverter;

public enum ConversionType {
	JdkInstall(() -> new JdkConverter()), BootGeneration(() -> new BootClasspathGenerator()), JarInlining(() -> null);

	private final Supplier<IConverter> factory;

	public IConverter construct() {
		return factory.get();
	}

	private ConversionType(final Supplier<IConverter> factory) {
		this.factory = factory;
	}
}
