package app;

import app.product.Product;
import app.product.ProductRepository;

import java.util.Scanner;

public class OrderApp {
    public void start() {

        /* sudo 코드
        while() {
            1. 메뉴출력;

            2. 입력받기;

            if (입력값:0){
                장바구니 출력;
            }else if (입력값 :+){
                주문하기 (주문 내역 출력);
            }else if (입력값 : 메뉴선택){

                옵션 입력;

                if(햄버거세트){
                    사이드선택, 옵션;
                    드링크선택, 옵션;
                }

                장바구니의 담기
            }
        }
        */
        Scanner scanner = new Scanner(System.in);

        ProductRepository productRepository = new ProductRepository();
        Product[] products = productRepository.getAllProducts();
        Menu menu = new Menu(products);

        Cart cart = new Cart(productRepository, menu);

        System.out.println("🍔 BurgerQueen Order Service");

        while (true) {

            menu.printMenu();
            String input = scanner.nextLine();

            if(input == "+"){
                //주문내역출력
                break;
            }else{
                int menuNumber = Integer.parseInt(input);

                if (menuNumber == 0) cart.printCart();
                else if (1 <= menuNumber && menuNumber <= products.length) cart.addToCart(menuNumber);
            }

        }

    }
}
