package com.github.zachdeibert.jlinq.installer.conversions.classes;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import jlinq.IEnumerable;
import jlinq.Jlinq;

public class StringConverter extends AbstractClassConverter {
	@Override
	public void visit(final int version, final int access, final String name, final String signature,
			final String superName, final String[] interfaces) {
		super.visit(version, access, name, signature, superName,
				Jlinq.from(interfaces).concat(Jlinq.from(new String[] { IEnumerable.class.getName() })).toArray());
		final MethodVisitor method = visitMethod(Opcodes.ACC_PUBLIC, "iterator", "Ljava/lang/Iterator;",
				"Ljava/lang/Iterator<Ljava/lang/Byte;>;", null);
		method.visitCode();
		method.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/String", "this", "Ljava/lang/String;");
		method.visitMethodInsn(Opcodes.INVOKESTATIC, "jlinq/Jlinq", "from", "(Ljava/lang/String;)Ljlinq/IEnumerable",
				false);
		method.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "jlinq/IEnumerable", "iterator", "Ljava/io/Iterator", false);
		method.visitInsn(Opcodes.RETURN);
		method.visitEnd();
	}
}
