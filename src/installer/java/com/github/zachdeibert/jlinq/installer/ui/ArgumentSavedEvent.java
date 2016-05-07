package com.github.zachdeibert.jlinq.installer.ui;

import java.awt.AWTEvent;

import com.github.zachdeibert.jlinq.installer.Arguments;

public class ArgumentSavedEvent extends AWTEvent {
	private static final long serialVersionUID = 2L;
	public static final int ARGUMENT_SAVED_EVENT = 0x100000;
	private final Arguments args;

	public final Arguments getArgs() {
		return args;
	}

	public ArgumentSavedEvent(final Object source, final int id, final Arguments args) {
		super(source, id);
		this.args = args;
	}
}
