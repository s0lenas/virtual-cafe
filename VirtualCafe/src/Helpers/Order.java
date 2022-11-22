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
