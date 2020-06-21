package com.corundumstudio.socketio.handler;

import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;

public class ClientHeadTest {
    ClientHead clientHead;


    /**
     * Purpose: Test disconnected default
     * Input: void
     * Expected:
     *          Return False
     *          default
     *          disconnect -> True
     *
     */
    @Test
    public void isConnected() {
        assertFalse(clientHead.isConnected());
    }

    /**
     * Purpose: Test disconnected
     * Input: void
     * Expected:
     *          Return False
     *          disconnect -> True
     *
     */
    @Test
    public void onChannelDisconnect() {
        clientHead.disconnect();
        assertFalse(clientHead.isConnected());
    }

}