package app;

import app.product.Product;
import app.product.ProductRepository;
import app.product.subproduct.BurgerSet;
import app.product.subproduct.Drink;
import app.product.subproduct.Hamburger;
import app.product.subproduct.Side;

import java.awt.*;
import java.util.Scanner;

public class Cart {
    private Product[] items = new Product[0];
    private Scanner scanner = new Scanner(System.in);

    private ProductRepository productRepository;

    private Menu menu;

    public Cart(ProductRepository productRepository, Menu menu) {
        this.productRepository = productRepository;
        this.menu = menu;
    }

    public void printCart(){
        System.out.println("๐งบ ์ฅ๋ฐ๊ตฌ๋");
        System.out.println("-".repeat(60));

        printCartItemDetails();

        System.out.println("-".repeat(60));
        //System.out.printf("ํฉ๊ณ : %d์\n", ๊ธ์ก ํฉ๊ณ);

        System.out.println("์ด์ ์ผ๋ก ๋์๊ฐ๋ ค๋ฉด ์ํฐ๋ฅผ ๋๋ฅด์ธ์. ");
        scanner.nextLine();
    }

    protected void printCartItemDetails(){
        for(Product product : items){
            if(product instanceof BurgerSet){
                BurgerSet burgerSet = (BurgerSet) product;
                System.out.printf(
                        " %s %6d์ (%s(์ผ์ฒฉ %d๊ฐ), %s(๋นจ๋ %s))\n",
                        product.getName(),
                        product.getPrice(),
                        burgerSet.getSide().getName(),
                        burgerSet.getSide().getKetchup(),
                        burgerSet.getDrink().getName(),
                        burgerSet.getDrink().hasStraw() ? "์์" : "์์"
                );
            }
            else if(product instanceof Hamburger){
                System.out.printf(
                        " %s %6d์ (%s)\n",
                        product.getName(),
                        product.getPrice(),
                        "๋จํ"
                );
            }else if(product instanceof Side){
                System.out.printf(
                        " %s %6d์ (์ผ์ฒฉ %d๊ฐ)\n",
                        product.getName(),
                        product.getPrice(),
                        ((Side) product).getKetchup()
                );
            }else if(product instanceof Drink){
                System.out.printf(
                        " %s %6d์ (๋นจ๋ %s)\n",
                        product.getName(),
                        product.getPrice(),
                        ((Drink) product).hasStraw() ? "์์" : "์์"
                );
            }
        }
    }

    protected int calculateTotalPrice(){

        int totalPrice=0;

        for(Product product : items){
            totalPrice += product.getPrice();
        }
        return totalPrice;
    }

    public void addToCart(int productId) {

        Product product = productRepository.findById(productId);

        Product newProduct;
        if (product instanceof Hamburger) newProduct = new Hamburger((Hamburger) product );
        else if (product instanceof Side) newProduct = new Side((Side) product);
        else newProduct = new Drink((Drink) product);

        chooseOption(newProduct);

        if (newProduct instanceof Hamburger) {
            Hamburger hamburger = (Hamburger) newProduct;
            if (hamburger.isBurgerSet()) newProduct = composeSet(hamburger);
        }

        Product[] newItems = new Product[items.length+1];
        System.arraycopy(items, 0, newItems, 0, items.length);
        newItems[newItems.length - 1] = newProduct;
        items = newItems;

        System.out.printf("[๐ฃ] %s๋ฅผ(์) ์ฅ๋ฐ๊ตฌ๋์ ๋ด์์ต๋๋ค.\n", product.getName());

    }

    private void chooseOption(Product product){

        String input;

        // ํ๋ฒ๊ฑฐ๋ผ๋ฉด ๋จํ or ์ธํธ ์ ํ
        if(product instanceof Hamburger){
            System.out.printf(
                    "๋จํ์ผ๋ก ์ฃผ๋ฌธํ์๊ฒ ์ด์? (1)_๋จํ(%d์) (2)_์ธํธ(%d์)\n",
                    product.getPrice(),
                    ((Hamburger) product).getBurgerSetPrice()
            );
            input = scanner.nextLine();
            if(input.equals("2")) ((Hamburger) product).setISBurgerSet(true);
        }else if(product instanceof Side){

            System.out.println("์ผ์ฒฉ์ ๋ช๊ฐ๊ฐ ํ์ํ์ ๊ฐ์?");
            input = scanner.nextLine();
            ((Side) product).setKetchup(Integer.parseInt(input));

        }else if(product instanceof Drink){

            System.out.println("๋นจ๋๊ฐ ํ์ ํ์ ๊ฐ์? (1)_์ (2)_์๋์ค");
            input = scanner.nextLine();
            if(input.equals("2")) ((Drink) product).setHasStraw(false);

        }

    }

    private BurgerSet composeSet(Hamburger hamburger){

        System.out.println("์ฌ์ด๋๋ฅผ ๊ณจ๋ผ์ฃผ์ธ์");
        menu.printSides(false);

        String sideId= scanner.nextLine();
        Side side = (Side) productRepository.findById(Integer.parseInt(sideId));
        Side newSide = new Side(side);
        chooseOption(newSide);

        System.out.println("์๋ฃ๋ฅผ ๊ณจ๋ผ์ฃผ์ธ์.");
        menu.printDrinks(false);

        String drinkId = scanner.nextLine();
        Drink drink = (Drink) productRepository.findById(Integer.parseInt(drinkId));
        Drink newDrink = new Drink(drink);
        chooseOption(newDrink);

        String name = hamburger.getName()+"์ธํธ";
        int price = hamburger.getBurgerSetPrice();
        int kcal = hamburger.getKcal() + side.getKcal() + drink.getKcal();

        return new BurgerSet(name, price, kcal, hamburger, newSide, newDrink);
        // ์ฌ์ด๋ ๋ฉ๋ด ์ถ๋ ฅ
        // ์ฌ์ด๋๋ฉ๋ด ์๋ ฅ
        // ์ผ์ฒฉ ๊ฐฏ์ ์๋ ฅ (์ต์)
        // ์๋ฃ ๋ฉ๋ด ์ถ๋ ฅ
        // ์๋ฃ ์๋ ฅ
        // ๋นจ๋ ์ฌ๋ถ ํ์ธ (์ต์)
        // ๋ฒ๊ฑฐ์๋ฐํ
    }


}
