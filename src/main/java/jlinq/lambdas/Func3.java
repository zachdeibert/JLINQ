package jlinq.lambdas;

@FunctionalInterface
public interface Func3<T1, T2, T3, TRes> {
	public TRes run(T1 o1, T2 o2, T3 o3);
}
