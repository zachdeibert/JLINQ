package jlinq;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

public abstract class Jlinq {
	public static IEnumerable<Byte> from(final byte[] array) {
		return new LazyEnumerable<Byte>(() -> {
			final Pointer<Integer> i = new Pointer<Integer>();
			i.resolve = -1;
			return (final Pointer<Byte> res, final Pointer<Boolean> retry, final Pointer<Boolean> end) -> {
				if ( !(end.resolve = !(i.resolve < array.length - 1)) ) {
					res.resolve = array[++i.resolve];
				}
				retry.resolve = false;
			};
		});
	}

	public static IEnumerable<Short> from(final short[] array) {
		return new LazyEnumerable<Short>(() -> {
			final Pointer<Integer> i = new Pointer<Integer>();
			i.resolve = -1;
			return (final Pointer<Short> res, final Pointer<Boolean> retry, final Pointer<Boolean> end) -> {
				if ( !(end.resolve = !(i.resolve < array.length - 1)) ) {
					res.resolve = array[++i.resolve];
				}
				retry.resolve = false;
			};
		});
	}

	public static IEnumerable<Integer> from(final int[] array) {
		return new LazyEnumerable<Integer>(() -> {
			final Pointer<Integer> i = new Pointer<Integer>();
			i.resolve = -1;
			return (final Pointer<Integer> res, final Pointer<Boolean> retry, final Pointer<Boolean> end) -> {
				if ( !(end.resolve = !(i.resolve < array.length - 1)) ) {
					res.resolve = array[++i.resolve];
				}
				retry.resolve = false;
			};
		});
	}

	public static IEnumerable<Long> from(final long[] array) {
		return new LazyEnumerable<Long>(() -> {
			final Pointer<Integer> i = new Pointer<Integer>();
			i.resolve = -1;
			return (final Pointer<Long> res, final Pointer<Boolean> retry, final Pointer<Boolean> end) -> {
				if ( !(end.resolve = !(i.resolve < array.length - 1)) ) {
					res.resolve = array[++i.resolve];
				}
				retry.resolve = false;
			};
		});
	}

	public static IEnumerable<Float> from(final float[] array) {
		return new LazyEnumerable<Float>(() -> {
			final Pointer<Integer> i = new Pointer<Integer>();
			i.resolve = -1;
			return (final Pointer<Float> res, final Pointer<Boolean> retry, final Pointer<Boolean> end) -> {
				if ( !(end.resolve = !(i.resolve < array.length - 1)) ) {
					res.resolve = array[++i.resolve];
				}
				retry.resolve = false;
			};
		});
	}

	public static IEnumerable<Double> from(final double[] array) {
		return new LazyEnumerable<Double>(() -> {
			final Pointer<Integer> i = new Pointer<Integer>();
			i.resolve = -1;
			return (final Pointer<Double> res, final Pointer<Boolean> retry, final Pointer<Boolean> end) -> {
				if ( !(end.resolve = !(i.resolve < array.length - 1)) ) {
					res.resolve = array[++i.resolve];
				}
				retry.resolve = false;
			};
		});
	}

	public static IEnumerable<Boolean> from(final boolean[] array) {
		return new LazyEnumerable<Boolean>(() -> {
			final Pointer<Integer> i = new Pointer<Integer>();
			i.resolve = -1;
			return (final Pointer<Boolean> res, final Pointer<Boolean> retry, final Pointer<Boolean> end) -> {
				if ( !(end.resolve = !(i.resolve < array.length - 1)) ) {
					res.resolve = array[++i.resolve];
				}
				retry.resolve = false;
			};
		});
	}

	public static IEnumerable<Character> from(final char[] array) {
		return new LazyEnumerable<Character>(() -> {
			final Pointer<Integer> i = new Pointer<Integer>();
			i.resolve = -1;
			return (final Pointer<Character> res, final Pointer<Boolean> retry, final Pointer<Boolean> end) -> {
				if ( !(end.resolve = !(i.resolve < array.length - 1)) ) {
					res.resolve = array[++i.resolve];
				}
				retry.resolve = false;
			};
		});
	}

	public static <T> IEnumerable<T> from(final T[] array) {
		return new LazyEnumerable<T>(() -> {
			final Pointer<Integer> i = new Pointer<Integer>();
			i.resolve = -1;
			return (final Pointer<T> res, final Pointer<Boolean> retry, final Pointer<Boolean> end) -> {
				if ( !(end.resolve = !(i.resolve < array.length - 1)) ) {
					res.resolve = array[++i.resolve];
				}
				retry.resolve = false;
			};
		});
	}

	public static <T> IEnumerable<T> from(final Iterable<T> it) {
		return new LazyEnumerable<T>(() -> {
			final Iterator<T> i = it.iterator();
			return (final Pointer<T> res, final Pointer<Boolean> retry, final Pointer<Boolean> end) -> {
				if ( !(end.resolve = !i.hasNext()) ) {
					res.resolve = i.next();
				}
				retry.resolve = false;
			};
		});
	}

	public static IEnumerable<Character> from(final String str) {
		return from(str.toCharArray());
	}

	public static IEnumerable<Byte> from(final InputStream stream) {
		return new LazyEnumerable<Byte>(
				() -> (final Pointer<Byte> res, final Pointer<Boolean> retry, final Pointer<Boolean> end) -> {
					try {
						int r;
						if ( !(end.resolve = (r = stream.read()) == -1) ) {
							res.resolve = (byte) r;
						}
					} catch ( final IOException ex ) {
						end.resolve = true;
					}
				});
	}
}
