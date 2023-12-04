package core.util;

import com.github.javafaker.Faker;

public class RandomUtil {
    private static final Faker faker = new Faker();

    public static String random(String type) {
        switch (type.toLowerCase()) {
            case "zipcode":
                return faker.number().digits(3) + "-" + faker.number().digits(4);
            case "address":
                return faker.address().streetAddress();
            case "phone":
                return faker.phoneNumber().subscriberNumber(7);
            case "name":
                return faker.name().name();
            case "id":
                return faker.idNumber().valid();
            case "email":
                return faker.internet().emailAddress();
            case "item":
                return faker.commerce().productName();
            default:
                throw new IllegalArgumentException(type + " is invalid!");
        }
    }
}
