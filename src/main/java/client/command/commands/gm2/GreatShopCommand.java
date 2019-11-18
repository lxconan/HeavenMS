package client.command.commands.gm2;

import client.MapleCharacter;
import client.MapleClient;
import client.command.Command;
import server.MapleShopFactory;

import java.util.HashMap;
import java.util.Map;

public class GreatShopCommand extends Command {
    private static final Map<String, Integer> shops = new HashMap<>();

    static {
        shops.put("warrior", 1400);
        shops.put("bowman", 1401);
        shops.put("wizard", 1402);
        shops.put("thief", 1403);
        shops.put("pirate", 1404);
    }

    public GreatShopCommand() {
        setDescription("");
    }

    @Override
    public void execute(MapleClient client, String[] params) {
        MapleCharacter player = client.getPlayer();
        if (params.length != 1) {
            player.yellowMessage("Syntax: !greatshop <warrior|bowman|wizard|thief|pirate>");
            return;
        }

        final String job = params[0];
        if (!shops.containsKey(job)) {
            player.yellowMessage("Syntax: !greatshop <warrior|bowman|wizard|thief|pirate>");
            return;
        }

        final Integer shopId = shops.get(job);
        MapleShopFactory.getInstance().getShop(shopId).sendShop(client);
    }
}
