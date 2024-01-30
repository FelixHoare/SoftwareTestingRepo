package uk.ac.ed.inf;

import org.junit.Test;
import org.junit.jupiter.api.RepeatedTest;
import uk.ac.ed.inf.ilp.constant.OrderStatus;
import uk.ac.ed.inf.ilp.constant.OrderValidationCode;
import uk.ac.ed.inf.ilp.constant.SystemConstants;
import uk.ac.ed.inf.ilp.data.*;

import java.util.Random;

import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class OrderValidatorTest {

    public static Restaurant createRestaurant() {
        return new Restaurant("Testaurant",
                new LngLat(-3.186874, 55.944494),
                new DayOfWeek[] { DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY,
                        DayOfWeek.FRIDAY, DayOfWeek.SATURDAY, DayOfWeek.SUNDAY },
                new Pizza[] { new Pizza("Margherita", 1000), new Pizza("Pepperoni", 1100),
                        new Pizza("Hawaiian", 1100) });
    }

    private static String createValidCardNumber() {
        Random random = new Random();
        StringBuilder cardNoBuilder = new StringBuilder(16);

        for (int i = 0; i < 16; i++) {
            int digit = random.nextInt(10);
            cardNoBuilder.append(digit);
        }

        return cardNoBuilder.toString();
    }

    private static String createInvalidCardNumber() {
        StringBuilder sb = new StringBuilder(16);
        Random random = new Random();

        sb.append(random.nextInt(26) + 'A');

        for (int i = 0; i < 15; i++) {
            int randomType = random.nextInt(2);
            char randomChar;
            if (randomType == 0) {
                randomChar = (char) (random.nextInt(10) + '0');
            } else {
                randomChar = (char) (random.nextInt(26) + 'A');
            }
            sb.append(randomChar);
        }

        return sb.toString();
    }

    private static String createInvalidCardNumberLen() {
        Random random = new Random();
        int length = random.nextInt(1, 101);

        if (length == 16) {
            length += 1;
        }

        StringBuilder cardNoBuilder = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            int digit = random.nextInt(10);
            cardNoBuilder.append(digit);
        }

        return cardNoBuilder.toString();
    }

    private static String createValidExpiryDate() {
        Random random = new Random();
        LocalDate currentDate = LocalDate.now();
        int randomYear = currentDate.getYear() + random.nextInt(10);
        int randomMonth = random.nextInt(12) + 1;

        LocalDate expDate = LocalDate.of(randomYear, randomMonth, 1);
        String formatDate = expDate.format(DateTimeFormatter.ofPattern("MM/yy"));

        return formatDate;
    }

    private static String createValidExpiryDateNow() {
        LocalDate currentDate = LocalDate.now();
        String formatDate = currentDate.format(DateTimeFormatter.ofPattern("MM/yy"));

        return formatDate;
    }

    private static String createInvalidExpiryDatePattern() {
        Random random = new Random();
        LocalDate currentDate = LocalDate.now();
        int randomYear = currentDate.getYear() + random.nextInt(10);
        int randomMonth = random.nextInt(12) + 1;

        LocalDate expDate = LocalDate.of(randomYear, randomMonth, 1);
        String formatDate = expDate.format(DateTimeFormatter.ofPattern("MM/yyyy"));

        return formatDate;
    }

    private static String createExpiryDatePast() {
        Random random = new Random();
        LocalDate currentDate = LocalDate.now();
        int randomYear = currentDate.getYear() - random.nextInt(10) - 1;
        int randomMonth = random.nextInt(12) + 1;

        LocalDate expDate = LocalDate.of(randomYear, randomMonth, 1);
        String formatDate = expDate.format(DateTimeFormatter.ofPattern("MM/yy"));

        return formatDate;
    }

    private static String createInvalidExpiryDate() {
        Random random = new Random();
        StringBuilder sb = new StringBuilder(5);

        for (int i = 0; i < 5; i++) {
            char randomChar = (char) (random.nextInt(26) + 'A');
            sb.append(randomChar);
        }

        return sb.toString();
    }

    private static String createValidCVV() {
        Random random = new Random();
        StringBuilder cvvBuilder = new StringBuilder(3);

        for (int i = 0; i < 3; i++) {
            int digit = random.nextInt(10);
            cvvBuilder.append(digit);
        }

        return cvvBuilder.toString();
    }

    private static String createInvalidCvvChar() {
        StringBuilder sb = new StringBuilder(3);
        Random random = new Random();
        sb.append(random.nextInt(26) + 'A');

        for (int i = 0; i < 2; i++) {
            int randomType = random.nextInt(2);
            char randomChar;
            if (randomType == 0) {
                randomChar = (char) (random.nextInt(10) + '0');
            } else {
                randomChar = (char) (random.nextInt(26) + 'A');
            }
            sb.append(randomChar);
        }

        return sb.toString();
    }

    private static String createInvalidCvvLen() {
        Random random = new Random();
        int length = random.nextInt(1, 101);

        if (length == 3) {
            length += 1;
        }

        StringBuilder cardNoBuilder = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            int digit = random.nextInt(10);
            cardNoBuilder.append(digit);
        }

        return cardNoBuilder.toString();
    }

    public static Order createOrder(String cardNumber, String cardExpiry, String cardCVV, Restaurant restaurant) {
        Order order = new Order();
        StringBuilder sb = new StringBuilder(8);
        Random random = new Random();

        for (int i = 0; i < 8; i++) {
            int randomType = random.nextInt(2);
            char randomChar;
            if (randomType == 0) {
                randomChar = (char) (random.nextInt(10) + '0');
            } else {
                randomChar = (char) (random.nextInt(26) + 'A');
            }
            sb.append(randomChar);
        }

        order.setOrderNo(sb.toString());
        order.setOrderDate(LocalDate.of(2023, 12, 25));

        order.setCreditCardInformation(
                new CreditCardInformation(cardNumber.toString(), cardExpiry, cardCVV.toString()));

        int numPizzas = random.nextInt(4) + 1;
        Pizza[] pizzas = new Pizza[numPizzas];
        int orderCost = 0;

        for (int i = 0; i < numPizzas; i++) {
            pizzas[i] = (restaurant.menu()[random.nextInt(3)]);
            orderCost += pizzas[i].priceInPence();
        }

        order.setPizzasInOrder(pizzas);
        order.setPriceTotalInPence(orderCost + SystemConstants.ORDER_CHARGE_IN_PENCE);

        order.setOrderStatus(OrderStatus.UNDEFINED);
        order.setOrderValidationCode(OrderValidationCode.UNDEFINED);

        return order;
    }

    public void printOutput() {

    }

    @Test
    public void testTester() {
        String cardNumber = createValidCardNumber();
        String cardExpiry = createValidExpiryDate();
        String cardCVV = createValidCVV();

        Restaurant restaurant = createRestaurant();
        Order order = createOrder(cardNumber, cardExpiry, cardCVV, restaurant);
        int orderLength = order.getPizzasInOrder().length;

        System.out.println("\nOrder Number: " + order.getOrderNo()
                + "\nOrder Status: " + order.getOrderStatus()
                + "\nOrder Validation Code: " + order.getOrderValidationCode()
                + "\nOrder Date: " + order.getOrderDate()
                + "\nCard Number: " + order.getCreditCardInformation().getCreditCardNumber()
                + "\nCVV: " + order.getCreditCardInformation().getCvv()
                + "\nCard Expiry Date: " + order.getCreditCardInformation().getCreditCardExpiry()
                + "\nLength of Order: " + order.getPizzasInOrder().length
                + "\nTotal Order Cost:" + order.getPriceTotalInPence());

        System.out.println("Pizzas in Order: ");
    
        for (int i = 0; i < orderLength; i++) {
            System.out.print(order.getPizzasInOrder()[i].name() + " ");
        }
    }

    @RepeatedTest(100)
    public void testValidOrder() {
        OrderValidator validator = new OrderValidator();

        String cardNumber = createValidCardNumber();
        String cardExpiry = createValidExpiryDate();
        String cardCVV = createValidCVV();

        Restaurant restaurant = createRestaurant();
        Order order = createOrder(cardNumber, cardExpiry, cardCVV, restaurant);

        validator.validateOrder(order, new Restaurant[] { restaurant });

        assertEquals(OrderValidationCode.NO_ERROR, order.getOrderValidationCode());
        assertEquals(OrderStatus.VALID_BUT_NOT_DELIVERED, order.getOrderStatus());
    }

    @RepeatedTest(100)
    public void testValidCardNumber() {
        OrderValidator validator = new OrderValidator();

        String cardNumber = createValidCardNumber();
        String cardExpiry = createValidExpiryDate();
        String cardCVV = createValidCVV();

        Restaurant restaurant = createRestaurant();
        Order order = createOrder(cardNumber, cardExpiry, cardCVV, restaurant);

        assertTrue(validator.checkCardNumber(order));

        validator.validateOrder(order, new Restaurant[] { restaurant });
        assertEquals(OrderValidationCode.NO_ERROR, order.getOrderValidationCode());
        assertEquals(OrderStatus.VALID_BUT_NOT_DELIVERED, order.getOrderStatus());
    }

    @RepeatedTest(100)
    public void testInvalidCardNumber() {
        OrderValidator validator = new OrderValidator();

        String cardNumber = createInvalidCardNumber();
        String cardExpiry = createValidExpiryDate();
        String cardCVV = createValidCVV();

        Restaurant restaurant = createRestaurant();
        Order order = createOrder(cardNumber, cardExpiry, cardCVV, restaurant);

        assertFalse(validator.checkCardNumber(order));
        validator.validateOrder(order, new Restaurant[] { restaurant });

        assertEquals(OrderValidationCode.CARD_NUMBER_INVALID, order.getOrderValidationCode());
        assertEquals(OrderStatus.INVALID, order.getOrderStatus());
    }

    @RepeatedTest(100)
    public void testInvalidCardNumberLength() {
        OrderValidator validator = new OrderValidator();

        String cardNumber = createInvalidCardNumberLen();
        String cardExpiry = createValidExpiryDate();
        String cardCVV = createValidCVV();

        Restaurant restaurant = createRestaurant();
        Order order = createOrder(cardNumber, cardExpiry, cardCVV, restaurant);

        assertFalse(validator.checkCardNumber(order));
        validator.validateOrder(order, new Restaurant[] { restaurant });

        assertEquals(OrderValidationCode.CARD_NUMBER_INVALID, order.getOrderValidationCode());
        assertEquals(OrderStatus.INVALID, order.getOrderStatus());
    }

    @RepeatedTest(100)
    public void testNullCardNumber() {
        OrderValidator validator = new OrderValidator();

        String cardNumber = createValidCardNumber();
        String cardExpiry = createValidExpiryDate();
        String cardCVV = createValidCVV();

        Restaurant restaurant = createRestaurant();
        Order order = createOrder(cardNumber, cardExpiry, cardCVV, restaurant);
        order.getCreditCardInformation().setCreditCardNumber(null);

        assertFalse(validator.checkCardNumber(order));
        validator.validateOrder(order, new Restaurant[] { restaurant });

        assertEquals(OrderValidationCode.CARD_NUMBER_INVALID, order.getOrderValidationCode());
        assertEquals(OrderStatus.INVALID, order.getOrderStatus());
    }

    @RepeatedTest(100)
    public void testValidExpiryDate() {
        OrderValidator validator = new OrderValidator();

        String cardNumber = createValidCardNumber();
        String cardExpiry = createValidExpiryDate();
        String cardCVV = createValidCVV();

        Restaurant restaurant = createRestaurant();
        Order order = createOrder(cardNumber, cardExpiry, cardCVV, restaurant);

        assertTrue(validator.checkExpiryDate(order));
        validator.validateOrder(order, new Restaurant[] { restaurant });

        assertEquals(OrderValidationCode.NO_ERROR, order.getOrderValidationCode());
        assertEquals(OrderStatus.VALID_BUT_NOT_DELIVERED, order.getOrderStatus());
    }

    @RepeatedTest(100)
    public void testExpiryDateNow() {
        OrderValidator validator = new OrderValidator();

        String cardNumber = createValidCardNumber();
        String cardExpiry = createValidExpiryDateNow();
        String cardCVV = createValidCVV();

        Restaurant restaurant = createRestaurant();
        Order order = createOrder(cardNumber, cardExpiry, cardCVV, restaurant);

        assertTrue(validator.checkExpiryDate(order));
        validator.validateOrder(order, new Restaurant[] { restaurant });

        assertEquals(OrderValidationCode.NO_ERROR, order.getOrderValidationCode());
        assertEquals(OrderStatus.VALID_BUT_NOT_DELIVERED, order.getOrderStatus());
    }

    @RepeatedTest(100)
    public void testInvalidExpiryDatePattern() {
        OrderValidator validator = new OrderValidator();

        String cardNumber = createValidCardNumber();
        String cardExpiry = createInvalidExpiryDatePattern();
        String cardCVV = createValidCVV();

        Restaurant restaurant = createRestaurant();
        Order order = createOrder(cardNumber, cardExpiry, cardCVV, restaurant);

        assertFalse(validator.checkExpiryDate(order));
        validator.validateOrder(order, new Restaurant[] { restaurant });

        assertEquals(OrderValidationCode.EXPIRY_DATE_INVALID, order.getOrderValidationCode());
        assertEquals(OrderStatus.INVALID, order.getOrderStatus());
    }

    @RepeatedTest(100)
    public void testPastExpiryDate() {
        OrderValidator validator = new OrderValidator();

        String cardNumber = createValidCardNumber();
        String cardExpiry = createExpiryDatePast();
        String cardCVV = createValidCVV();

        Restaurant restaurant = createRestaurant();
        Order order = createOrder(cardNumber, cardExpiry, cardCVV, restaurant);

        assertFalse(validator.checkExpiryDate(order));
        validator.validateOrder(order, new Restaurant[] { restaurant });

        assertEquals(OrderValidationCode.EXPIRY_DATE_INVALID, order.getOrderValidationCode());
        assertEquals(OrderStatus.INVALID, order.getOrderStatus());
    }

    @RepeatedTest(100)
    public void testInvalidExpiryDate() {
        OrderValidator validator = new OrderValidator();

        String cardNumber = createValidCardNumber();
        String cardExpiry = createInvalidExpiryDate();
        String cardCVV = createValidCVV();

        Restaurant restaurant = createRestaurant();
        Order order = createOrder(cardNumber, cardExpiry, cardCVV, restaurant);

        assertFalse(validator.checkExpiryDate(order));
        validator.validateOrder(order, new Restaurant[] { restaurant });

        assertEquals(OrderValidationCode.EXPIRY_DATE_INVALID, order.getOrderValidationCode());
        assertEquals(OrderStatus.INVALID, order.getOrderStatus());
    }

    @RepeatedTest(100)
    public void testNullExpiryDate() {
        OrderValidator validator = new OrderValidator();

        String cardNumber = createValidCardNumber();
        String cardExpiry = createValidExpiryDate();
        String cardCVV = createValidCVV();

        Restaurant restaurant = createRestaurant();
        Order order = createOrder(cardNumber, cardExpiry, cardCVV, restaurant);
        order.getCreditCardInformation().setCreditCardExpiry(null);

        assertFalse(validator.checkExpiryDate(order));
        validator.validateOrder(order, new Restaurant[] { restaurant });

        assertEquals(OrderValidationCode.EXPIRY_DATE_INVALID, order.getOrderValidationCode());
        assertEquals(OrderStatus.INVALID, order.getOrderStatus());
    }

    @RepeatedTest(100)
    public void testValidCVV() {
        OrderValidator validator = new OrderValidator();

        String cardNumber = createValidCardNumber();
        String cardExpiry = createValidExpiryDate();
        String cardCVV = createValidCVV();

        Restaurant restaurant = createRestaurant();
        Order order = createOrder(cardNumber, cardExpiry, cardCVV, restaurant);
        
        assertTrue(validator.checkCVV(order));
        validator.validateOrder(order, new Restaurant[] { restaurant });

        assertEquals(OrderValidationCode.NO_ERROR, order.getOrderValidationCode());
        assertEquals(OrderStatus.VALID_BUT_NOT_DELIVERED, order.getOrderStatus());
    }

    @RepeatedTest(100)
    public void testInvalidCVV() {
        OrderValidator validator = new OrderValidator();

        String cardNumber = createValidCardNumber();
        String cardExpiry = createValidExpiryDate();
        String cardCVV = createInvalidCvvChar();

        Restaurant restaurant = createRestaurant();
        Order order = createOrder(cardNumber, cardExpiry, cardCVV, restaurant);

        assertFalse(validator.checkCVV(order));
        validator.validateOrder(order, new Restaurant[] { restaurant });

        assertEquals(OrderValidationCode.CVV_INVALID, order.getOrderValidationCode());
        assertEquals(OrderStatus.INVALID, order.getOrderStatus());
    }

    @RepeatedTest(100)
    public void testInvalidCVVLen() {
        OrderValidator validator = new OrderValidator();

        String cardNumber = createValidCardNumber();
        String cardExpiry = createValidExpiryDate();
        String cardCVV = createInvalidCvvLen();

        Restaurant restaurant = createRestaurant();
        Order order = createOrder(cardNumber, cardExpiry, cardCVV, restaurant);

        assertFalse(validator.checkCVV(order));
        validator.validateOrder(order, new Restaurant[] { restaurant });

        assertEquals(OrderValidationCode.CVV_INVALID, order.getOrderValidationCode());
        assertEquals(OrderStatus.INVALID, order.getOrderStatus());
    }

    @RepeatedTest(100)
    public void testNullCVV() {
        OrderValidator validator = new OrderValidator();

        String cardNumber = createValidCardNumber();
        String cardExpiry = createValidExpiryDateNow();
        String cardCVV = createValidCVV();

        Restaurant restaurant = createRestaurant();
        Order order = createOrder(cardNumber, cardExpiry, cardCVV, restaurant);
        order.getCreditCardInformation().setCvv(null);

        assertFalse(validator.checkCVV(order));
        validator.validateOrder(order, new Restaurant[] { restaurant });

        assertEquals(OrderValidationCode.CVV_INVALID, order.getOrderValidationCode());
        assertEquals(OrderStatus.INVALID, order.getOrderStatus());
    }

    @Test
    public void testValidTotal() {
        OrderValidator validator = new OrderValidator();

        String cardNumber = createValidCardNumber();
        String cardExpiry = createValidExpiryDate();
        String cardCVV = createValidCVV();
        
        Restaurant restaurant = createRestaurant();
        Order order = createOrder(cardNumber, cardExpiry, cardCVV, restaurant);

        validator.validateOrder(order, new Restaurant[] { restaurant });

        assertEquals(OrderValidationCode.NO_ERROR, order.getOrderValidationCode());
        assertEquals(OrderStatus.VALID_BUT_NOT_DELIVERED, order.getOrderStatus());
    }

    @Test
    public void testNegativeTotal() {
        OrderValidator validator = new OrderValidator();

        String cardNumber = createValidCardNumber();
        String cardExpiry = createValidExpiryDate();
        String cardCVV = createValidCVV();

        Restaurant restaurant = createRestaurant();
        Order order = createOrder(cardNumber, cardExpiry, cardCVV, restaurant);
        order.setPriceTotalInPence(-1000);

        validator.validateOrder(order, new Restaurant[] { restaurant });

        assertEquals(OrderValidationCode.TOTAL_INCORRECT, order.getOrderValidationCode());
        assertEquals(OrderStatus.INVALID, order.getOrderStatus());
    }

    @Test
    public void testZeroTotal() {
        OrderValidator validator = new OrderValidator();

        String cardNumber = createValidCardNumber();
        String cardExpiry = createValidExpiryDate();
        String cardCVV = createValidCVV();

        Restaurant restaurant = createRestaurant();
        Order order = createOrder(cardNumber, cardExpiry, cardCVV, restaurant);
        order.setPriceTotalInPence(0);

        validator.validateOrder(order, new Restaurant[] { restaurant });

        assertEquals(OrderValidationCode.TOTAL_INCORRECT, order.getOrderValidationCode());
        assertEquals(OrderStatus.INVALID, order.getOrderStatus());
    }

    @Test
    public void testNoDeliveryCharge() {
        OrderValidator validator = new OrderValidator();

        String cardNumber = createValidCardNumber();
        String cardExpiry = createValidExpiryDate();
        String cardCVV = createValidCVV();

        Restaurant restaurant = createRestaurant();
        Order order = createOrder(cardNumber, cardExpiry, cardCVV, restaurant);
        order.setPriceTotalInPence(order.getPriceTotalInPence() - SystemConstants.ORDER_CHARGE_IN_PENCE);

        validator.validateOrder(order, new Restaurant[] { restaurant });

        assertEquals(OrderValidationCode.TOTAL_INCORRECT, order.getOrderValidationCode());
        assertEquals(OrderStatus.INVALID, order.getOrderStatus());
    }

    @RepeatedTest(100)
    public void testPizzaNotOnMenu() {
        OrderValidator validator = new OrderValidator();

        String cardNumber = createValidCardNumber();
        String cardExpiry = createValidExpiryDate();
        String cardCVV = createValidCVV();

        Restaurant restaurant = createRestaurant();
        Order order = createOrder(cardNumber, cardExpiry, cardCVV, restaurant);
        order.setPizzasInOrder(new Pizza[] { new Pizza("Not on menu", 1000) });
        order.setPriceTotalInPence(1100);
        
        validator.validateOrder(order, new Restaurant[] { restaurant });
        assertFalse(validator.isPizzaInMenus(order.getPizzasInOrder()[0], new Restaurant[] { restaurant }));
        
        assertEquals(OrderValidationCode.PIZZA_NOT_DEFINED, order.getOrderValidationCode());
        assertEquals(OrderStatus.INVALID, order.getOrderStatus());
    }

    @RepeatedTest(100)
    public void testPizzasFromMultipleRestaurants() {
        Random random = new Random();
        OrderValidator validator = new OrderValidator();

        String cardNumber = createValidCardNumber();
        String cardExpiry = createValidExpiryDate();
        String cardCVV = createValidCVV();

        Restaurant restaurant = createRestaurant();
        Order order = createOrder(cardNumber, cardExpiry, cardCVV, restaurant);

        Restaurant secondRestaurant = new Restaurant("Testaurant Two",
                new LngLat(-3.1912869215011597, 55.945535152517735),
                new DayOfWeek[] { DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY,
                        DayOfWeek.FRIDAY, DayOfWeek.SATURDAY, DayOfWeek.SUNDAY },
                new Pizza[] { new Pizza("Meat-Feast", 1200), new Pizza("BBQ-Chicken", 1200),
                        new Pizza("Calzone", 1100) });

        order.setPizzasInOrder(null);
        order.setPriceTotalInPence(0);

        int numPizzas = random.nextInt(2, 5);
        Pizza[] pizzas = generateRandomOrder(restaurant, secondRestaurant, numPizzas);
        order.setPizzasInOrder(pizzas);
        int orderCost = 0;

        for (int i = 0; i < numPizzas; i++) {
            orderCost += order.getPizzasInOrder()[i].priceInPence();
        }

        order.setPriceTotalInPence(orderCost + SystemConstants.ORDER_CHARGE_IN_PENCE);
        validator.validateOrder(order, new Restaurant[] { restaurant, secondRestaurant });

        assertEquals(OrderValidationCode.PIZZA_FROM_MULTIPLE_RESTAURANTS, order.getOrderValidationCode());
        assertEquals(OrderStatus.INVALID, order.getOrderStatus());

    }

    private static Pizza[] generateRandomOrder(Restaurant rstA, Restaurant rstB, int length) {
        Random random = new Random();
        Pizza[] pizzas = new Pizza[length];

        pizzas[0] = rstA.menu()[random.nextInt(rstA.menu().length)];
        pizzas[1] = rstB.menu()[random.nextInt(rstB.menu().length)];

        for (int i = 2; i < pizzas.length; i++) {
            if (random.nextBoolean()) {
                pizzas[i] = rstA.menu()[random.nextInt(rstA.menu().length)];
            } else {
                pizzas[i] = rstB.menu()[random.nextInt(rstB.menu().length)];
            }
        }

        return pizzas;
    }

    @RepeatedTest(100)
    public void testTooManyPizzas() {
        Random random = new Random();
        OrderValidator validator = new OrderValidator();

        String cardNumber = createValidCardNumber();
        String cardExpiry = createValidExpiryDate();
        String cardCVV = createValidCVV();

        Restaurant restaurant = createRestaurant();
        Order order = createOrder(cardNumber, cardExpiry, cardCVV, restaurant);

        order.setPizzasInOrder(null);
        order.setPriceTotalInPence(0);

        int numPizzas = random.nextInt(5, 101);
        Pizza[] pizzas = new Pizza[numPizzas];

        int orderCost = 0;

        for (int i = 0; i < numPizzas; i++) {
            pizzas[i] = (restaurant.menu()[random.nextInt(3)]);
            orderCost += pizzas[i].priceInPence();
        }

        order.setPizzasInOrder(pizzas);
        order.setPriceTotalInPence(orderCost + SystemConstants.ORDER_CHARGE_IN_PENCE);

        validator.validateOrder(order, new Restaurant[] { restaurant });

        assertEquals(OrderValidationCode.MAX_PIZZA_COUNT_EXCEEDED, order.getOrderValidationCode());
        assertEquals(OrderStatus.INVALID, order.getOrderStatus());

    }

    @RepeatedTest(100)
    public void testRestaurantClosed() {
        OrderValidator validator = new OrderValidator();
        Restaurant closedRestaurant = new Restaurant("Testaurant the Third",
                new LngLat(-3.1838572025299072, 55.94449876875712),
                new DayOfWeek[] {},
                new Pizza[] { new Pizza("Margherita", 1000), new Pizza("Pepperoni", 1100),
                        new Pizza("Hawaiian", 1100) });

        String cardNumber = createValidCardNumber();
        String cardExpiry = createValidExpiryDate();
        String cardCVV = createValidCVV();

        Order order = createOrder(cardNumber, cardExpiry, cardCVV, closedRestaurant);

        validator.validateOrder(order, new Restaurant[] { closedRestaurant });
        
        assertEquals(OrderValidationCode.RESTAURANT_CLOSED, order.getOrderValidationCode());
        assertEquals(OrderStatus.INVALID, order.getOrderStatus());

    }

}
