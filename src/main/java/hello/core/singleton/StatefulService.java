package hello.core.singleton;

public class StatefulService {

    private int price; // X 모든 요청이 이 필드 하나를 공유

    public void order(String name, int price) {
        System.out.println("name = " + name + " price = " + price);
        this.price = price;
    }

    public int getPrice() {
        return price;
    }

    // ✅ 올바른 방법: 필드에 저장하지 않고 바로 돌려준다
    public int orderStateless(String name, int price) {
        System.out.println("name = " + name + " price = " + price);
        return price;
    }
}
