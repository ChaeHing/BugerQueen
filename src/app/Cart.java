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
        System.out.println("🧺 장바구니");
        System.out.println("-".repeat(60));

        printCartItemDetails();

        System.out.println("-".repeat(60));
        //System.out.printf("합계 : %d원\n", 금액 합계);

        System.out.println("이전으로 돌아가려면 엔터를 누르세요. ");
        scanner.nextLine();
    }

    private void printCartItemDetails(){
        for(Product product : items){
            if(product instanceof BurgerSet){
                BurgerSet burgerSet = (BurgerSet) product;
                System.out.printf(
                        " %s %6d원 (%s(케첩 %d개), %s(빨대 %s))\n",
                        product.getName(),
                        product.getPrice(),
                        burgerSet.getSide().getName(),
                        burgerSet.getSide().getKetchup(),
                        burgerSet.getDrink().getName(),
                        burgerSet.getDrink().isStraw() ? "있음" : "없음"
                );
            }
            else if(product instanceof Hamburger){
                System.out.printf(
                        " %s %6d원 (%s)\n",
                        product.getName(),
                        product.getPrice(),
                        "단품"
                );
            }else if(product instanceof Side){
                System.out.printf(
                        " %s %6d원 (케첩 %d개)\n",
                        product.getName(),
                        product.getPrice(),
                        ((Side) product).getKetchup()
                );
            }else if(product instanceof Drink){
                System.out.printf(
                        " %s %6d원 (빨대 %s)\n",
                        product.getName(),
                        product.getPrice(),
                        ((Drink) product).isStraw() ? "있음" : "없음"
                );
            }
        }
    }

    private int calculateTotalPrice(){

        int totalPrice=0;

        for(Product product : items){
            totalPrice += product.getPrice();
        }
        return totalPrice;
    }

    public void addToCart(int productId) {

        Product product = productRepository.findById(productId);
        chooseOption(product);

        if (product instanceof Hamburger) {
            Hamburger hamburger = (Hamburger) product;
            if (hamburger.isBurgerSet()) product = compseset(hamburger);
        }

        Product[] newItems = new Product[items.length+1];
        System.arraycopy(items, 0, newItems, 0, items.length);
        newItems[newItems.length - 1] = product;
        items = newItems;

        System.out.printf("[📣] %s를(을) 장바구니에 담았습니다.\n", product.getName());

    }

    private void chooseOption(Product product){

        String input;

        // 햄버거라면 단품 or 세트 선택
        if(product instanceof Hamburger){
            System.out.printf(
                    "단품으로 주문하시겠어요? (1)_단품(%d원) (2)_세트(%d원)\n",
                    product.getPrice(),
                    ((Hamburger) product).getBurgerSetPrice()
            );
            input = scanner.nextLine();
            if(input.equals("2")) ((Hamburger) product).setBurgerSet(true);
        }else if(product instanceof Side){

            System.out.println("케첩은 몇개가 필요하신가요?");
            input = scanner.nextLine();
            ((Side) product).setKetchup(Integer.parseInt(input));

        }else if(product instanceof Drink){

            System.out.println("빨대가 필요 하신가요? (1)_예 (2)_아니오");
            input = scanner.nextLine();
            if(input.equals("2")) ((Drink) product).setStraw(true);

        }

    }

    private BurgerSet compseset(Hamburger hamburger){


        System.out.println("사이드를 골라주세요");
        menu.printSides(false);

        String sideId= scanner.nextLine();
        Side side = (Side) productRepository.findById(Integer.parseInt(sideId));
        chooseOption(side);

        System.out.println("음료를 골라주세요.");
        menu.printDrinks(false);

        String drinkId = scanner.nextLine();
        Drink drink = (Drink) productRepository.findById(Integer.parseInt(drinkId));
        chooseOption(drink);

        String name = hamburger.getName()+"세트";
        int price = hamburger.getBurgerSetPrice();
        int kcal = hamburger.getKcal() + side.getKcal() + drink.getKcal();

        return new BurgerSet(name, price, kcal, hamburger, side, drink);
        // 사이드 메뉴 출력
        // 사이드메뉴 입력
        // 케첩 갯수 입력 (옵션)
        // 음료 메뉴 출력
        // 음료 입력
        // 빨대 여부 확인 (옵션)
        // 버거셋반환
    }


}
