package core.env;

import core.helper.Helper;

public enum Environment {
    INSTANCE;

    public static final int TIMEOUT_SMALL = Integer.valueOf(Helper.getConf("webdriver.timeouts.small")) / 1000;
    public static final int TIMEOUT_DEFAULT = Integer.valueOf(Helper.getConf("webdriver.timeouts.default")) / 1000;
    public static final String SERVER_MAIN = Helper.getConf("webdriver.remote.url");
    public static final String SERVER_CUSTOM = Helper.getConf("custom.driver.url");
    public static final String TIME_ZONE_ID = Helper.getConf("env.timezone");
    public static final String ACCOUNT_EMAIL = Helper.getConf("account.email");
    private PageObject pageObject;

    Environment() {
    }

    public PageObject getPageObject() {
        if (pageObject == null) {
            pageObject = new PageObject();
            pageObject.loadFromDirectory(Helper.getConf("locator.folder"));
        }
        return pageObject;
    }
}
