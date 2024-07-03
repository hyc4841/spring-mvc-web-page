package hello.itemservice.domain.item;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data // 실무에선 @Data 조심해서 사용해야함.
public class Item {

    private Long id;            // 유저 id
    private String itemName;    // 상품 이름
    private Integer price;      // 상품 가격
    private Integer quantity;   // 상품 수량

    public Item() {
    }

    public Item(String itemName, Integer price, Integer quantity) {
        this.itemName = itemName;
        this.price = price;
        this.quantity = quantity;
    }


}
