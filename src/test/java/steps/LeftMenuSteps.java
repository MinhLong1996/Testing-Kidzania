package steps;

import io.cucumber.java.en.And;

public class LeftMenuSteps extends BaseStep {

    @And("User navigate to {string} on left menu")
    public void userNavigateToOnLeftMenu(String menuName) {
        switch (menuName) {
            case "Employee":
                menuName = "従業員";
                break;
            case "Tax Adjustment":
                menuName = "手続き";
                break;
            case "Authority":
                menuName = "権限";
                break;
        }
        String xpath = "//div[@class='menu-icon']//child::a[1]//child::span[text()='" + menuName + "'] | //a[contains(@class,'list-group-item') and text()='権限']";
        getElementByXPath(xpath).click();
    }
}
