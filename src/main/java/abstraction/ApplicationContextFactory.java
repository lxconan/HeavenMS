package abstraction;

import abstraction.dao.PlayerNpcFieldGateway;

public class ApplicationContextFactory {
    private static ApplicationContext instance;

    static {
        instance = new ApplicationContext();
        instance.register(DataConnectionFactory.class, new DataConnectionFactoryImpl());
        instance.register(PlayerNpcFieldGateway.class, new PlayerNpcFieldGateway(instance));
    }

    public static ApplicationContext getInstance() {
        return instance;
    }
}
