package steps;

import io.cucumber.java.en.And;
import io.cucumber.java.en.When;

import java.util.Map;

public class ARM_2129 extends BaseStep {

    @And("Create button in the quotation creation screen should be enabled initially")
    public void createButtonInTheQuotationCreationScreenShouldBeEnabledInitially() {
        clickElement("QUOTATION_PAGE_NEW_QUOTATION_CREATE_BUTTON");
        assertElementPresentInGivenTimeout("QUOTATION_PAGE_NEW_QUOTATION_ERROR_MESSAGE_LABEL",2);
        takeScreenshot();
    }

    @When("User enter Client info as below")
    public void userEnterClientInfoAsBelow(Map<String, String> table) {
        String client = table.get("client").trim();
        String clientDetails = table.get("client_details").trim();
        String title = table.get("title").trim();

        typeText(client, "QUOTATION_PAGE_NEW_QUOTATION_CLIENT_NAME_INPUT");
        typeText(clientDetails, "QUOTATION_PAGE_NEW_QUOTATION_CLIENT_DETAILS_INPUT");
        typeText(title, "QUOTATION_PAGE_NEW_QUOTATION_TITLE_INPUT");
    }

    @And("User enter {string} into the mandatory field {string}")
    public void userEnterIntoTheMandatoryField(String value, String field) {
        typeText(value, field);
        delaySync(1);
        pressKey("ESC");
    }
}
