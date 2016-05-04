package jlinq.lambdas;

@FunctionalInterface
public interface Action3<T1, T2, T3> {
	public void run(T1 o1, T2 o2, T3 o3);
}
