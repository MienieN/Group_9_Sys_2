package main.java.zenit.util;

public class Tuple<T1, T2> {
	private T1 fst;
	private T2 snd;
	
	public Tuple() {}
	
	public Tuple(T1 fst, T2 snd) {
		this();
		set(fst, snd);
	}
	
	public void set(T1 fst, T2 snd) {
		this.fst = fst;
		this.snd = snd;
	}
	
	public T1 fst() {
		return fst;
	}
	
	public T2 snd() {
		return snd;
	}
	
	@Override
	public String toString() {
		return String.format("(%s, %s", fst, snd);
	}
}
