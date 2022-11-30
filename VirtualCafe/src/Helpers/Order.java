package Helpers;

import java.util.ArrayList;
import java.util.List;

public class Order {

    private final String customerName;
    private boolean complete;

    private List<Drink> drinks = new ArrayList<>();

    // This is the constructor for the Order class. It takes in a customer name, and the number of
    // coffees and teas in the order. It then creates a new drink object for each coffee and tea in the
    // order, and adds them to the drinks list.
    public Order(String customerName, int numCoffee, int numTea) {
        this.customerName = customerName;
        this.complete = false;

        if (numCoffee > 0) {
            for (int i = 0; i < numCoffee; i++) {
                Drink coffee = new Drink(this, DrinkType.Coffee);
                drinks.add(coffee);
            }
        }

        if (numTea > 0) {
            for (int i = 0; i < numTea; i++) {
                Drink tea = new Drink(this, DrinkType.Tea);
                drinks.add(tea);
            }
        }
    }

    public String getCustomerName() { return customerName; }

    // To account for potential changes in the order beyond initial creation,
    // this method counts drinks within the list, rather than simply returning
    // the number set in the constructor of the class. Same goes for getNumTea() method
    public int getNumCoffee() {
        int count = 0;
        for (Drink drink : drinks) { if (drink.getDrinkType() == DrinkType.Coffee) count++; }
        return count;
    }
    public int getNumTea() {
        int count = 0;
        for (Drink drink : drinks) { if (drink.getDrinkType() == DrinkType.Tea) count++; }
        return count;
    }

    public List<Drink> getDrinks() { return drinks; }

    /** This method is used to check if the order is complete.
     * If the number of drinks in the tray is equal to the number of drinks in the order, then the
     * order is complete.
     * 
     * @return The method is returning a boolean value.
     */
    public boolean isComplete() {
        int count = 0;

        for (int i = 0; i < drinks.size(); i++) {
            if (drinks.get(i).getDrinkStatus() == DrinkStatus.Trayed) count++;
            if (count == drinks.size()) complete = true;
        }

        return complete;
    }

    /**
     * This function returns a string that contains the number of coffees and teas ordered
     * 
     * @return The number of coffees and teas ordered.
     */
    public String orderMessage() {
        String msg = "";
        int completeCoffee = getNumCoffee();
        int completeTea = getNumTea();
        
        if (completeCoffee > 0 && completeTea == 0) msg += completeCoffee + " coffee(s))";
        else if (completeCoffee == 0 && completeTea > 0) msg += completeTea + " tea(s))";
        else msg += completeCoffee + " coffee(s) and " + completeTea + " tea(s))";

        return msg;
    }

    // Overriding the toString method for printing out orders
    @Override
    public String toString() {
        String orderString = "Order for " + customerName + ":\n";
        int numCoffeeWaiting, numTeaWaiting, numCoffeeBrewing, numTeaBrewing, numCoffeeTray, numTeaTray;
        numCoffeeWaiting = numTeaWaiting = numCoffeeBrewing = numTeaBrewing = numCoffeeTray = numTeaTray = 0;

        // Counting the number of drinks in each status
        for(Drink drink : this.drinks) {
            if (drink.getDrinkStatus() == DrinkStatus.Waiting) {
                if (drink.getDrinkType() == DrinkType.Coffee) numCoffeeWaiting++;
                else numTeaWaiting++;
            } else if (drink.getDrinkStatus() == DrinkStatus.Brewing) {
                if (drink.getDrinkType() == DrinkType.Coffee) numCoffeeBrewing++;
                else numTeaBrewing++;
            } else if (drink.getDrinkStatus() == DrinkStatus.Trayed) {
                if (drink.getDrinkType() == DrinkType.Coffee) numCoffeeTray++;
                else numTeaTray++;
            }
        }

        // Drinks with waiting status
        if (numCoffeeWaiting > 0 && numTeaWaiting > 0) orderString += "- " + numCoffeeWaiting + " coffee(s) and " + numTeaWaiting + " tea(s) currently in waiting area\n";
        else if (numCoffeeWaiting > 0) orderString += "- " + numCoffeeWaiting + " coffee(s) currently in waiting area\n";
        else if (numTeaWaiting > 0) orderString += "- " + numTeaWaiting + " tea(s) currently in waiting area\n";
        else orderString += "- No drinks currently in waiting area\n";

        // Drinks with brewing status
        if (numCoffeeBrewing > 0 && numTeaBrewing > 0) orderString += "- " + numCoffeeBrewing + " coffee(s) and " + numTeaBrewing + " tea(s) currently being prepared\n";
        else if (numCoffeeBrewing > 0) orderString += "- " + numCoffeeBrewing + " coffee(s) currently being prepared\n";
        else if (numTeaBrewing > 0) orderString += "- " + numTeaBrewing + " tea(s) currently being prepared\n";
        else orderString += "- No drinks currently being prepared\n";

        // drinks with tray status
        if (numCoffeeTray > 0 && numTeaTray > 0) orderString += "- " + numCoffeeTray + " coffee(s) and " + numTeaTray + " tea(s) currently on tray";
        else if (numCoffeeTray > 0) orderString += "- " + numCoffeeTray + " coffee(s) currently on tray";
        else if (numTeaTray > 0) orderString += "- " + numTeaTray + " tea(s) currently on tray";
        else orderString += "- No drinks currently on tray";

        return orderString;
    }
}
