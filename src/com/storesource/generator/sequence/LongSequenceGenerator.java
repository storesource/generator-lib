package com.storesource.generator.sequence;

import java.net.NetworkInterface;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Enumeration;

/**
 *
 *  Using Twitter snowflake & Instagram inspired pattern to create a 64 bit sequence.
 *  See <a href="https://github.com/twitter/snowflake/tree/snowflake-2010">Twitter snowflake</a>
 *  See <a href="https://instagram-engineering.com/sharding-ids-at-instagram-1cf5a71e5a5c">Sharding & IDs at Instagram</a>
 *
 *  LONG_SEQUENCE (64 bit Long)  = EPOCH( 42 bits ) + NODE_ID( 10 bits ) + SEQUENCE( 12 bits )
 *
 *  EPOCH    => 42 bits represent total ms from STORESOURCE_EPOCH defined below
 *  NODE_ID  => 10 bits representing an identifier created using system’s MAC address. 10 bits => max 1024 nodes
 *  SEQUENCE => 12 bits that represent an auto-incrementing sequence from a local counter per machine. 12 bits => max value = 4095
 *
 */
public enum LongSequenceGenerator {

    INSTANCE; // singleton instance

    private static final int TOTAL_BITS = 64;
    private static final int EPOCH_BITS = 42;
    private static final int NODE_ID_BITS = 10;
    private static final int SEQUENCE_BITS = 12;
    private static final String BYTE_TO_HEX_CODE = "%02X";
    private static final int MAX_NODE_ID = (int)(Math.pow(2, NODE_ID_BITS) - 1);
    private static final int MAX_SEQUENCE = (int)(Math.pow(2, SEQUENCE_BITS) - 1);
    private static final String INVALID_SYSTEM_CLOCK_MESSAGE = "Invalid System Clock!";

    // storesource Custom Epoch (December 31, 1992 8:52:00 PM GMT)
    private static final long STORESOURCE_EPOCH = 725835120000L;

    private final int nodeId;

    private volatile long lastTimestamp = -1L;
    private volatile long sequence = 0L;

    LongSequenceGenerator() {
        this.nodeId = createNodeId();
    }


    public synchronized long nextId() {
        long currentTimestamp = timestamp();

        if(currentTimestamp < lastTimestamp) {
            throw new IllegalStateException(INVALID_SYSTEM_CLOCK_MESSAGE);
        }

        if (currentTimestamp == lastTimestamp) {
            sequence = (sequence + 1) & MAX_SEQUENCE;
            if(sequence == 0) {
                // Sequence Exhausted
                currentTimestamp = blockTillNextMilliSecond(currentTimestamp);
            }
        } else {
            // reset sequence to start with zero for the next millisecond
            sequence = 0;
        }

        lastTimestamp = currentTimestamp;

        long id = currentTimestamp << (TOTAL_BITS - EPOCH_BITS);
        id |= (nodeId << (TOTAL_BITS - EPOCH_BITS - NODE_ID_BITS));
        id |= sequence;
        return id;
    }

    private int createNodeId() {
        int nodeId;
        try {
            StringBuilder sb = new StringBuilder();
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = networkInterfaces.nextElement();
                byte[] mac = networkInterface.getHardwareAddress();
                if (mac != null) {
                    for(int i = 0; i < mac.length; i++) {
                        //converting to hex code
                        sb.append(String.format(BYTE_TO_HEX_CODE, mac[i]));
                    }
                }
            }
            nodeId = sb.toString().hashCode();
        } catch (Exception ex) {
            nodeId = (new SecureRandom().nextInt());
        }
        nodeId = nodeId & MAX_NODE_ID;
        return nodeId;
    }


    // Get current timestamp in milliseconds, adjust for the custom epoch.
    private static long timestamp() {
        return Instant.now().toEpochMilli() - STORESOURCE_EPOCH;
    }

    private long blockTillNextMilliSecond(long currentTimestamp) {
        while (currentTimestamp == lastTimestamp) {
            currentTimestamp = timestamp();
        }
        return currentTimestamp;
    }

}