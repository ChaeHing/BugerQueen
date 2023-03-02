package app.product.subproduct;
import app.product.*;

public class Drink extends Product {
    private boolean Straw;

    public Drink(int id, String name, int price, int kcal, boolean straw) {
        super(id, name, price, kcal);
        Straw = straw;
    }

    public boolean isStraw() {
        return Straw;
    }

    public void setStraw(boolean straw) {
        Straw = straw;
    }
}
