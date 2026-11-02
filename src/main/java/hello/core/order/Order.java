package hello.core.order;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/*
* 주문 결과
* */

@Getter
@ToString
@AllArgsConstructor
public class Order {

    private Long memberId;
    private String itemName;
    private int itemPrice;
    private int discountPrice;

    public int calculatePrice() {
        return itemPrice - discountPrice;
    }
}
