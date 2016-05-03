package com.felixpageau.roboboat.mission.utils;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.felixpageau.roboboat.mission.utils.NMEAUtils;

public class NMEATest {
    @Test
    public void testNMEAFormatter() {
        assertEquals("$RXPNC,A,0*26\r\n", NMEAUtils.formatNMEAmessage("RXPNC,A,0"));
    }
}
