package net.server;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class CouponService {
    private final Map<Integer, Integer> couponRates = new HashMap<>(30);
    public final List<Integer> activeCoupons = new LinkedList<>();

    public Map<Integer, Integer> getCouponRates() {
        return couponRates;
    }

    public void loadCouponRates(Connection c) throws SQLException {
        try (
            PreparedStatement ps = c.prepareStatement("SELECT couponid, rate FROM nxcoupons");
            ResultSet rs = ps.executeQuery();
        ) {
            while (rs.next()) {
                int cid = rs.getInt("couponid");
                int rate = rs.getInt("rate");

                couponRates.put(cid, rate);
            }
        }
    }

    public List<Integer> getActiveCoupons() {
        synchronized (activeCoupons) {
            return activeCoupons;
        }
    }
}
