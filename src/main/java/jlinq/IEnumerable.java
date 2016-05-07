package jlinq;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import jlinq.lambdas.Func1;
import jlinq.lambdas.Func2;

@FunctionalInterface
public interface IEnumerable<T> extends Iterable<T> {
	public default <U> int compare(final U a, final U b) {
		return a == b ? 0 : 1;
	}

	public default T aggregate(final Func2<T, T, T> func) {
		T working = null;
		boolean first = true;
		for ( final T o : this ) {
			if ( first ) {
				working = o;
				first = false;
			} else {
				working = func.run(working, o);
			}
		}
		return working;
	}

	public default <TSeed> TSeed aggregate(TSeed seed, final Func2<TSeed, T, TSeed> func) {
		for ( final T o : this ) {
			seed = func.run(seed, o);
		}
		return seed;
	}

	public default <TSeed, TResult> TResult aggregate(final TSeed seed, final Func2<TSeed, T, TSeed> func,
			final Func1<TSeed, TResult> result) {
		return result.run(aggregate(seed, func));
	}

	public default boolean all(final Func1<T, Boolean> predicate) {
		for ( final T o : this ) {
			if ( !predicate.run(o) ) {
				return false;
			}
		}
		return true;
	}

	public default boolean any() {
		return iterator().hasNext();
	}

	public default boolean any(final Func1<T, Boolean> predicate) {
		for ( final T o : this ) {
			if ( predicate.run(o) ) {
				return true;
			}
		}
		return false;
	}

	public default IEnumerable<T> asEnumerable() {
		return this;
	}

	public default double average(final Func1<T, Double> selector) {
		double count = 0;
		double sum = 0;
		for ( final double o : select(t -> selector.run(t)) ) {
			sum += o;
			++count;
		}
		return sum / count;
	}

	@SuppressWarnings("unchecked")
	public default <TResult> IEnumerable<TResult> cast() {
		return select(t -> (TResult) t);
	}

	public default IEnumerable<T> concat(final IEnumerable<T> second) {
		return new LazyEnumerable<T>(() -> {
			final Pointer<Boolean> finishedFirst = new Pointer<Boolean>();
			finishedFirst.resolve = false;
			final Iterator<T> f = iterator();
			final Iterator<T> s = second.iterator();
			return (final Pointer<T> res, final Pointer<Boolean> retry, final Pointer<Boolean> end) -> {
				if ( finishedFirst.resolve ) {
					if ( !(end.resolve = !s.hasNext()) ) {
						res.resolve = s.next();
					}
					retry.resolve = false;
				} else if ( !(retry.resolve = !f.hasNext()) ) {
					res.resolve = f.next();
					retry.resolve = false;
				} else {
					finishedFirst.resolve = true;
				}
			};
		});
	}

	public default IEnumerable<T> concat(@SuppressWarnings("unchecked") final IEnumerable<T>... others) {
		IEnumerable<T> res = this;
		for ( final IEnumerable<T> other : others ) {
			res = res.concat(other);
		}
		return res;
	}

	public default IEnumerable<T> concatAll(final IEnumerable<IEnumerable<T>> others) {
		return new LazyEnumerable<T>(() -> {
			final Iterator<IEnumerable<T>> it = others.iterator();
			final Pointer<Iterator<T>> derrived = new Pointer<Iterator<T>>();
			derrived.resolve = empty().iterator();
			return (final Pointer<T> res, final Pointer<Boolean> retry, final Pointer<Boolean> end) -> {
				if ( derrived.resolve.hasNext() ) {
					res.resolve = derrived.resolve.next();
					retry.resolve = false;
					end.resolve = false;
				} else if ( !(end.resolve = !it.hasNext()) ) {
					derrived.resolve = it.next().iterator();
					retry.resolve = true;
				}
			};
		});
	}

	public default boolean contains(final T value) {
		return contains(value, (o1, o2) -> compare(o1, o2));
	}

	public default boolean contains(final T value, final Comparator<T> comparer) {
		for ( final T o : this ) {
			if ( comparer.compare(o, value) == 0 ) {
				return true;
			}
		}
		return false;
	}

	public default int count() {
		return count(t -> true);
	}

	public default int count(final Func1<T, Boolean> predicate) {
		return aggregate(0, (num, t) -> num + (predicate.run(t) ? 1 : 0));
	}

	public default IEnumerable<T> defaultIfEmpty() {
		return defaultIfEmpty(null);
	}

	public default IEnumerable<T> defaultIfEmpty(final T defaultValue) {
		return new LazyEnumerable<T>(() -> {
			final Pointer<Boolean> first = new Pointer<Boolean>();
			first.resolve = true;
			final Iterator<T> it = iterator();
			return (final Pointer<T> res, final Pointer<Boolean> retry, final Pointer<Boolean> end) -> {
				if ( first.resolve ) {
					if ( it.hasNext() ) {
						res.resolve = defaultValue;
					} else {
						res.resolve = it.next();
					}
					end.resolve = false;
					first.resolve = false;
				} else if ( !(end.resolve = !it.hasNext()) ) {
					res.resolve = it.next();
					end.resolve = false;
				} else {
					end.resolve = true;
				}
				retry.resolve = false;
			};
		});
	}

	public default IEnumerable<T> distinct() {
		return distinct((o1, o2) -> compare(o1, o2));
	}

	public default IEnumerable<T> distinct(final Comparator<T> comparator) {
		return new LazyEnumerable<T>(() -> {
			final Iterator<T> it = iterator();
			final Set<T> set = new TreeSet<T>(comparator);
			return (final Pointer<T> res, final Pointer<Boolean> retry, final Pointer<Boolean> end) -> {
				if ( !(end.resolve = !it.hasNext()) ) {
					retry.resolve = !set.add(res.resolve = it.next());
				}
			};
		});
	}

	public default T elementAt(final int index) {
		return where((t, i) -> i == index).first();
	}

	public default T elementAtOrDefault(final int index) {
		return where((t, i) -> i == index).firstOrDefault();
	}

	public default IEnumerable<T> empty() {
		return new LazyEnumerable<T>(() -> (final Pointer<T> res, final Pointer<Boolean> retry,
				final Pointer<Boolean> end) -> end.resolve = true);
	}

	public default IEnumerable<T> except(final IEnumerable<T> other) {
		return except(other, (o1, o2) -> compare(o1, o2));
	}

	public default IEnumerable<T> except(final IEnumerable<T> other, final Comparator<T> comparator) {
		return new LazyEnumerable<T>(() -> {
			final Set<T> exclude = other.toSet();
			final Iterator<T> it = iterator();
			return (final Pointer<T> res, final Pointer<Boolean> retry, final Pointer<Boolean> end) -> {
				if ( !(end.resolve = !it.hasNext()) ) {
					retry.resolve = exclude.contains(res.resolve = it.next());
				}
			};
		});
	}

	public default T first() {
		return iterator().next();
	}

	public default T first(final Func1<T, Boolean> predicate) {
		for ( final T o : this ) {
			if ( predicate.run(o) ) {
				return o;
			}
		}
		throw new IndexOutOfBoundsException("Predicate not matched in enumerator");
	}

	public default T firstOrDefault() {
		final Iterator<T> it = iterator();
		if ( it.hasNext() ) {
			return it.next();
		} else {
			return null;
		}
	}

	public default T firstOrDefault(final Func1<T, Boolean> predicate) {
		for ( final T o : this ) {
			if ( predicate.run(o) ) {
				return o;
			}
		}
		return null;
	}

	public default <TKey> IEnumerable<Grouping<TKey, T>> groupBy(final Func1<T, TKey> selector) {
		return (IEnumerable<Grouping<TKey, T>>) groupBy(selector, (t, e) -> new Grouping<TKey, T>(e, t),
				(o1, o2) -> compare(o1, o2));
	}

	public default <TKey> IEnumerable<Grouping<TKey, T>> groupBy(final Func1<T, TKey> selector,
			final Comparator<TKey> comparator) {
		return groupBy(selector, t -> t, comparator);
	}

	public default <TKey, TElement> IEnumerable<Grouping<TKey, TElement>> groupBy(final Func1<T, TKey> keySelector,
			final Func1<T, TElement> elementSelector) {
		return groupBy(keySelector, (t, e) -> new Grouping<TKey, TElement>(e.select(o -> elementSelector.run(o)), t),
				(o1, o2) -> compare(o1, o2));
	}

	public default <TKey, TElement> IEnumerable<Grouping<TKey, TElement>> groupBy(final Func1<T, TKey> keySelector,
			final Func1<T, TElement> elementSelector, final Comparator<TKey> comparator) {
		return groupBy(keySelector, (t, e) -> new Grouping<TKey, TElement>(e.select(o -> elementSelector.run(o)), t),
				comparator);
	}

	public default <TKey, TResult> IEnumerable<TResult> groupBy(final Func1<T, TKey> keySelector,
			final Func2<TKey, IEnumerable<T>, TResult> resultSelector) {
		return groupBy(keySelector, resultSelector, (o1, o2) -> compare(o1, o2));
	}

	public default <TKey, TResult> IEnumerable<TResult> groupBy(final Func1<T, TKey> keySelector,
			final Func2<TKey, IEnumerable<T>, TResult> resultSelector, final Comparator<TKey> comparator) {
		return groupBy(keySelector, t -> t, resultSelector, comparator);
	}

	public default <TKey, TElement, TResult> IEnumerable<TResult> groupBy(final Func1<T, TKey> keySelector,
			final Func1<T, TElement> elementSelector,
			final Func2<TKey, IEnumerable<TElement>, TResult> resultSelector) {
		return groupBy(keySelector, elementSelector, resultSelector, (o1, o2) -> compare(o1, o2));
	}

	public default <TKey, TElement, TResult> IEnumerable<TResult> groupBy(final Func1<T, TKey> keySelector,
			final Func1<T, TElement> elementSelector, final Func2<TKey, IEnumerable<TElement>, TResult> resultSelector,
			final Comparator<TKey> comparator) {
		final Map<TKey, List<TElement>> map = new TreeMap<TKey, List<TElement>>(comparator);
		for ( final T o : this ) {
			final TKey key = keySelector.run(o);
			final TElement element = elementSelector.run(o);
			final List<TElement> list;
			if ( map.containsKey(key) ) {
				list = map.get(key);
			} else {
				map.put(key, (list = new ArrayList<TElement>()));
			}
			list.add(element);
		}
		return Jlinq.from(map.keySet()).select(k -> resultSelector.run(k, Jlinq.from(map.get(k))));
	}

	public default <TInner, TKey, TResult> IEnumerable<TResult> groupJoin(final IEnumerable<TInner> inner,
			final Func1<T, TKey> outerKeySelector, final Func1<TInner, TKey> innerKeySelector,
			final Func2<T, IEnumerable<TInner>, TResult> resultSelector) {
		return groupJoin(inner, outerKeySelector, innerKeySelector, resultSelector, (o1, o2) -> compare(o1, o2));
	}

	public default <TInner, TKey, TResult> IEnumerable<TResult> groupJoin(final IEnumerable<TInner> inner,
			final Func1<T, TKey> outerKeySelector, final Func1<TInner, TKey> innerKeySelector,
			final Func2<T, IEnumerable<TInner>, TResult> resultSelector, final Comparator<TKey> comparator) {
		return join(inner.groupBy(innerKeySelector, comparator), outerKeySelector, g -> g.key,
				(t, g) -> resultSelector.run(t, g));
	}

	public default IEnumerable<T> intersect(final IEnumerable<T> other) {
		return intersect(other, (o1, o2) -> compare(o1, o2));
	}

	public default IEnumerable<T> intersect(final IEnumerable<T> other, final Comparator<T> comparator) {
		return new LazyEnumerable<T>(() -> {
			final Set<T> exclude = other.toSet();
			final Iterator<T> it = iterator();
			return (final Pointer<T> res, final Pointer<Boolean> retry, final Pointer<Boolean> end) -> {
				if ( !(end.resolve = !it.hasNext()) ) {
					retry.resolve = !exclude.contains(res.resolve = it.next());
				}
			};
		});
	}

	public default <TInner, TKey, TResult> IEnumerable<TResult> join(final IEnumerable<TInner> inner,
			final Func1<T, TKey> outerKeySelector, final Func1<TInner, TKey> innerKeySelector,
			final Func2<T, TInner, TResult> resultSelector) {
		return join(inner, outerKeySelector, innerKeySelector, resultSelector, (o1, o2) -> compare(o1, o2));
	}

	public default <TInner, TKey, TResult> IEnumerable<TResult> join(final IEnumerable<TInner> inner,
			final Func1<T, TKey> outerKeySelector, final Func1<TInner, TKey> innerKeySelector,
			final Func2<T, TInner, TResult> resultSelector, final Comparator<TKey> comparator) {
		final Map<TKey, T> outers = new TreeMap<TKey, T>(comparator);
		final Map<TKey, TInner> inners = new TreeMap<TKey, TInner>(comparator);
		for ( final T o : this ) {
			final TKey key = outerKeySelector.run(o);
			outers.put(key, o);
		}
		for ( final TInner o : inner ) {
			final TKey key = innerKeySelector.run(o);
			inners.put(key, o);
		}
		return Jlinq.from(outers.keySet()).select(key -> resultSelector.run(outers.get(key), inners.get(key)));
	}

	public default T last() {
		return last(t -> true);
	}

	public default T last(final Func1<T, Boolean> predicate) {
		T last = null;
		boolean hasLast = false;
		for ( final T o : this ) {
			if ( predicate.run(o) ) {
				last = o;
				hasLast = true;
			}
		}
		if ( hasLast ) {
			return last;
		} else {
			throw new IndexOutOfBoundsException("Unable to find match to predicate");
		}
	}

	public default T lastOrDefault() {
		return lastOrDefault(t -> true);
	}

	public default T lastOrDefault(final Func1<T, Boolean> predicate) {
		T last = null;
		for ( final T o : this ) {
			if ( predicate.run(o) ) {
				last = o;
			}
		}
		return last;
	}

	public default long longCount() {
		return longCount(t -> true);
	}

	public default long longCount(final Func1<T, Boolean> predicate) {
		return aggregate(0L, (num, t) -> num + (predicate.run(t) ? 1 : 0));
	}

	public default T max() {
		return aggregate(null, (a, b) -> compare(a, b) > 0 ? b : a);
	}

	public default double max(final Func1<T, Double> selector) {
		return aggregate(Double.MIN_VALUE, (a, b) -> Math.max(a, selector.run(b)));
	}

	public default T min() {
		return aggregate(null, (a, b) -> compare(a, b) > 0 ? a : b);
	}

	public default double min(final Func1<T, Double> selector) {
		return aggregate(Double.MAX_VALUE, (a, b) -> Math.min(a, selector.run(b)));
	}

	@SuppressWarnings("unchecked")
	public default <TResult> IEnumerable<TResult> ofType() {
		return new LazyEnumerable<TResult>(() -> {
			final Iterator<T> it = iterator();
			return (final Pointer<TResult> res, final Pointer<Boolean> retry, final Pointer<Boolean> end) -> {
				if ( !(end.resolve = !it.hasNext()) ) {
					try {
						res.resolve = (TResult) it.next();
						retry.resolve = false;
					} catch ( final ClassCastException ex ) {
						retry.resolve = true;
					}
				}
			};
		});
	}

	public default <TKey> IEnumerable<T> orderBy(final Func1<T, TKey> selector) {
		return orderBy(selector, (o1, o2) -> compare(o1, o2));
	}

	public default <TKey> IEnumerable<T> orderBy(final Func1<T, TKey> selector, final Comparator<TKey> comparator) {
		final List<T> list = new ArrayList<T>();
		all(t -> list.add(t));
		Collections.sort(list, (o1, o2) -> comparator.compare(selector.run(o1), selector.run(o2)));
		return Jlinq.from(list);
	}

	public default <TKey> IEnumerable<T> orderByDescending(final Func1<T, TKey> selector) {
		return orderByDescending(selector, (o1, o2) -> compare(o1, o2));
	}

	public default <TKey> IEnumerable<T> orderByDescending(final Func1<T, TKey> selector,
			final Comparator<TKey> comparator) {
		return orderBy(selector, (o1, o2) -> comparator.compare(o2, o1));
	}

	public default IEnumerable<T> reverse() {
		final List<T> list = new ArrayList<T>();
		all(t -> list.add(t));
		Collections.reverse(list);
		return Jlinq.from(list);
	}

	public default <TResult> IEnumerable<TResult> select(final Func1<T, TResult> selector) {
		return new LazyEnumerable<TResult>(() -> {
			final Iterator<T> it = iterator();
			return (final Pointer<TResult> res, final Pointer<Boolean> retry, final Pointer<Boolean> end) -> {
				if ( !(end.resolve = !it.hasNext()) ) {
					res.resolve = selector.run(it.next());
				}
				retry.resolve = false;
			};
		});
	}

	public default <TResult> IEnumerable<TResult> select(final Func2<T, Integer, TResult> selector) {
		return new LazyEnumerable<TResult>(() -> {
			final Iterator<T> it = iterator();
			final Pointer<Integer> i = new Pointer<Integer>();
			i.resolve = -1;
			return (final Pointer<TResult> res, final Pointer<Boolean> retry, final Pointer<Boolean> end) -> {
				if ( !(end.resolve = !it.hasNext()) ) {
					res.resolve = selector.run(it.next(), ++i.resolve);
				}
				retry.resolve = false;
			};
		});
	}

	public default <TResult> IEnumerable<TResult> selectMany(final Func1<T, IEnumerable<TResult>> selector) {
		return selectMany(selector, (t, c) -> c);
	}

	public default <TResult> IEnumerable<TResult> selectMany(final Func2<T, Integer, IEnumerable<TResult>> selector) {
		return selectMany(selector, (t, c) -> c);
	}

	@SuppressWarnings("unchecked")
	public default <TCollection, TResult> IEnumerable<TResult> selectMany(
			final Func1<T, IEnumerable<TCollection>> collectionSelector,
			final Func2<T, TCollection, TResult> resultSelector) {
		return (IEnumerable<TResult>) concatAll(
				select(t -> (IEnumerable<T>) collectionSelector.run(t).select(c -> resultSelector.run(t, c))));
	}

	@SuppressWarnings("unchecked")
	public default <TCollection, TResult> IEnumerable<TResult> selectMany(
			final Func2<T, Integer, IEnumerable<TCollection>> collectionSelector,
			final Func2<T, TCollection, TResult> resultSelector) {
		return (IEnumerable<TResult>) concatAll(
				select((t, i) -> (IEnumerable<T>) collectionSelector.run(t, i).select(c -> resultSelector.run(t, c))));
	}

	public default boolean sequenceEquals(final IEnumerable<T> second) {
		return sequenceEquals(second, (o1, o2) -> compare(o1, o2));
	}

	public default boolean sequenceEquals(final IEnumerable<T> second, final Comparator<T> comparator) {
		final Iterator<T> f = iterator();
		final Iterator<T> s = second.iterator();
		while ( f.hasNext() && s.hasNext() ) {
			if ( comparator.compare(f.next(), s.next()) != 0 ) {
				return false;
			}
		}
		return f.hasNext() && s.hasNext();
	}

	public default T single() {
		return single(t -> true);
	}

	public default T single(final Func1<T, Boolean> predicate) {
		T t = null;
		boolean first = true;
		for ( final T o : this ) {
			if ( predicate.run(o) ) {
				if ( first ) {
					t = o;
					first = false;
				} else {
					throw new IndexOutOfBoundsException("More than one matching element found");
				}
			}
		}
		if ( first ) {
			throw new IndexOutOfBoundsException("No matching elements found");
		}
		return t;
	}

	public default T singleOrDefault() {
		return singleOrDefault(t -> true);
	}

	public default T singleOrDefault(final Func1<T, Boolean> predicate) {
		T t = null;
		boolean first = true;
		for ( final T o : this ) {
			if ( predicate.run(o) ) {
				if ( first ) {
					t = o;
					first = false;
				} else {
					throw new IndexOutOfBoundsException("More than one matching element found");
				}
			}
		}
		return t;
	}

	public default IEnumerable<T> skip(final int count) {
		return skipWhile((t, i) -> i < count);
	}

	public default IEnumerable<T> skipWhile(final Func1<T, Boolean> predicate) {
		return skipWhile((t, i) -> predicate.run(t));
	}

	public default IEnumerable<T> skipWhile(final Func2<T, Integer, Boolean> predicate) {
		return () -> {
			final Iterator<T> it = iterator();
			for ( int i = 0; predicate.run(it.next(), i); ++i )
				;
			return it;
		};
	}

	public default double sum(final Func1<T, Double> selector) {
		return aggregate(0., (a, b) -> a + selector.run(b));
	}

	public default IEnumerable<T> take(final int count) {
		return takeWhile((t, i) -> i < count);
	}

	public default IEnumerable<T> takeWhile(final Func1<T, Boolean> predicate) {
		return new LazyEnumerable<T>(() -> {
			final Iterator<T> it = iterator();
			return (final Pointer<T> res, final Pointer<Boolean> retry, final Pointer<Boolean> end) -> {
				if ( !(end.resolve = !it.hasNext()) ) {
					res.resolve = it.next();
					end.resolve = !predicate.run(res.resolve);
				}
			};
		});
	}

	public default IEnumerable<T> takeWhile(final Func2<T, Integer, Boolean> predicate) {
		return new LazyEnumerable<T>(() -> {
			final Iterator<T> it = iterator();
			final Pointer<Integer> i = new Pointer<Integer>();
			i.resolve = -1;
			return (final Pointer<T> res, final Pointer<Boolean> retry, final Pointer<Boolean> end) -> {
				if ( !(end.resolve = !it.hasNext()) ) {
					res.resolve = it.next();
					end.resolve = !predicate.run(res.resolve, ++i.resolve);
				}
			};
		});
	}

	@SuppressWarnings("unchecked")
	public default T[] toArray() {
		return (T[]) toList().toArray();
	}

	public default List<T> toList() {
		final List<T> list = new ArrayList<T>();
		for ( final T o : this ) {
			list.add(o);
		}
		return list;
	}

	public default <TKey> Map<TKey, T> toMap(final Func1<T, TKey> selector) {
		return toMap(selector, (o1, o2) -> compare(o1, o2));
	}

	public default <TKey> Map<TKey, T> toMap(final Func1<T, TKey> selector, final Comparator<TKey> comparator) {
		return toMap(selector, t -> t, comparator);
	}

	public default <TKey, TElement> Map<TKey, TElement> toMap(final Func1<T, TKey> keySelector,
			final Func1<T, TElement> elementSelector) {
		return toMap(keySelector, elementSelector, (o1, o2) -> compare(o1, o2));
	}

	public default <TKey, TElement> Map<TKey, TElement> toMap(final Func1<T, TKey> keySelector,
			final Func1<T, TElement> elementSelector, final Comparator<TKey> comparator) {
		final Map<TKey, TElement> map = new TreeMap<TKey, TElement>(comparator);
		for ( final T o : this ) {
			map.put(keySelector.run(o), elementSelector.run(o));
		}
		return map;
	}

	public default Set<T> toSet() {
		final Set<T> set = new HashSet<T>();
		for ( final T o : this ) {
			set.add(o);
		}
		return set;
	}

	public default IEnumerable<T> union(final IEnumerable<T> second) {
		return union(second, (o1, o2) -> compare(o1, o2));
	}

	public default IEnumerable<T> union(final IEnumerable<T> second, final Comparator<T> comparator) {
		return concat(second.except(this, comparator));
	}

	public default IEnumerable<T> where(final Func1<T, Boolean> predicate) {
		return new LazyEnumerable<T>(() -> {
			final Iterator<T> it = iterator();
			return (final Pointer<T> res, final Pointer<Boolean> retry, final Pointer<Boolean> end) -> {
				if ( !(end.resolve = !it.hasNext()) ) {
					retry.resolve = !predicate.run(res.resolve = it.next());
				}
			};
		});
	}

	public default IEnumerable<T> where(final Func2<T, Integer, Boolean> predicate) {
		return new LazyEnumerable<T>(() -> {
			final Iterator<T> it = iterator();
			final Pointer<Integer> i = new Pointer<Integer>();
			i.resolve = -1;
			return (final Pointer<T> res, final Pointer<Boolean> retry, final Pointer<Boolean> end) -> {
				if ( !(end.resolve = !it.hasNext()) ) {
					retry.resolve = !predicate.run(res.resolve = it.next(), ++i.resolve);
				}
			};
		});
	}

	public default <TResult, TSecond> IEnumerable<TResult> zip(final IEnumerable<TSecond> second,
			final Func2<T, TSecond, TResult> selector) {
		return new LazyEnumerable<TResult>(() -> {
			final Iterator<T> f = iterator();
			final Iterator<TSecond> s = second.iterator();
			return (final Pointer<TResult> res, final Pointer<Boolean> retry, final Pointer<Boolean> end) -> {
				if ( !(end.resolve = !(f.hasNext() && s.hasNext())) ) {
					res.resolve = selector.run(f.next(), s.next());
				}
			};
		});
	}
}
