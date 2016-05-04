package jlinq.lambdas;

@FunctionalInterface
public interface Func2<T1, T2, TRes> {
	public TRes run(T1 o1, T2 o2);
}
