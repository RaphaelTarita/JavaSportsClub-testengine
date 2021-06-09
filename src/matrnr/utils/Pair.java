package matrnr.utils;

public class Pair<A, B> {
    private A a;
    private B b;

    public Pair(A a, B b) {
        this.a = a;
        this.b = b;
    }

    public A first() {
        return a;
    }

    public B second() {
        return b;
    }

    public void first(A a) {
        this.a = a;
    }

    public void second(B b) {
        this.b = b;
    }
}
