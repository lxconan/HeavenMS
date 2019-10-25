package net.server;

import config.YamlConfig;

import java.util.Calendar;
import java.util.concurrent.atomic.AtomicLong;

public class ServerTimer {
    private static final ServerTimer instance = new ServerTimer();
    public static ServerTimer getInstance() {
        return instance;
    }

    private final AtomicLong currentTime = new AtomicLong(0);
    private long serverCurrentTime = 0;
    private long uptime = System.currentTimeMillis();

    private ServerTimer() {}

    public void updateCurrentTime() {
        serverCurrentTime = currentTime.addAndGet(YamlConfig.config.server.UPDATE_INTERVAL);
    }

    public long forceUpdateCurrentTime() {
        long timeNow = System.currentTimeMillis();
        serverCurrentTime = timeNow;
        currentTime.set(timeNow);

        return timeNow;
    }

    public long getCurrentTime() {  // returns a slightly delayed time value, under frequency of UPDATE_INTERVAL
        return serverCurrentTime;
    }

    public long getUptime() {
        return uptime;
    }

    public int getCurrentTimestamp() {
        return (int) (getCurrentTime() - getUptime());
    }

    public static long getTimeLeftForNextDay() {
        Calendar nextDay = Calendar.getInstance();
        nextDay.add(Calendar.DAY_OF_MONTH, 1);
        nextDay.set(Calendar.HOUR_OF_DAY, 0);
        nextDay.set(Calendar.MINUTE, 0);
        nextDay.set(Calendar.SECOND, 0);

        return Math.max(0, nextDay.getTimeInMillis() - System.currentTimeMillis());
    }

    public static long getTimeLeftForNextHour() {
        Calendar nextHour = Calendar.getInstance();
        nextHour.add(Calendar.HOUR, 1);
        nextHour.set(Calendar.MINUTE, 0);
        nextHour.set(Calendar.SECOND, 0);

        return Math.max(0, nextHour.getTimeInMillis() - System.currentTimeMillis());
    }
}
