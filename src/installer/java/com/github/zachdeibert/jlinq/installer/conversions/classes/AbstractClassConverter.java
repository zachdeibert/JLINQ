package com.github.zachdeibert.jlinq.installer.conversions.classes;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;

public abstract class AbstractClassConverter extends ClassVisitor {
	public void convert(final InputStream in, final OutputStream out) throws IOException {
		final ClassWriter writer = new ClassWriter(0);
		final ClassReader reader = new ClassReader(in);
		cv = writer;
		reader.accept(this, 0);
		out.write(writer.toByteArray());
	}

	protected AbstractClassConverter() {
		super(Opcodes.ASM4);
	}
}
