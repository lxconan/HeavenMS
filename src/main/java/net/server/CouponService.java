package net.server;

import client.MapleCharacter;
import constants.inventory.ItemConstants;
import net.server.world.World;
import tools.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class CouponService {
    private final WorldServer worldServer = WorldServer.getInstance();
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

    public void commitActiveCoupons() {
        for (World world : worldServer.getWorlds()) {
            for (MapleCharacter chr : world.getPlayerStorage().getAllCharacters()) {
                if (!chr.isLoggedin()) continue;
                chr.updateCouponRates();
            }
        }
    }

    public void toggleCoupon(Integer couponId) {
        if (ItemConstants.isRateCoupon(couponId)) {
            synchronized (activeCoupons) {
                if (activeCoupons.contains(couponId)) {
                    activeCoupons.remove(couponId);
                } else {
                    activeCoupons.add(couponId);
                }

                commitActiveCoupons();
            }
        }
    }

    public void updateActiveCoupons() throws SQLException {
        synchronized (activeCoupons) {
            activeCoupons.clear();
            Calendar c = Calendar.getInstance();

            int weekDay = c.get(Calendar.DAY_OF_WEEK);
            int hourDay = c.get(Calendar.HOUR_OF_DAY);

            Connection con = null;
            try {
                con = DatabaseConnection.getConnection();

                int weekdayMask = (1 << weekDay);
                PreparedStatement ps = con.prepareStatement(
                    "SELECT couponid FROM nxcoupons WHERE (activeday & ?) = ? AND starthour <= ? AND endhour > ?");
                ps.setInt(1, weekdayMask);
                ps.setInt(2, weekdayMask);
                ps.setInt(3, hourDay);
                ps.setInt(4, hourDay);

                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    activeCoupons.add(rs.getInt("couponid"));
                }

                rs.close();
                ps.close();

                con.close();
            } catch (SQLException ex) {
                ex.printStackTrace();

                try {
                    if (con != null && !con.isClosed()) {
                        con.close();
                    }
                } catch (SQLException ex2) {
                    ex2.printStackTrace();
                }
            }
        }
    }
}
