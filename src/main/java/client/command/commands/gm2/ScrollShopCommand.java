package client.command.commands.gm2;

import client.MapleCharacter;
import client.MapleClient;
import client.command.Command;
import server.MapleShopFactory;

public class ScrollShopCommand extends Command {

    public ScrollShopCommand() {
        setDescription("");
    }

    @Override
    public void execute(MapleClient client, String[] params) {
        MapleCharacter player = client.getPlayer();

        final String syntaxMessage = "Syntax: !scrollshop <10|60>";
        if (params.length != 1) {
            player.yellowMessage(syntaxMessage);
            return;
        }

        final String shopType = params[0];
        if ("10".equals(shopType) || "60".equals(shopType)) {
            int shopId = "10".equals(shopType) ? 1405 : 1406;
            MapleShopFactory.getInstance().getShop(shopId).sendShop(client);
        } else {
            player.yellowMessage(syntaxMessage);
        }
    }
}
