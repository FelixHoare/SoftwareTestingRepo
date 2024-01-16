package uk.ac.ed.inf;

import uk.ac.ed.inf.ilp.constant.OrderStatus;
import uk.ac.ed.inf.ilp.constant.OrderValidationCode;
import uk.ac.ed.inf.ilp.constant.SystemConstants;
import uk.ac.ed.inf.ilp.data.Order;
import uk.ac.ed.inf.ilp.data.Pizza;
import uk.ac.ed.inf.ilp.data.Restaurant;
import uk.ac.ed.inf.ilp.interfaces.OrderValidation;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

/**
 * interface to validate an order
 */
public class OrderValidator implements OrderValidation {
    /**
     * validate an order and deliver a validated version where the
     * OrderStatus and OrderValidationCode are set accordingly.
     *
     * Fields to validate include (among others - for details please see the OrderValidationStatus):
     * card number (16 digit numeric)
     * CVV
     * expiration date
     * the total order price
     * the menu items selected in the order
     * the involved restaurants, including if an order is from multiple restaurants
     * if the maximum pizza count is exceeded
     * if the order is valid on the given date for the involved restaurants (opening days)
     *
     * @param orderToValidate    is the order which needs validation
     * @param definedRestaurants is the vector of defined restaurants with their according menu structure
     * @return the validated order
     */

    public Order validateOrder(Order orderToValidate, Restaurant[] definedRestaurants) {
        Restaurant restaurantToCheck;
        // check credit card number:
        // check if number is non-null
        // check if number is 16 digits
        // check if number is numeric
        if (!checkCardNumber(orderToValidate)) {
            orderToValidate.setOrderValidationCode(OrderValidationCode.CARD_NUMBER_INVALID);
            orderToValidate.setOrderStatus(OrderStatus.INVALID);
            return orderToValidate;
        }

        // check CVV:
        // check if CVV is non-null
        // check if CVV is 3 digits
        // check if CVV is numeric
        if (!checkCVV(orderToValidate)) {
            orderToValidate.setOrderValidationCode(OrderValidationCode.CVV_INVALID);
            orderToValidate.setOrderStatus(OrderStatus.INVALID);
            return orderToValidate;
        }

        // check expiry date:
        // check if expiry date is non-null
        // check if expiry date is in the future
        // check if expiry date is in the format MM/YY / is numeric
        if (!checkExpiryDate(orderToValidate)) {
            orderToValidate.setOrderValidationCode(OrderValidationCode.EXPIRY_DATE_INVALID);
            orderToValidate.setOrderStatus(OrderStatus.INVALID);
            return orderToValidate;
        }

        int orderCost = 0;

        for (int i = 0; i < orderToValidate.getPizzasInOrder().length; i++) {
            orderCost += orderToValidate.getPizzasInOrder()[i].priceInPence();
        }

        // check if total price is correct and non-negative
        if (orderToValidate.getPriceTotalInPence() != orderCost + SystemConstants.ORDER_CHARGE_IN_PENCE ||
                orderToValidate.getPriceTotalInPence() < 0) {
            orderToValidate.setOrderValidationCode(OrderValidationCode.TOTAL_INCORRECT);
            orderToValidate.setOrderStatus(OrderStatus.INVALID);
            return orderToValidate;
        }

        // check if pizza is on any menu
        for (Pizza pizza : orderToValidate.getPizzasInOrder()) {
            if (!isPizzaInMenus(pizza, definedRestaurants)) {
                orderToValidate.setOrderValidationCode(OrderValidationCode.PIZZA_NOT_DEFINED);
                orderToValidate.setOrderStatus(OrderStatus.INVALID);
                return orderToValidate;
            }
        }

        // check if order contains between 1 and 4 pizzas
        if (orderToValidate.getPizzasInOrder().length > SystemConstants.MAX_PIZZAS_PER_ORDER) {
            orderToValidate.setOrderValidationCode(OrderValidationCode.MAX_PIZZA_COUNT_EXCEEDED);
            orderToValidate.setOrderStatus(OrderStatus.INVALID);
            return orderToValidate;
        }

        // check if order contains pizzas from multiple restaurants
        if (oneRestaurant(orderToValidate.getPizzasInOrder(), definedRestaurants) == null) {
            orderToValidate.setOrderValidationCode(OrderValidationCode.PIZZA_FROM_MULTIPLE_RESTAURANTS);
            orderToValidate.setOrderStatus(OrderStatus.INVALID);
            return orderToValidate;
        }

        // check if order is valid on the given date for the involved restaurants (opening days)
        DayOfWeek orderDay = orderToValidate.getOrderDate().getDayOfWeek();
        restaurantToCheck = oneRestaurant(orderToValidate.getPizzasInOrder(), definedRestaurants);
        if (!isRestaurantOpen(restaurantToCheck, orderDay)) {
            orderToValidate.setOrderValidationCode(OrderValidationCode.RESTAURANT_CLOSED);
            orderToValidate.setOrderStatus(OrderStatus.INVALID);
            return orderToValidate;
        }

        // if code makes it to here, there are no errors
        if (orderToValidate.getOrderValidationCode() == OrderValidationCode.UNDEFINED) {
            orderToValidate.setOrderValidationCode(OrderValidationCode.NO_ERROR);
            orderToValidate.setOrderStatus(OrderStatus.VALID_BUT_NOT_DELIVERED);
        }

        return orderToValidate;
    }

    public boolean checkCardNumber(Order order) {
        if (order.getCreditCardInformation().getCreditCardNumber() == null) {
            return false;
        }
        if (!order.getCreditCardInformation().getCreditCardNumber().matches("[0-9]+")) {
            return false;
        }
        if (order.getCreditCardInformation().getCreditCardNumber().length() != 16) {
            return false;
        }
        return true;
    }

    public boolean checkCVV(Order order) {
        if (order.getCreditCardInformation().getCvv() == null) {
            return false;
        }
        if (!order.getCreditCardInformation().getCvv().matches("[0-9]+")) {
            return false;
        }
        if (order.getCreditCardInformation().getCvv().length() != 3) {
            return false;
        }
        return true;
    }

    public boolean checkExpiryDate(Order order) {
        if (order.getCreditCardInformation().getCreditCardExpiry() == null) {
            return false;
        }
        if (!order.getCreditCardInformation().getCreditCardExpiry().matches("^(0[1-9]|1[0-2])\\/?([0-9]{2})")) {
            return false;
        }
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MM/yy");
        YearMonth cardExpiry = YearMonth.parse(order.getCreditCardInformation().getCreditCardExpiry(), dateFormatter);
        LocalDate now = LocalDate.now();
        if (now.isAfter(cardExpiry.atEndOfMonth())) {
            return false;
        }

        return true;
    }

    public boolean isPizzaInMenu(Pizza pizza, Restaurant restaurant) {
        return Arrays.asList(restaurant.menu()).contains(pizza);
    }

    public boolean isPizzaInMenus(Pizza pizza, Restaurant[] restaurants) {
        // check if pizza is in any menu
        for (Restaurant restaurant : restaurants) {
            Pizza[] menu = restaurant.menu();
            for (Pizza menuPizza : menu) {
                if (menuPizza.name().equals(pizza.name())) {
                    return true;
                }
            }
        }
        return false;

    }

    public Restaurant oneRestaurant(Pizza[] pizzas, Restaurant[] restaurants) {
        // check if pizzas are from one restaurant
        // initialise array of length restaurants.length
        // loop through menus and increment count for each restaurant if pizza is in menu
        // if an array index is equal to the length of pizzas, return the restaurant at that index
        int[] pizzaCount = new int[restaurants.length];
        for (int i = 0; i < restaurants.length; i++) {
            for (Pizza pizza : pizzas) {
                if (isPizzaInMenu(pizza, restaurants[i])) {
                    pizzaCount[i] += 1;
                }
            }
            if (pizzaCount[i] == pizzas.length) {
                return restaurants[i];
            }
        }
        return null;
    }

    public static boolean isRestaurantOpen(Restaurant restaurant, DayOfWeek date) {
        return date == null || Arrays.asList(restaurant.openingDays()).contains(date);
    }



}