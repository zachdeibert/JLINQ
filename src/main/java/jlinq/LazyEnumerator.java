package jlinq;

import java.util.Iterator;

import jlinq.lambdas.Action3;

public class LazyEnumerator<T> implements Iterator<T> {
	private final Action3<Pointer<T>, Pointer<Boolean>, Pointer<Boolean>> select;
	private T next;
	private T afterNext;
	private boolean hasNext;
	private boolean hasAfterNext;
	private boolean isEnd;
	
	private void calculateIter(final Pointer<T> res, final Pointer<Boolean> end) {
		final Pointer<Boolean> tryAgain = new Pointer<Boolean>();
		tryAgain.resolve = false;
		select.run(res, tryAgain, end);
		if ( tryAgain.resolve ) {
			calculateIter(res, end);
		}
	}

	private T calculateNext(final Pointer<Boolean> end) {
		end.resolve = false;
		final Pointer<T> res = new Pointer<T>();
		calculateIter(res, end);
		return res.resolve;
	}

	@Override
	public boolean hasNext() {
		if ( isEnd ) {
			return false;
		}
		if ( !hasAfterNext ) {
			final Pointer<Boolean> end = new Pointer<Boolean>();
			afterNext = calculateNext(end);
			hasAfterNext = !end.resolve;
			if ( !hasAfterNext ) {
				isEnd = true;
			}
		}
		return hasAfterNext;
	}

	@Override
	public T next() {
		hasNext = hasNext();
		if ( hasNext ) {
			next = afterNext;
			hasAfterNext = false;
			return next;
		} else {
			throw new IndexOutOfBoundsException("End of iterator reached!");
		}
	}
	
	public LazyEnumerator(final Action3<Pointer<T>, Pointer<Boolean>, Pointer<Boolean>> select) {
		this.select = select;
	}
}
