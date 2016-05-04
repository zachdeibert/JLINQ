package jlinq;

import java.util.Iterator;

import jlinq.lambdas.Action3;
import jlinq.lambdas.Func;

public class LazyEnumerable<T> implements IEnumerable<T> {
	private final Func<Action3<Pointer<T>, Pointer<Boolean>, Pointer<Boolean>>> select;

	@Override
	public Iterator<T> iterator() {
		return new LazyEnumerator<T>(select.run());
	}

	public LazyEnumerable(final Func<Action3<Pointer<T>, Pointer<Boolean>, Pointer<Boolean>>> select) {
		this.select = select;
	}
}
