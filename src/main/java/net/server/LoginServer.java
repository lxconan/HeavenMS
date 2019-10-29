package net.server;

import net.server.audit.locks.MonitoredLockType;
import net.server.audit.locks.MonitoredReentrantReadWriteLock;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class LoginServer {
    private final ReentrantReadWriteLock lgnLock = new MonitoredReentrantReadWriteLock(MonitoredLockType.SERVER_LOGIN, true);
    public final ReentrantReadWriteLock.ReadLock lgnRLock = lgnLock.readLock();
    public final ReentrantReadWriteLock.WriteLock lgnWLock = lgnLock.writeLock();
    public final Map<Integer, Integer> worldChars = new HashMap<>();
}
