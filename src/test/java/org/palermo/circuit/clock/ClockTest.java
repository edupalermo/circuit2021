package org.palermo.circuit.clock;

import org.junit.jupiter.api.Test;

class ClockTest {

    @Test
    void test() throws InterruptedException {

        Clock clock = Clock.start();
        Thread.sleep(1);
        System.out.println(clock.getDelta());

        clock = Clock.start();
        Thread.sleep(1000);
        System.out.println(clock.getDelta());

        clock = Clock.start();
        Thread.sleep(60 * 1000);
        System.out.println(clock.getDelta());

    }

}
