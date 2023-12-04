@Login
Feature: Login page actions
# mvn clean verify -Dcucumber.filter.tags="@Login"
  @Func@Login
  Scenario Outline: Login page actions
    Given User redirects to "ta.staging"
#     And "LOGIN_PAGE" shows up
#    And User waits for 2 seconds
    And User clicks on "LOGIN_PAGE_GENERAL_LOGIN"
    And User clicks on "LOGIN_PAGE_LOGIN_BTN"
#    And User clicks on "LOGIN_PAGE_SIGNUP_BTN"
#    And User clicks on "LOGIN_PAGE_AGREE_ALL_CHECKBOX"
#    And User clicks on "LOGIN_PAGE_NEXT_AGREE_ALL_BTN"
#    And User types "<PHONE>" into "LOGIN_PAGE_PHONE_NUMBER_INPUT"
#    And User clicks on "LOGIN_PAGE_APPLY_BTN"
#    And User waits for 2 seconds
#    And User types "<CODE>" into "LOGIN_PAGE_CODE_INPUT"
#    And User clicks on "LOGIN_PAGE_NEXT_TO_INFO_BTN"
#    And User types "<Pass>" into "LOGIN_PAGE_PASSWORD_INPUT"
#    And User types "<Pass>" into "LOGIN_PAGE_CONFIRM_PASSWORD_INPUT"
#    And User types "<Name>" into "LOGIN_PAGE_NAME_INPUT"
#    And User types "<email>" into "LOGIN_PAGE_EMAIL_INPUT"
#    And User clicks on "LOGIN_PAGE_DUPLICATE_BTN"
#    And User waits for 2 seconds
#    And User clicks on "LOGIN_PAGE_DUPLICATE_BACK_BTN"
#    And User types "<ADDRESS>" into "LOGIN_PAGE_ADDRESS_INPUT"
#    And User clicks on "LOGIN_PAGE_SELECT_CHECKBOX"
#    And User clicks on "LOGIN_PAGE_NEXT_AGREE_ALL_BTN"
#    And User waits for 3 seconds
#    And User clicks on "LOGIN_PAGE_LOGIN_BTN"
    And User types "<PHONE>" into "LOGIN_PAGE_PHONE_NUMBER_INPUT"
    And User types "<Pass>" into "LOGIN_PAGE_PASSWORD_LOGIN_INPUT"
  # And User clicks on "LOGIN_PAGE_REMEMBER_ID_CHECKBOX"
    And User clicks on "LOGIN_PAGE_LOGIN_PAGE_BTN"
    And User waits for 2 seconds
    And User clicks on "LOGIN_PAGE_CREATE_GENERAL_TICKET_BTN"
   # And User clicks on "LOGIN_PAGE_CHOICE_CALENDAR_MONTH08_BTN"
     And User clicks on "LOGIN_PAGE_CREATE_GENERAL_TICKET_BTN"
     And User scroll down by 300 pixel 1 times

     And User clicks on "LOGIN_PAGE_CHOICE_CALENDAR_BTN"
     And User waits for 2 seconds
     And User clicks on "LOGIN_PAGE_CHOICE_PART1_BTN"
     And User scroll down by 310 pixel 2 times
     And User clicks on "LOGIN_PAGE_NEXT_STEP3_BTN"
     And User waits for 1 seconds
     And User clicks on "LOGIN_PAGE_IMPORT_BTN"
    And User waits for 2 seconds
     And User scroll down by 220 pixel 1 times
     And User waits for 2 seconds
#    And User clicks on "LOGIN_PAGE_CHOICE_BABY_BTN"
#    And User types "<Name Baby>" into "LOGIN_PAGE_CHOICE_BABY_NAME_INPUT"
#    And User clicks on "LOGIN_PAGE_CHOICE_BABY_BIRTH_DATE_BTN"
#    And User waits for 2 seconds
#    And User clicks on "LOGIN_PAGE_CHOICE_BABY_YEAR_BTN"
#    And User clicks on "LOGIN_PAGE_CHOICE_BABY_YEAR_2022_BTN"
#    And User clicks on "LOGIN_PAGE_CHOICE_BABY_DAY_BTN"
#    And User waits for 1 seconds
#  # And User clicks on "LOGIN_PAGE_SAVE_INFORMATION_BABY_BTN"
#  # And User scroll down by 50 pixel 3 times
#    And User clicks on "LOGIN_PAGE_CHOICE_TODDLER"
#    And User types "<Name toddler>" into "LOGIN_PAGE_CHOICE_TODDLER_NAME_INPUT"
#    And User waits for 1 seconds
#    And User clicks on "LOGIN_PAGE_CHOICE_TODDLER_BIRTH_DATE_BTN"
#    And User waits for 1 seconds
#    And User scroll down by 250 pixel 1 times
#    And User clicks on "LOGIN_PAGE_CHOICE_TODDLER_YEAR_BTN"
#    And User waits for 1 seconds
#    And User clicks on "LOGIN_PAGE_CHOICE_TODDLER_YEAR_2020_BTN"
#    And User clicks on "LOGIN_PAGE_CHOICE_TODDLER_DAY_BTN"
#    And User waits for 1 seconds
#    And User clicks on "LOGIN_PAGE_SAVE_INFORMATION_TODDLER_BTN"
     And User clicks on "LOGIN_PAGE_NEXT_STEP_SHOW_POPUP_BTN"
     And User clicks on "LOGIN_PAGE_COUPON_BTN"
     And User scroll down by 150 pixel 1 times
     And User types "<CODE>" into "LOGIN_PAGE_COUPON_INPUT"
     And User waits for 3 seconds
     And User clicks on "LOGIN_PAGE_APPLY1_BTN"
#    And User clicks on "LOGIN_PAGE_TURN_OFF_POPUP_BTN"
#    And User waits for 1 seconds
#    And User clicks on "LOGIN_PAGE_CHOICE_ADULT_BTN"
#    And User clicks on "LOGIN_PAGE_CHOICE_RESERVED_PERSON_CHECKBOX"
#    And User scroll down by 250 pixel 2 times
#    And User types "<date>" into "LOGIN_PAGE_CHOICE_ADULT_BIRTH_DATE_INPUT"
#    And User clicks on "LOGIN_PAGE_CHOICE_ADULT_BIRTH_DATE_INPUT"
#    And User clicks on "LOGIN_PAGE_CHOICE_ADULT_DAY_BTN"
#    And User waits for 3 seconds
#    And User clicks on "LOGIN_PAGE_NEXT_STEP4_BTN"
#    And User clicks on "LOGIN_PAGE_ADD_COUPON"
#    And User clicks on "LOGIN_PAGE_DOWNLOAD_COUPON"
#    And User clicks on "LOGIN_PAGE_APPLY_VOUCHER"
#    And User clicks on "LOGIN_PAGE_CHOICE_ATM"
    And User clicks on "LOGIN_PAGE_PURCHASE_BTN"
    And User waits for 5 seconds
#    And User scroll down by 250 pixel 2 times
#    And User clicks on "LOGIN_PAGE_CHOICE_NCB_BANK_BTN"
#    And User types "<CARD NUMBER>" into "LOGIN_PAGE_CARD_NUMBER_INPUT"
#    And User types "<CARD HOLDER>" into "LOGIN_PAGE_CARD_HOLDER_INPUT"
#    And User types "<ISSUING DATE>" into "LOGIN_PAGE_ISSUING_DATE_INPUT"
#    And User clicks on "LOGIN_PAGE_CONTINUE_BTN"
#    And User types "<OTP>" into "LOGIN_PAGE_OTP_INPUT"
#    And User clicks on "LOGIN_PAGE_CONFIRM_BTN"
#    And User waits for 5 seconds
#    And User scroll down by 200 pixel
#    And User clicks on "LOGIN_PAGE_GO_HISTORY_BTN"
#    And User clicks on "LOGIN_PAGE_GO_TO_HISTORY_BTN"
#    And User waits for 1 seconds
#    And User clicks on "LOGIN_PAGE_GO_TO_HISTORY_DETAIL_BTN"
#    And User scroll down by 100 pixel
#    And User clicks on "LOGIN_PAGE_CANCEL_PAYMENT_BTN"
#    And User clicks on "LOGIN_PAGE_CREATE_NEW_GROUP"
#    And User scroll down by 380 pixel
#    And User types "<GROUP NAME>" into "LOGIN_PAGE_GROUPNAME_INPUT"
#    And User types "<NAME USER>" into "LOGIN_PAGE_NAME_USER_GROUP_INPUT"
#    And User types "<PHONE>" into "LOGIN_PAGE_PHONE_NUMBER_GROUP_INPUT"
#    And User clicks on "LOGIN_PAGE_CHOICE_DATE_VISIT_INPUT"
#    And User clicks on "LOGIN_PAGE_CHOICE_MONTH08_BTN"
#    And User clicks on "LOGIN_PAGE_CHOICE_DAY_BTN"
#    And User types "<email>" into "LOGIN_PAGE_EMAIL_GROUP_BTN"
#    And User types "<Discription>" into "LOGIN_PAGE_DISCRIPTION_INPUT"
#    And User clicks on "LOGIN_PAGE_CONFIRM_GROUP_BTN"
#    And User clicks on "LOGIN_PAGE_GO_BACK_MAINPAGE_BTN"
#    And User waits for 5 seconds
    Examples:
  | Name toddler |CODE     |  GROUP NAME |NAME USER|DATE VIST|   PHONE              | Pass             | Name        | Name Baby       | date       | email                    | CARD NUMBER                  |     CARD HOLDER      | ISSUING DATE | OTP   | Todder date |        ADDRESS                                                           |    ANSWERS1                                     |    ANSWERS2                                 |  Discription   |
    | Join         |20102 023   |  HIGHSHOOL  |HARRY    |         |   0988363698       | 1234             | Harry Kun   | FIN             | 04/06/1995 | t.minhlong013@gmail.com |  9704198526191432198         |   NGUYEN VAN A       |    07/15     | 123456 |01/07/2020 |7 Đường số 7C, Khu đô thị Lakeview city, Quận 2, Thành phố Hồ Chí Minh, Vietnam  |      Đặt các câu hỏi mở cho người nghe      |   Hỏi người nghe xem họ có hiểu bạn không.  |I would like to visit the KidZania game zone. I have heard that it is a lot of fun and that there are many different games to choose from. I would love to try out all of the games and see what they are like. I think it would be a great way to have some fun and learn new things.|