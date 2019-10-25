package abstraction;

import abstraction.dao.PlayerNpcFieldGateway;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericApplicationContext;

public class ApplicationContextFactory {
    private static GenericApplicationContext instance;

    static {
        instance = new GenericApplicationContext();
        instance.registerBean(DataConnectionFactoryImpl.class);
        instance.registerBean(PlayerNpcFieldGateway.class);
        instance.refresh();
    }

    public static ApplicationContext getInstance() {
        return instance;
    }
}
