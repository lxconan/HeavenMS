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

        if (params.length != 0) {
            player.yellowMessage("Syntax: !scrollshop");
            return;
        }

        MapleShopFactory.getInstance().getShop(1405).sendShop(client);
    }
}
