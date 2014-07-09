package com.felixpageau.roboboat.mission2014.utils;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class NMEATest {
    @Test
    public void testNMEAFormatter() {
        assertEquals("$RXPNC,A,0*26\r\n", NMEAUtils.formatNMEAmessage("RXPNC,A,0"));
    }
}
