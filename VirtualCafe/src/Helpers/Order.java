package Helpers;

import java.util.ArrayList;
import java.util.List;

public class Order implements Runnable{

    private final String customerName;
    private final int numCoffee;
    private final int numTea;
    private final int coffeeBrewTime = 45000; // 45 seconds
    private final int teaBrewTime = 30000; // 30 seconds

    private boolean complete;

    private List<Drink> drinks = new ArrayList<>();
    private List<Drink> tray = new ArrayList<>();

    public Order(String customerName, int numCoffee, int numTea) {

        this.customerName = customerName;
        this.numCoffee = numCoffee;
        this.numTea = numTea;
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
        // TODO handle enums
    }

    public String getCustomerName() {
        return customerName;
    }

    public int getNumCoffee() {
        return numCoffee;
    }

    public int getNumTea() {
        return numTea;
    }

    public List<Drink> getDrinks() {
        return drinks;
    }

    public List<Drink> getTray() {
        return tray;
    }

    public void addToTray(Drink drink) {
        tray.add(drink);
    }

    public void addDrink(int numDrink, DrinkType drinkType) {
        for (int i = 0; i < numDrink; i++) {
            Drink drink = new Drink(this, drinkType);
            drinks.add(drink);
        }
    }

    public boolean isComplete() {
        return complete;
    }

    public void setComplete(boolean complete) {
        this.complete = complete;
    }

    // Overriding the toString method for printing out orders
    @Override
    public String toString() {
        String orderString = "Order for " + customerName + ":\n";
        //List<String> orderString = new ArrayList<>();
        //orderString.add("Order for " + customerName + ":");

        int numCoffeeWaiting, numTeaWaiting, numCoffeeBrewing, numTeaBrewing, numCoffeeTray, numTeaTray;
        numCoffeeWaiting = numTeaWaiting = numCoffeeBrewing = numTeaBrewing = numCoffeeTray = numTeaTray = 0;

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

        // if (numCoffeeWaiting > 0 && numTeaWaiting > 0) orderString.add("- " + numCoffeeWaiting + " coffee(s) and " + numTeaWaiting + " tea(s) currently in waiting area");
        // else if (numCoffeeWaiting > 0) orderString.add("- " + numCoffeeWaiting + " coffee(s) currently in waiting area");
        // else if (numTeaWaiting > 0) orderString.add("- " + numTeaWaiting + " tea(s) currently in waiting area");
        // else orderString.add("- No drinks currently in waiting area");

        // if (numCoffeeBrewing > 0 && numTeaBrewing > 0) orderString.add("- " + numCoffeeBrewing + " coffee(s) and " + numTeaBrewing + " tea(s) currently being prepared");
        // else if (numCoffeeBrewing > 0) orderString.add("- " + numCoffeeBrewing + " coffee(s) currently being prepared");
        // else if (numTeaBrewing > 0) orderString.add("- " + numTeaBrewing + " tea(s) currently being prepared");
        // else orderString.add("- No drinks currently being prepared");

        // if (numCoffeeTray > 0 && numTeaTray > 0) orderString.add("- " + numCoffeeTray + " coffee(s) and " + numTeaTray + " tea(s) currently on tray");
        // else if (numCoffeeTray > 0) orderString.add("- " + numCoffeeTray + " coffee(s) currently on tray");
        // else if (numTeaTray > 0) orderString.add("- " + numTeaTray + " tea(s) currently on tray");
        // else orderString.add("- No drinks currently on tray");

        if (numCoffeeWaiting > 0 && numTeaWaiting > 0) orderString += "- " + numCoffeeWaiting + " coffee(s) and " + numTeaWaiting + " tea(s) currently in waiting area\n";
        else if (numCoffeeWaiting > 0) orderString += "- " + numCoffeeWaiting + " coffee(s) currently in waiting area\n";
        else if (numTeaWaiting > 0) orderString += "- " + numTeaWaiting + " tea(s) currently in waiting area\n";
        else orderString += "- No drinks currently in waiting area\n";

        if (numCoffeeBrewing > 0 && numTeaBrewing > 0) orderString += "- " + numCoffeeBrewing + " coffee(s) and " + numTeaBrewing + " tea(s) currently being prepared\n";
        else if (numCoffeeBrewing > 0) orderString += "- " + numCoffeeBrewing + " coffee(s) currently being prepared\n";
        else if (numTeaBrewing > 0) orderString += "- " + numTeaBrewing + " tea(s) currently being prepared\n";
        else orderString += "- No drinks currently being prepared\n";

        if (numCoffeeTray > 0 && numTeaTray > 0) orderString += "- " + numCoffeeTray + " coffee(s) and " + numTeaTray + " tea(s) currently on tray";
        else if (numCoffeeTray > 0) orderString += "- " + numCoffeeTray + " coffee(s) currently on tray";
        else if (numTeaTray > 0) orderString += "- " + numTeaTray + " tea(s) currently on tray";
        else orderString += "- No drinks currently on tray";

        return orderString;
    }

    @Override
    public void run() {
        // while(!complete) {
        //     for (Drink drink : drinks) {
        //         if (drink.getDrinkStatus() == DrinkStatus.Trayed) {
                    
        //         }
        //     }
        // }
    }

    // Maybe a list for drinks within the order?
    // Maybe a list for statuses of individual drinks within the order?
}
