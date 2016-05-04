package jlinq.lambdas;

@FunctionalInterface
public interface Func4<T1, T2, T3, T4, TRes> {
	public TRes run(T1 o1, T2 o2, T3 o3, T4 o4);
}
