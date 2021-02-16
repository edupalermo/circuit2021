package org.palermo.circuit.collection;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

public class MixedOrderedSetTest {


    @Test
    public void test() throws InterruptedException {
        MixedOrderedSet set = new MixedOrderedSet();

        List<Thread> list = new ArrayList<>();

        list.addAll(run(() -> { set.add(null);}, 2));
        list.addAll(run(() -> { set.select(1);}, 4));
        list.addAll(run(() -> { set.add(null);}, 8));
        list.addAll(run(() -> { set.contains(null);}, 16));
        list.addAll(run(() -> { set.height();}, 32));
        for (Thread t : list) {
            t.join();
        }
    }

    private List<Thread> run(Runnable r, int times) {
        List<Thread> list = new ArrayList<>();
        for (int i = 0; i < times; i++) {
            Thread t = new Thread(r);
            t.start();
            list.add(t);
        }
        return list;
    }
}
