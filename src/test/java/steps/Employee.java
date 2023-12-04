package steps;

import core.util.CustomStringUtil;
import io.cucumber.java.en.When;

public class Employee extends BaseStep {
    private CustomStringUtil csUtil = new CustomStringUtil();
    private String locator;

    @When("User click {string} button on the Employee {string}")
    public void userClickButtonOnTheEmployee(String button, String employeeId) {
        if (button.equalsIgnoreCase("DELETE"))
            button = "削除";
        else if (button.equalsIgnoreCase("EDIT"))
            button = "編集";

        locator = "//td[text()='" + employeeId + "']//parent::tr//child::a[text()='" + button + "']";
        getElementByXPath(locator).click();
    }

    @When("User import Employee using {string}")
    public void userImportEmployeeUsing(String fileName) {
        String filePath = csUtil.getFullPathFromFragments(new String[]{"src", "test", "resources", "upload", fileName});
        getElement("EMPLOYEE_PAGE_UPLOAD_INPUT").sendKeys(filePath);
        clickElement("EMPLOYEE_PAGE_UPLOAD_BTN");
    }
}

    


