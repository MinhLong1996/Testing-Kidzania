package hooks;

import core.helper.LogHelper;
import core.helper.TestHelper;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import net.serenitybdd.core.pages.PageObject;

public class Hooks extends PageObject {
    private static final LogHelper logger = LogHelper.getInstance();

    @Before
    public void initialization() {
        logger.info("Initialization");
    }

    @After
    public void tearDown() {
        logger.info("Tear Down");
        TestHelper.tearDown();
    }
}
