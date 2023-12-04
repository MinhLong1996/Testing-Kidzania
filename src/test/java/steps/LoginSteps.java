package steps;

import io.cucumber.java.en.And;
import io.cucumber.java.en.When;

public class LoginSteps extends BaseStep {

    @And("User enter valid credentials")
    public void userEnterValidCredentials() {
        String user = helper.getConfig("env." + context.getContext("env") + ".login.user");
        String pass = helper.getConfig("env." + context.getContext("env") + ".login.pass");
        pass = helper.decodePassPhrase(pass);
        logger.info("Login using account: {}", user);
        typeText(user, "LOGIN_PAGE_USERNAME_INPUT");
        clickElement("LOGIN_PAGE_SUBMIT_BUTTON");
        typeText(pass, "LOGIN_PAGE_PASSWORD_INPUT");
        clickElement("LOGIN_PAGE_SUBMIT_BUTTON");
    }

    @When("User enter valid credentials of {string}")
    public void userEnterValidCredentialsOf(String office) {
        String user = helper.getConfig("env." + context.getContext("env") + "." + office + ".login.user");
        String pass = helper.getConfig("env." + context.getContext("env") + "." + office + ".login.pass");
        pass = helper.decodePassPhrase(pass);
        logger.info("Login using account: {} and office: {}", user, office);
        typeText(user, "LOGIN_PAGE_USERNAME_INPUT");
        clickElement("LOGIN_PAGE_SUBMIT_BUTTON");
        typeText(pass, "LOGIN_PAGE_PASSWORD_INPUT");
        clickElement("LOGIN_PAGE_SUBMIT_BUTTON");
    }

    @When("User logout ARM")
    public void userLogoutARM() {
        clickElement("MASTER_PAGE_LOGGED_IN_USER_LABEL");
        try {
            clickElement("MASTER_PAGE_LOGOUT_BUTTON");
        } catch (Exception e) {
            clickElementByJS("MASTER_PAGE_LOGOUT_BUTTON");
        }
    }
}
