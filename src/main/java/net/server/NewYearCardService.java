package net.server;

import client.newyear.NewYearCardRecord;

import java.util.HashMap;
import java.util.Map;

public class NewYearCardService {
    private static final NewYearCardService instance = new NewYearCardService();
    public static NewYearCardService getInstance() {return instance;}

    private final Map<Integer, NewYearCardRecord> newYears = new HashMap<>();

    public void setNewYearCard(NewYearCardRecord nyc) {
        newYears.put(nyc.getId(), nyc);
    }

    public NewYearCardRecord getNewYearCard(int cardid) {
        return newYears.get(cardid);
    }

    public void removeNewYearCard(int cardId) {
        newYears.remove(cardId);
    }
}
