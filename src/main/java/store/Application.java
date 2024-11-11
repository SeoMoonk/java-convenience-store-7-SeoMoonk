package store;

import global.config.AppConfig;
import store.controller.StoreController;

public class Application {
    public static void main(String[] args) {
        AppConfig appConfig = new AppConfig();
        StoreController storeController = appConfig.getStoreController();

        storeController.setUp();
        storeController.shopping();
    }
}
