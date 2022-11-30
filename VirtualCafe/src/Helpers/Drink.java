package Helpers;

// Types of drinks
enum DrinkType {
    Tea,
    Coffee
}

// Status states
enum DrinkStatus {
    Waiting,
    Brewing,
    Trayed
}

public class Drink implements Runnable {
    
    private final Order order;
    private final String drinkOwner;
    private DrinkType drinkType;
    private DrinkStatus drinkStatus;
    private static int brewTimeTea = 30000; // 30 seconds
    private static int brewTimeCoffee = 45000; // 45 seconds
    
    // The drink constructor takes in an order object and a drink type. It then sets the drink
    // status to waiting, and sets the drink owner to the customer name from the order object by default.
    public Drink(Order order, DrinkType drinkType) {
        this.order = order;
        this.drinkType = drinkType;
        this.drinkStatus = DrinkStatus.Waiting;
        this.drinkOwner = order.getCustomerName();
    }

    public Order getOrder() { return order; }

    public String getDrinkOwner() { return drinkOwner; }

    public DrinkType getDrinkType() { return drinkType; }

    public DrinkStatus getDrinkStatus() { return drinkStatus; }
    
    /**
     * When the run method is called, the drink status is set to brewing, and the thread sleeps for
     * the brewTime of the drinkType. After the sleep, the drinkStatus is set to Trayed to simulate actual brewing.
     */
    @Override
    public void run() {
        try {
            drinkStatus = DrinkStatus.Brewing;

            if (drinkType == DrinkType.Tea) Thread.sleep(brewTimeTea);
            else if (drinkType == DrinkType.Coffee) Thread.sleep(brewTimeCoffee);

            drinkStatus = DrinkStatus.Trayed;
        } catch (InterruptedException e) {}
    }

}
