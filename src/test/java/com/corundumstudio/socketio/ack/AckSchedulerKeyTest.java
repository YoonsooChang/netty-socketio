package com.corundumstudio.socketio.ack;

import com.corundumstudio.socketio.scheduler.SchedulerKey;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;

import com.corundumstudio.socketio.scheduler.SchedulerKey.Type;

import java.util.UUID;

public class AckSchedulerKeyTest {
    AckSchedulerKey key;
    UUID uuid;
    long index;

    @BeforeClass
    public void oneTimeSetUp() {
        uuid = UUID.randomUUID();
        index = 148903842;
        key = new AckSchedulerKey(Type.ACK_TIMEOUT, uuid, index);
    }

    /**
     * Purpose: Test getIndex() return value
     * Input: void
     * Expected:
     *          Return SUCCESS
     */
    @Test
    public void getIndex() {
        assertEquals(key.getIndex(), index);
    }

    /**
     * Purpose: Test Object Equal Condition
     * Input: Equals(key2)
     * Expected:
     *          Return False
     *          key = AckSchedulerKey
     *          key2 = SchedulerKey
     *          Not object equal
     */
    @Test
    public void equals() {
        SchedulerKey key2 = new SchedulerKey(Type.UPGRADE_TIMEOUT, 18437930);
        assertFalse(key.equals(key2));
    }

    /**
     * Purpose: Test Index Equal Condition
     * Input: key2 index -> 18437930
     * Expected:
     *          Return False
     *          key index = 148903842
     *          key2 index = 18437930
     *          Not index equal
     */
    @Test
    public void equals1() {
        AckSchedulerKey key2 = new AckSchedulerKey(Type.UPGRADE_TIMEOUT, UUID.randomUUID(), 18437930);
        assertFalse(key.equals(key2));
    }

    /**
     * Purpose: Test Object Equal Condition
     * Input: key -> index, uuid
     * Expected:
     *          Return True
     *          key = key
     *          Equal object
     */
    @Test
    public void equals2() {
        assertTrue(key.equals(key));
    }
}