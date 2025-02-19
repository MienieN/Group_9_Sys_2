package main.java.zenit.util;

public class Tuple<T1, T2> {
	private T1 first;
	private T2 second;
	
	public Tuple() {}
	
	public Tuple(T1 fst, T2 snd) {
		this();
		set(fst, snd);
	}
	
	public void set(T1 fst, T2 snd) {
		this.first = fst;
		this.second = snd;
	}
	
	public T1 fst() {
		return first;
	}
	
	public T2 snd() {
		return second;
	}
	
	@Override
	public String toString() {
		return String.format("(%s, %s", first, second);
	}
}
