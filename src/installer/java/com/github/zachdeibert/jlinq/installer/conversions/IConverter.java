package com.github.zachdeibert.jlinq.installer.conversions;

import com.github.zachdeibert.jlinq.installer.Arguments;

@FunctionalInterface
public interface IConverter {
	public void convert(Arguments args);
}
