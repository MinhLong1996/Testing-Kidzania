@Employee
Feature: Quotation

  Background: User is Logged In
    Given User redirects to "ta.staging"
      And "LOGIN_PAGE" shows up
    When User enter valid credentials
    Then "MASTER_PAGE" shows up


  @Func @Employee
  Scenario Outline: C22079 - Add Employee - Check user is able to add loginable employee successfully with valid value
    Given User navigate to "Employee" on left menu
      And "EMPLOYEE_PAGE" shows up
    When User clicks on "EMPLOYEE_PAGE_CREATE_BTN"
    Then "NEW_EMPLOYEE_PAGE" shows up
    When User types "<employee_id>" into "NEW_EMPLOYEE_PAGE_EMPLOYEE_NUMBER_TXT"
      And User types "<fullname_surname>" into "NEW_EMPLOYEE_PAGE_FULL_NAME_SURNAME_TXT"
      And User types "<fullname_firstname>" into "NEW_EMPLOYEE_PAGE_FULL_NAME_FIRSTNAME_TXT"
      And User types "<name_furigana_surname>" into "NEW_EMPLOYEE_PAGE_NAME_FURIGANA_SURNAME_TXT"
      And User types "<name_furigana_firstname>" into "NEW_EMPLOYEE_PAGE_NAME_FURIGANA_FIRSTNAME_TXT"
      And User types "<dob>" into "NEW_EMPLOYEE_PAGE_DOB_TXT"
      And User waits for 1 seconds
      And User clicks on "NEW_EMPLOYEE_PAGE_DOB_LBL"
      And User select item "<office>" on the dropdown "NEW_EMPLOYEE_PAGE_OFFICE_DROPDOWN_BTN > NEW_EMPLOYEE_PAGE_OFFICE_DROPDOWN_DDL"
      And User types "<join_date>" into "NEW_EMPLOYEE_PAGE_JOIN_DATE_TXT"
      And User waits for 1 seconds
      And User clicks on "NEW_EMPLOYEE_PAGE_JOIN_DATE_LBL"
      And User clicks on "NEW_EMPLOYEE_PAGE_LOGINABLE_RDB"
      And User types "<email>" into "NEW_EMPLOYEE_PAGE_EMAIL_ADDRESS_TXT"
      And User select item "<roll>" on the dropdown "NEW_EMPLOYEE_PAGE_ROLL_DROPDOWN_BTN > NEW_EMPLOYEE_PAGE_ROLL_DROPDOWN_DDL"
      And User clicks on "NEW_EMPLOYEE_PAGE_SUBMIT_BTN"
    Then "EMPLOYEE_PAGE" shows up
      And "EMPLOYEE_PAGE_CREATED_SUCCESS_MESSAGE_LBL" is present
      And Current web table should contain below data
        | 従業員番号     | <employee_id>                           |
        | 氏名          | <fullname_surname> <fullname_firstname> |
        | 事業所        | <office>                                |
        | 在籍状況      | 在籍中                                    |
        | 入社日        | <join_date>                              |
        | 退職日        |                                         |
        | メールアドレス | <email>                                 |
        | ロール        | <roll>                                  |
        | 連携         |                                         |

    Examples:
      | employee_id | fullname_surname | fullname_firstname | name_furigana_surname | name_furigana_firstname | dob        | office       | join_date  | email        | roll      |
      | 123         | 倭国              | 踒                 | 大和                   | 山処                    | 1990/02/15 | Office Group | 2022/12/12 | auto2@abc.com | 一般従業員 |


  @Func @Employee
  Scenario Outline: C22078 - Add Employee - Check user is able to add non-loginable employee successfully with valid value
    Given User navigate to "Employee" on left menu
      And "EMPLOYEE_PAGE" shows up
    When User clicks on "EMPLOYEE_PAGE_CREATE_BTN"
    Then "NEW_EMPLOYEE_PAGE" shows up
    When User types "<employee_id>" into "NEW_EMPLOYEE_PAGE_EMPLOYEE_NUMBER_TXT"
      And User types "<fullname_surname>" into "NEW_EMPLOYEE_PAGE_FULL_NAME_SURNAME_TXT"
      And User types "<fullname_firstname>" into "NEW_EMPLOYEE_PAGE_FULL_NAME_FIRSTNAME_TXT"
      And User types "<name_furigana_surname>" into "NEW_EMPLOYEE_PAGE_NAME_FURIGANA_SURNAME_TXT"
      And User types "<name_furigana_firstname>" into "NEW_EMPLOYEE_PAGE_NAME_FURIGANA_FIRSTNAME_TXT"
      And User types "<dob>" into "NEW_EMPLOYEE_PAGE_DOB_TXT"
      And User waits for 1 seconds
      And User clicks on "NEW_EMPLOYEE_PAGE_DOB_LBL"
      And User select item "<office>" on the dropdown "NEW_EMPLOYEE_PAGE_OFFICE_DROPDOWN_BTN > NEW_EMPLOYEE_PAGE_OFFICE_DROPDOWN_DDL"
      And User types "<join_date>" into "NEW_EMPLOYEE_PAGE_JOIN_DATE_TXT"
      And User waits for 1 seconds
      And User clicks on "NEW_EMPLOYEE_PAGE_JOIN_DATE_LBL"
      And User clicks on "NEW_EMPLOYEE_PAGE_NON_LOGINABLE_RDB"
      And User clicks on "NEW_EMPLOYEE_PAGE_SUBMIT_BTN"
    Then "EMPLOYEE_PAGE" shows up
      And "EMPLOYEE_PAGE_CREATED_SUCCESS_MESSAGE_LBL" is present
      And Current web table should contain below data
        | 従業員番号     | <employee_id>                           |
        | 氏名          | <fullname_surname> <fullname_firstname> |
        | 事業所        | <office>                                |
        | 在籍状況      | 在籍中                                    |
        | 入社日        | <join_date>                              |
        | 退職日        |                                         |
        | メールアドレス |                                         |
        | ロール        |                                         |
        | 連携         |                                         |

    Examples:
      | employee_id | fullname_surname | fullname_firstname | name_furigana_surname | name_furigana_firstname | dob        | office       | join_date  |
      | 124         | 倭国              | 踒                 | 大和                   | 山処                    | 1990/02/15 | Office Group | 2022/12/12 |


  @Func @Employee
  Scenario Outline: C22080 - Edit Employee - Check user is able to edit loginable employee successfully with new valid value
    Given User navigate to "Employee" on left menu
      And "EMPLOYEE_PAGE" shows up
    When User click "EDIT" button on the Employee "<employee_id>"
    Then "NEW_EMPLOYEE_PAGE" shows up
    When User types "<fullname_firstname>" into "NEW_EMPLOYEE_PAGE_FULL_NAME_FIRSTNAME_TXT"
      And User types "<name_furigana_surname>" into "NEW_EMPLOYEE_PAGE_NAME_FURIGANA_SURNAME_TXT"
      And User types "<join_date>" into "NEW_EMPLOYEE_PAGE_JOIN_DATE_TXT"
      And User waits for 1 seconds
      And User clicks on "NEW_EMPLOYEE_PAGE_JOIN_DATE_LBL"
      And User clicks on "NEW_EMPLOYEE_PAGE_SUBMIT_BTN"
    Then "NEW_EMPLOYEE_PAGE_UPDATED_SUCCESS_MESSAGE_LBL" is present
    When User navigate to "Employee" on left menu
    Then "EMPLOYEE_PAGE" shows up
      And Current web table should contain below data
        | 従業員番号     | <employee_id>                           |
        | 氏名          | <fullname_surname> <fullname_firstname> |
        | 事業所        | <office>                                |
        | 在籍状況      | 在籍中                                    |
        | 入社日        | <join_date>                              |
        | 退職日        |                                         |
        | メールアドレス | <email>                                 |
        | ロール        | <roll>                                  |
        | 連携         |                                         |

    Examples:
      | employee_id | fullname_surname | fullname_firstname | name_furigana_surname | office       | join_date  | email         | roll      |
      | 123         | 倭国              | 踒和                | 大和日本               | Office Group | 2022/12/13 | auto2@abc.com | 一般従業員 |


  @Func @Employee
  Scenario Outline: C22081 - Edit Employee - Check user is able to edit non-loginable employee successfully with new valid value
    Given User navigate to "Employee" on left menu
      And "EMPLOYEE_PAGE" shows up
    When User click "EDIT" button on the Employee "<employee_id>"
    Then "NEW_EMPLOYEE_PAGE" shows up
    When User types "<fullname_firstname>" into "NEW_EMPLOYEE_PAGE_FULL_NAME_FIRSTNAME_TXT"
      And User types "<name_furigana_surname>" into "NEW_EMPLOYEE_PAGE_NAME_FURIGANA_SURNAME_TXT"
      And User types "<join_date>" into "NEW_EMPLOYEE_PAGE_JOIN_DATE_TXT"
      And User waits for 1 seconds
      And User clicks on "NEW_EMPLOYEE_PAGE_JOIN_DATE_LBL"
      And User clicks on "NEW_EMPLOYEE_PAGE_SUBMIT_BTN"
    Then "NEW_EMPLOYEE_PAGE_UPDATED_SUCCESS_MESSAGE_LBL" is present
    When User navigate to "Employee" on left menu
    Then "EMPLOYEE_PAGE" shows up
      And Current web table should contain below data
        | 従業員番号     | <employee_id>                           |
        | 氏名          | <fullname_surname> <fullname_firstname> |
        | 事業所        | <office>                                |
        | 在籍状況      | 在籍中                                    |
        | 入社日        | <join_date>                              |
        | 退職日        |                                         |
        | メールアドレス |                                         |
        | ロール        |                                        |
        | 連携         |                                         |

    Examples:
      | employee_id | fullname_surname | fullname_firstname | name_furigana_surname | office       | join_date  |
      | 124         | 倭国              | 踒和                | 大和日本               | Office Group | 2022/12/13 |


  @Func @Employee 
  Scenario Outline: C22082 - Edit Employee - Check user is able to edit non-loginable employee to loginable employee successfully with new valid value
    Given User navigate to "Employee" on left menu
      And "EMPLOYEE_PAGE" shows up
    When User click "EDIT" button on the Employee "<employee_id>"
    Then "NEW_EMPLOYEE_PAGE" shows up
    When User types "<fullname_firstname>" into "NEW_EMPLOYEE_PAGE_FULL_NAME_FIRSTNAME_TXT"
      And User types "<name_furigana_surname>" into "NEW_EMPLOYEE_PAGE_NAME_FURIGANA_SURNAME_TXT"
      And User types "<join_date>" into "NEW_EMPLOYEE_PAGE_JOIN_DATE_TXT"
      And User waits for 1 seconds
      And User clicks on "NEW_EMPLOYEE_PAGE_JOIN_DATE_LBL"
      And User clicks on "NEW_EMPLOYEE_PAGE_LOGINABLE_RDB"
      And User types "<email>" into "NEW_EMPLOYEE_PAGE_EMAIL_ADDRESS_TXT"
      And User select item "<roll>" on the dropdown "NEW_EMPLOYEE_PAGE_ROLL_DROPDOWN_BTN > NEW_EMPLOYEE_PAGE_ROLL_DROPDOWN_DDL"
      And User clicks on "NEW_EMPLOYEE_PAGE_SUBMIT_BTN"
    Then "NEW_EMPLOYEE_PAGE_UPDATED_SUCCESS_MESSAGE_LBL" is present
    When User navigate to "Employee" on left menu
    Then "EMPLOYEE_PAGE" shows up
      And Current web table should contain below data
        | 従業員番号     | <employee_id>                           |
        | 氏名          | <fullname_surname> <fullname_firstname> |
        | 事業所        | <office>                                |
        | 在籍状況      | 在籍中                                    |
        | 入社日        | <join_date>                              |
        | 退職日        |                                         |
        | メールアドレス | <email>                                 |
        | ロール        | <roll>                                  |
        | 連携         |                                         |
    When User click "DELETE" button on the Employee "<employee_id>"
      And User clicks on "EMPLOYEE_PAGE_CONFIRM_DELETE_BTN"
    Then "EMPLOYEE_PAGE_DELETED_SUCCESS_MESSAGE_LBL" is present

    Examples:
      | employee_id | fullname_surname | fullname_firstname | name_furigana_surname | office       | join_date  | email         | roll      |
      | 124         | 倭国              | 踒                 | 大和日                 | Office Group | 2022/12/14 | auto3@abc.com | 一般従業員  |


  @Func @Employee 
  Scenario Outline: C22083 - Edit Employee - Check user is able to edit loginable employee to non-loginable employee successfully with new valid value
    Given User navigate to "Employee" on left menu
      And "EMPLOYEE_PAGE" shows up
    When User click "EDIT" button on the Employee "<employee_id>"
    Then "NEW_EMPLOYEE_PAGE" shows up
    When User types "<name_furigana_surname>" into "NEW_EMPLOYEE_PAGE_NAME_FURIGANA_SURNAME_TXT"
      And User types "<join_date>" into "NEW_EMPLOYEE_PAGE_JOIN_DATE_TXT"
      And User waits for 1 seconds
      And User clicks on "NEW_EMPLOYEE_PAGE_JOIN_DATE_LBL"
      And User clicks on "NEW_EMPLOYEE_PAGE_REMOVE_EMAIL_BTN"
      And User clicks on "NEW_EMPLOYEE_PAGE_CONFIRM_DELETE_BTN"
      And User clicks on "NEW_EMPLOYEE_PAGE_NON_LOGINABLE_RDB"
      And User types "<fullname_firstname>" into "NEW_EMPLOYEE_PAGE_FULL_NAME_FIRSTNAME_TXT"
      And User clicks on "NEW_EMPLOYEE_PAGE_SUBMIT_BTN"
    Then "NEW_EMPLOYEE_PAGE_UPDATED_SUCCESS_MESSAGE_LBL" is present
    When User navigate to "Employee" on left menu
    Then "EMPLOYEE_PAGE" shows up
      And Current web table should contain below data
        | 従業員番号     | <employee_id>                           |
        | 氏名          | <fullname_surname> <fullname_firstname> |
        | 事業所        | <office>                                |
        | 在籍状況      | 在籍中                                    |
        | 入社日        | <join_date>                              |
        | 退職日        |                                         |
        | メールアドレス |                                         |
        | ロール        |                                         |
        | 連携         |                                         |
    When User click "DELETE" button on the Employee "<employee_id>"
      And User clicks on "EMPLOYEE_PAGE_CONFIRM_DELETE_BTN"
    Then "EMPLOYEE_PAGE_DELETED_SUCCESS_MESSAGE_LBL" is present

    Examples:
      | employee_id | fullname_surname | fullname_firstname | name_furigana_surname | office       | join_date  |
      | 123         | 倭国              | 踒                 | 大和日                 | Office Group | 2022/12/13 |


  @Func @Employee
  Scenario Outline: C22084 - Import Employee - Check user is able to import one employee via CSV successfully with valid value
    Given User navigate to "Employee" on left menu
      And "EMPLOYEE_PAGE" shows up
    When User clicks on "EMPLOYEE_PAGE_IMPORT_BTN"
    Then "EMPLOYEE_PAGE_IMPORT_HEADER_LBL" is present
      And User waits for 5 seconds
    When User import Employee using "<file_name>"
    Then "EMPLOYEE_PAGE_UPLOADED_SUCCESS_MESSAGE_LBL" is present in 4 timeout
    When User navigate to "Employee" on left menu
    Then Current web table should contain below data
        | 従業員番号     | <employee_id>        |
        | 氏名          | 倭国1 mid 1 踒和 1    |
        | 事業所        | Office Group         |
        | 在籍状況      | 退職済み               |
        | 入社日        | 2022/05/05           |
        | 退職日        | 2022/12/01           |
        | メールアドレス | at22084@tax.auto.com |
        | ロール        | 一般従業員            |
        | 連携         |                      |
    When User click "DELETE" button on the Employee "<employee_id>"
      And User clicks on "EMPLOYEE_PAGE_CONFIRM_DELETE_BTN"
    Then "EMPLOYEE_PAGE_DELETED_SUCCESS_MESSAGE_LBL" is present

    Examples:
      | file_name                  | employee_id |
      | C22084_Import_Employee.csv | at22084     |


  @Func @Employee @debug
  Scenario Outline: C22085 - Import Employee - Check user is able to import multiple employee via CSV successfully with valid value
    Given User navigate to "Employee" on left menu
      And "EMPLOYEE_PAGE" shows up
    When User clicks on "EMPLOYEE_PAGE_IMPORT_BTN"
    Then "EMPLOYEE_PAGE_IMPORT_HEADER_LBL" is present
      And User waits for 5 seconds
    When User import Employee using "<file_name>"
    Then "EMPLOYEE_PAGE_UPLOADED_SUCCESS_MESSAGE_LBL" is present in 4 timeout
    When User start new custom driver
      And User login with valid credentials
      And User navigate to "Employee" on left menu
      And User waits for 5 seconds
    Then "EMPLOYEE_PAGE_MULTIPLE_IMPORT_IMG" is present
    When User clicks on "EMPLOYEE_PAGE_CHECK_ALL_CKB"
      And User clicks on "EMPLOYEE_PAGE_DELETE_ALL_BTN"
      And User clicks on "EMPLOYEE_PAGE_CONFIRM_DELETE_ALL_BTN"
    Then "EMPLOYEE_PAGE_DELETED_SUCCESS_MESSAGE_LBL" is present

    Examples:
      | file_name                  | employee_id |
      | C22085_Import_Employee.csv | at22084     |























