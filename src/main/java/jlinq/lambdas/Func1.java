package jlinq.lambdas;

@FunctionalInterface
public interface Func1<T1, TRes> {
	public TRes run(T1 o1);
}
