package net.server;

import java.util.HashSet;
import java.util.Set;

public class FlyStateService {
    private static final FlyStateService instance = new FlyStateService();
    public static FlyStateService getInstance() {return instance;}

    private final Set<Integer> activeFly = new HashSet<>();

    public void changeFly(Integer accountid, boolean canFly) {
        if (canFly) {
            activeFly.add(accountid);
        } else {
            activeFly.remove(accountid);
        }
    }

    public boolean canFly(Integer accountid) {
        return activeFly.contains(accountid);
    }
}
