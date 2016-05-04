package jlinq.lambdas;

@FunctionalInterface
public interface Action2<T1, T2> {
	public void run(T1 o1, T2 o2);
}
