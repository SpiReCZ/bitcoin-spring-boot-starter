package org.tbk.lnurl.simple;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SimpleK1Test {

    @Test
    void fromHexSuccessful() {
        SimpleK1 k1 = SimpleK1.fromHexString("00".repeat(32));
        assertThat(k1.hex(), is("0000000000000000000000000000000000000000000000000000000000000000"));
        assertThat(k1.data().length, is(32));
    }

    @Test
    void fromHexFail() {
        assertThrows(NullPointerException.class, () -> SimpleK1.fromHexString(null));
        assertThrows(IllegalArgumentException.class, () -> SimpleK1.fromHexString(""));

        IllegalArgumentException e1 = assertThrows(IllegalArgumentException.class, () -> SimpleK1.fromHexString("00"));
        IllegalArgumentException e2 = assertThrows(IllegalArgumentException.class, () -> SimpleK1.fromHexString("00".repeat(33)));
        assertThat(e1.getMessage(), is("data must be an array of size 32"));
        assertThat(e2.getMessage(), is(e1.getMessage()));
    }
}