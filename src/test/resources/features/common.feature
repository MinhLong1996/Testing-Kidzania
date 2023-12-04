@common
Feature: Common steps

  Background: User is Logged In
    Given User redirects to "ta.staging"
#      And "LOGIN_PAGE" shows up
#    When User enter valid credentials
#    Then "MASTER_PAGE" shows up in 4 timeout

  Scenario: Common steps
    Given User redirects to "arm.staging"
      And "page" shows up
      And "page" shows up in 2 timeout
      And User clicks on "element"
      And User clicks on "element" by JS
      And User clicks on "element" if present
      And User clicks on "elementType" by "visibleText"
      And User clear text on "element"
      And User clear special text on "element"
      And User types "text" into "element"
      And User types "text" into "element" by JS
      And User refresh browser
      And User navigate "Back/Forward"
      And User switch to "iframe"
      And User exit current iframe
      And User scroll down by 200 pixel
      And User scroll down by 200 pixel 4 times
      And User scroll to "element"
      And "element" is present
      And "element" is present in 2 timeout
      And "element" is not present
      And "element" is not present in 2 timeout
      And "element" is displayed
      And "element" is not displayed
      And "element" shows "text"
      And "element" contains "text"
      And "attribute" of "element" has "value"
      And "attribute" of "element" contains "value"
      And User double click on "element"
      And User mouse hover on "element"
      And User press key "ENTER"
      And User switch to tab 2
      And User close current tab
      And User goes to "URL"
      And User waits for 5 seconds
    # location of json data folder: src/test/resources/data
      And User loads data from json "data_example"


  Scenario: Business steps
    Given User switch to language "English"
      And User enter a string with 201 chars into "SETTINGS_PAGE_QUOTATION_ISSUER_TEXTAREA"
      And User snapshot current state of "見積書番号" column data in "QUOTATION_LIST" table
      And "見積書番号" column data of "QUOTATION_LIST" table should be sorted in "ascending" order properly
      And Current web table should contain below data
        | 見積書番号  | 8           |
        | 取引先     | BearCL999   |
        | 件名       | -           |
        | 見積日     | 2022/05/30  |
        | 作成者     | Bear 11     |
        | 見積金額   | ¥1,008      |
      And Current web table should have more than 1 row

  @encode_password
  Scenario Outline: encode password
    Given User encode the password "Demo@123"
#      And User extract login cookie of "arm.staging"
#      And User create new "GET" request to the endpoint "quotation" with data
#        | quotation_id  | 01G9BRP9MAC8W98RTQEE7TPMY4 |
#      And The response of "GET" request to the endpoint "quotation" with data should remain unchanged
#        | quotation_id  | <quotation_id> |
#    When User types "<filter_value>" into "QUOTATION_PAGE_FILTER_BY_QUOTATION_CLIENT_INPUT"
#      And User clicks on "QUOTATION_PAGE_FILTER_BUTTON"
#      And User waits for 2 seconds
#    Then "<column_name1>" column data of "QUOTATION_LIST" table should be filtered by "TEXT" and the "<filter_value>" properly
#    When User select date range "<filter2_value>" on the date picker "QUOTATION_PAGE_FILTER_BY_QUOTATION_QUOTE_DATE_DATEPICKER" by starting with "START_DATE"
#      And User select item "<filter1_value>" on the dropdown "OPPORTUNITIES_PAGE_FILTER_BY_CLIENT_DROPDOWN"
#      And User types "1000" into "QUOTATION_PAGE_FILTER_BY_ESTIMATED_AMOUNT_FROM_INPUT"
#      And User types "500000" into "QUOTATION_PAGE_FILTER_BY_ESTIMATED_AMOUNT_TO_INPUT"
#      And User clicks on "QUOTATION_PAGE_FILTER_BUTTON"
#      And User waits for 2 seconds
#    Then "<column_name1>" column data of "QUOTATION_LIST" table should be filtered by "DROPDOWN" and the "<filter1_value>" properly
#      And "<column_name1>" column data of "QUOTATION_LIST" table should be filtered by "DATEPICKER" and the "<filter2_value>" properly
#      And "<column_name1>" column data of "QUOTATION_LIST" table should be filtered by "AMOUNT_OF_MONEY" and the "<filter3_value>" properly

    Examples:
      | column_name  | column_name1  | filter_value | filter1_value | filter2_value        | filter3_value |
      | 見積書番号     | 取引先         | 20           | White,White 2 | 2022/5/1 - 2022/6/30 | n/a - 500000  |

  @email_accessibility
  Scenario Outline: Read email sent out from IV app
    Given User read email with title "<title>" from account "<account>"
    Examples:
      | title                                         | account             |
      |【請求書】Ryu test sending pdf（Per SuRaiderから）| bachthevu@gmail.com |

  @template_validation
  Scenario: Validate TAX template
    Given "TEMPLATE_PAGE" shows up
      And "TEMPLATE_PAGE_WITHHOLDING_TEMPLATE" is present
    When User scroll down by 500 pixel
    Then User validate the "TEMPLATE_PAGE_WITHHOLDING_TEMPLATE" by "HTML_SOURCE" comparison based on "expected.txt"
      And User validate the "TEMPLATE_PAGE_WITHHOLDING_TEMPLATE" by "SUB_IMAGE" comparison based on "expected.png"

