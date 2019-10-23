package abstraction;

public class ApplicationContextFactory {
    private static ApplicationContext instance = new ApplicationContext();

    public static ApplicationContext getInstance() {
        return instance;
    }
}
