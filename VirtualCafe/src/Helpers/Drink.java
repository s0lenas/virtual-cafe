package Helpers;

enum DrinkType {
    Tea,
    Coffee
}

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
    private boolean stop = false;
    
    public Drink(Order order, DrinkType drinkType) {
        this.order = order;
        this.drinkType = drinkType;
        this.drinkStatus = DrinkStatus.Waiting;
        this.drinkOwner = order.getCustomerName();
    }

    public Order getOrder() {
        return order;
    }

    public String getDrinkOwner() {
        return drinkOwner;
    }

    public DrinkType getDrinkType() {
        return drinkType;
    }

    public DrinkStatus getDrinkStatus() {
        return drinkStatus;
    }
    
    @Override
    public void run() {
        try {
            drinkStatus = DrinkStatus.Brewing;
            if (drinkType == DrinkType.Tea) {
                Thread.sleep(brewTimeTea);
            } else if (drinkType == DrinkType.Coffee) {
                Thread.sleep(brewTimeCoffee);
            }
            drinkStatus = DrinkStatus.Trayed;
        } catch (InterruptedException e) {}
    }

}
