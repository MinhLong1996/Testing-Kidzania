@Register
Feature: Register


  @Register
  Scenario Outline: C22079 - Add Employee - Check user is able to add loginable employee successfully with valid value
    Given User redirects to "ta.staging"
      And "LOGIN_PAGE" shows up
    When User clicks on "LOGIN_PAGE_REGISTER_NOW_BTN"
    Then "REGISTER_PAGE" shows up
    When User types "ungvien10@yopmail.com" into "REGISTER_PAGE_EMAIL_TXT"
    And User types "<Pass>" into "REGISTER_PAGE_PASSWORD_TXT"
    And User types "<Pass>" into "REGISTER_PAGE_CONFIRM_PASSWORD_TXT"
    And User clicks on "REGISTER_PAGE_REGISTER_BTN"

      And User waits for 5 seconds


    Examples:
      | employee_id | Pass             | fullname_firstname |   office       | date  | email        |
      | 123         | L@ng38465992     | Long                |   Office Group | 2022/12/12 | ungvien10@yopmail.com |


























