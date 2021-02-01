package org.palermo.circuit.engine;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class NonameTest {

    @Test
    void test() {
        /*
        assertThrows(RuntimeException.class, () -> NoName.getParentPorts(0, 0));

        assertThat(NoName.getParentPorts(1, 0)).isEqualTo(new long[] {});

        assertThat(NoName.getParentPorts(1, 1)).isEqualTo(new long[] {0, 0});

        assertThat(NoName.getParentPorts(1, 2)).isEqualTo(new long[] {0, 1});
        assertThat(NoName.getParentPorts(1, 3)).isEqualTo(new long[] {1, 1});

        assertThat(NoName.getParentPorts(1, 4)).isEqualTo(new long[] {0, 2});
        assertThat(NoName.getParentPorts(1, 5)).isEqualTo(new long[] {1, 2});
        assertThat(NoName.getParentPorts(1, 6)).isEqualTo(new long[] {2, 2});

        assertThat(NoName.getParentPorts(1, 7)).isEqualTo(new long[] {0, 3});
        assertThat(NoName.getParentPorts(1, 8)).isEqualTo(new long[] {1, 3});
        assertThat(NoName.getParentPorts(1, 9)).isEqualTo(new long[] {2, 3});
        assertThat(NoName.getParentPorts(1, 10)).isEqualTo(new long[] {3, 3});

        assertThat(NoName.getParentPorts(1, 11)).isEqualTo(new long[] {0, 4});
        assertThat(NoName.getParentPorts(1, 12)).isEqualTo(new long[] {1, 4});
        assertThat(NoName.getParentPorts(1, 13)).isEqualTo(new long[] {2, 4});
        assertThat(NoName.getParentPorts(1, 14)).isEqualTo(new long[] {3, 4});
        assertThat(NoName.getParentPorts(1, 15)).isEqualTo(new long[] {4, 4});


        assertThat(NoName.getParentPorts(2, 0)).isEqualTo(new long[] {});
        assertThat(NoName.getParentPorts(2, 1)).isEqualTo(new long[] {});

        assertThat(NoName.getParentPorts(2, 2)).isEqualTo(new long[] {0, 0});

        assertThat(NoName.getParentPorts(2, 3)).isEqualTo(new long[] {0, 1});
        assertThat(NoName.getParentPorts(2, 4)).isEqualTo(new long[] {1, 1});

        assertThat(NoName.getParentPorts(2, 5)).isEqualTo(new long[] {0, 2});
        assertThat(NoName.getParentPorts(2, 6)).isEqualTo(new long[] {1, 2});
        assertThat(NoName.getParentPorts(2, 7)).isEqualTo(new long[] {2, 2});

        assertThat(NoName.getParentPorts(2, 8)).isEqualTo(new long[] {0, 3});
        assertThat(NoName.getParentPorts(2, 9)).isEqualTo(new long[] {1, 3});
        assertThat(NoName.getParentPorts(2, 10)).isEqualTo(new long[] {2, 3});
        assertThat(NoName.getParentPorts(2, 11)).isEqualTo(new long[] {3, 3});

        assertThat(NoName.getParentPorts(3, 0)).isEqualTo(new long[] {});
        assertThat(NoName.getParentPorts(3, 1)).isEqualTo(new long[] {});
        assertThat(NoName.getParentPorts(3, 2)).isEqualTo(new long[] {});

        assertThat(NoName.getParentPorts(3, 3)).isEqualTo(new long[] {0, 0});

        assertThat(NoName.getParentPorts(3, 4)).isEqualTo(new long[] {0, 1});
        assertThat(NoName.getParentPorts(3, 5)).isEqualTo(new long[] {1, 1});

        assertThat(NoName.getParentPorts(3, 6)).isEqualTo(new long[] {0, 2});
        assertThat(NoName.getParentPorts(3, 7)).isEqualTo(new long[] {1, 2});
        assertThat(NoName.getParentPorts(3, 8)).isEqualTo(new long[] {2, 2});
        */
    }

    @Test
    void generation() {
        /*
        for (int i = 1; i < 50; i++) {
            System.out.println(String.format("%d - %d - %d", i, NoName.getParentPorts(1, i)[0], NoName.getParentPorts(1, i)[1]));
        }
         */
    }
}
