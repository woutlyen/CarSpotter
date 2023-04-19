package be.kuleuven.gt.javabean.model;

public class CoffeeOrder {
    private String name;
    private String coffee;
    private boolean sugar;
    private boolean whipCream;
    private int quantity;

    public CoffeeOrder(String name, String coffee, boolean sugar, boolean whipCream, int quantity) {
        this.name = name;
        this.coffee = coffee;
        this.sugar = sugar;
        this.whipCream = whipCream;
        this.quantity = quantity;
    }

    public String getName() {
        return name;
    }

    public String getCoffee() {
        return coffee;
    }

    public boolean isSugar() {
        return sugar;
    }

    public boolean isWhipCream() {
        return whipCream;
    }

    public int getQuantity() {
        return quantity;
    }

}
