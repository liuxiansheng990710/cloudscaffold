package com.example.spring.boot.redisson.enums;

/**
 * <p>
 * 锁类型枚举
 * <p>
 *
 * @author : 21
 * @since : 2023/10/24 16:08
 */

public enum LockType {

    REENTRANT(1, "reentrantLock", "可重入锁"),
    FAIR(2, "fairLock", "公平锁"),
    READ(3, "readLock", "读锁"),
    WRITE(4, "writeLock", "写锁");

    private final int value;
    private final String name;
    private final String description;

    LockType(int value, String name, String description) {
        this.value = value;
        this.name = name;
        this.description = description;
    }

    public int getValue() {
        return value;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    /**
     * 获取锁类型，默认可重入锁
     *
     * @param value
     * @return
     */
    public LockType getLockType(int value) {
        LockType[] values = LockType.values();
        for (LockType lockType : values) {
            if (lockType.getValue() == value) {
                return lockType;
            }
        }
        return REENTRANT;
    }
}
