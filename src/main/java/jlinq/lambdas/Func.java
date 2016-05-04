package jlinq.lambdas;

@FunctionalInterface
public interface Func<TRes> {
	public TRes run();
}
