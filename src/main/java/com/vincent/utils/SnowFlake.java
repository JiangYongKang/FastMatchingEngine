package com.vincent.utils;

public class SnowFlake {

    private final static long DEFAULT_DATA_CENTER_ID = 0;
    private final static long DEFAULT_MACHINE_ID = 0;

    private final static long START_STAMP = 1480166465631L;

    private final static long SEQUENCE_BIT = 12;   // 序列号占用的位数
    private final static long MACHINE_BIT = 5;     // 机器标识占用的位数
    private final static long DATA_CENTER_BIT = 5; // 数据中心占用的位数

    private final static long MAX_DATA_CENTER_NUM = ~(-1L << DATA_CENTER_BIT);
    private final static long MAX_MACHINE_NUM = ~(-1L << MACHINE_BIT);
    private final static long MAX_SEQUENCE = ~(-1L << SEQUENCE_BIT);

    private final static long MACHINE_LEFT = SEQUENCE_BIT;
    private final static long DATA_CENTER_LEFT = SEQUENCE_BIT + MACHINE_BIT;
    private final static long TIMESTAMP_LEFT = DATA_CENTER_LEFT + DATA_CENTER_BIT;

    private final long dataCenterId;  // 数据中心
    private final long machineId;     // 机器标识
    private long sequence = 0L;       // 序列号
    private long lastTimestamp = -1L; // 上一次时间戳

    public SnowFlake() {
        this(DEFAULT_DATA_CENTER_ID, DEFAULT_MACHINE_ID);
    }

    public SnowFlake(long dataCenterId, long machineId) {
        if (dataCenterId > MAX_DATA_CENTER_NUM || dataCenterId < 0) {
            throw new IllegalArgumentException("The dataCenterId can't be greater than MAX_DATA_CENTER_NUM or less than 0");
        }
        if (machineId > MAX_MACHINE_NUM || machineId < 0) {
            throw new IllegalArgumentException("The machineId can't be greater than MAX_MACHINE_NUM or less than 0");
        }
        this.dataCenterId = dataCenterId;
        this.machineId = machineId;
    }

    public synchronized Long nextId() {
        long currTimestamp = getNewTimestamp();
        if (currTimestamp < lastTimestamp) {
            throw new RuntimeException("Clock moved backwards. Refusing to generate id");
        }
        if (currTimestamp == lastTimestamp) {
            sequence = (sequence + 1) & MAX_SEQUENCE;
            if (sequence == 0L) {
                currTimestamp = getNextMill();
            }
        } else {
            sequence = 0L;
        }
        lastTimestamp = currTimestamp;
        return (currTimestamp - START_STAMP) << TIMESTAMP_LEFT
                | dataCenterId << DATA_CENTER_LEFT
                | machineId << MACHINE_LEFT
                | sequence;
    }

    private long getNextMill() {
        long mill = getNewTimestamp();
        while (mill <= lastTimestamp) {
            mill = getNewTimestamp();
        }
        return mill;
    }

    private long getNewTimestamp() {
        return System.currentTimeMillis();
    }
}
