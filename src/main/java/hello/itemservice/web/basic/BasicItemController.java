package hello.itemservice.web.basic;

import hello.itemservice.domain.item.Item;
import hello.itemservice.domain.item.ItemRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/basic/items")
@RequiredArgsConstructor // 이렇게 하면 final 붙은 변수 생성자 만들어줌. 또한 생성자가 하나만 있는 경우 스프링이 알아서 @Autowired 붙여서 빈에 등록해준다.
public class BasicItemController {

    private final ItemRepository itemRepository; // 알아서 의존관계 주입해준다.

    @GetMapping // 상품 목록
    public String items(Model model) {
        List<Item> items = itemRepository.findAll();
        model.addAttribute("items", items); // 뷰에서 뿌릴 데이터를 이런식으로 전달한다.
        // item에 대한 데이터는 id, 상품 이름, 가격, 수량 등이다.
        return "basic/items";
    }

    @GetMapping("/{itemId}") // 상품 상세
    public String item(@PathVariable Long itemId, Model model) {
        Item item = itemRepository.findById(itemId);
        model.addAttribute("item", item);
        return "basic/item";
    }

    @GetMapping("/add")
    public String addForm() {
        return "basic/addForm";
    }

    // @PostMapping("/add") // 상품 등록
    public String addItemV1(@RequestParam String itemName, @RequestParam int price, @RequestParam Integer quantity, Model model) {
        Item item = new Item();
        item.setItemName(itemName);
        item.setPrice(price);
        item.setQuantity(quantity);

        itemRepository.save(item);

        model.addAttribute("item", item);
        return "basic/item"; // post로 호출된 함수를 실행하고 내부 로직 처리하고 이 url로 데이터를 실어서 보낸다.
    }

    /**
     * @ModelAttribute("item") Item item
     * model.addAttribute("item", item); 자동 추가
     * @ModelAttribute는 Item 객체를 생성하고, 요청 파라미터의 값을 프로퍼티 접근법(setXxx)으로 입력해준다.
     */
//    @PostMapping("/add") // 상품 등록
    public String addItemV2(@ModelAttribute("item") Item item, Model model) {

        itemRepository.save(item);
        // model.addAttribute("item", item); @ModelAttribute를 사용하면 자동으로 보내준다.

        return "basic/item"; // post로 호출된 함수를 실행하고 내부 로직 처리하고 이 url로 데이터를 실어서 보낸다.
    }

    /**
     * @ModelAttribute name 생략 가능
     * model.addAttribute(item); 자동 추가, 생략 가능
     * 생략시 model에 저장되는 name은 클래스명 첫글자만 소문자로 등록 Item -> item
     */
//    @PostMapping("/add") // 상품 등록
    public String addItemV3(@ModelAttribute Item item) { // @ModelAttribute 이름 생략하고 model에 자동으로 보내준다. 이때 이름은 클래스 이름의 첫글자를 소문자로 바꿔 알아서 이름 지어줌
        itemRepository.save(item);
        return "basic/item";
    }

    /**
     * @ModelAttribute 자체 생략 가능
     * model.addAttribute(item) 자동 추가
     * 이제는 하다하다 @ModelAttribute도 생략함. 이래서 스프링이 편하다. 스프링이 알아서 보고 판단해서 @ModelAttribute를 붙여준다.
     */
//    @PostMapping("/add") // 상품 등록
    public String addItemV4(Item item) {
        itemRepository.save(item);
        return "basic/item"; // 이 코드는 최종적으로 큰 문제가 있음. 등록하고 나서 새로고침 누르면 또 똑같은 상품이 등록된다.
        // 새로고침의 원리 자체가 마지막으로 보냈던 데이터를 다시 보내는 것이기 때문.
    }

    /**
     * PRG - Post/Redirect/Get
     */
//    @PostMapping("/add")
    public String addItemV5(Item item) {
        itemRepository.save(item);
        return "redirect:/basic/items/" + item.getId(); // item을 저장하고 나서 리다이랙트로 get /item/{id} 가 되도록 만들어준다.
        // 그런데 이 방법도 위험하다고함. url에 item.getId()처럼 변수를 사용하는건 url 인코딩이 안된다나 뭐라나
    }

    /**
     * RedirectAttributes
     * pathVariable 바인딩: `{itemId}`
     * 쿼리 파라미터로 처리: `?status=true`
     */
    @PostMapping("/add")
    public String addItemV6(Item item, RedirectAttributes redirectAttributes) {
        Item savedItem = itemRepository.save(item);
        redirectAttributes.addAttribute("itemId", savedItem.getId()); // redirectAttributes로 Pathvariable 처리
        redirectAttributes.addAttribute("status", true); // redirectAttributes로 쿼리 파라미터 처리. ?status=true
        return "redirect:/basic/items/{itemId}";
    }


    @GetMapping("/{itemId}/edit") // 상품 수정 폼 보여주기
    public String editForm(@PathVariable Long itemId, Model model) {
        Item item = itemRepository.findById(itemId);
        model.addAttribute("item", item);
        return "basic/editForm";
        // 상품 수정 url을 get으로 호출하면 해당 상품 찾아서 model에 데이터 넣어줘서 뷰에서 뿌려서 보여줌
    }

    @PostMapping("/{itemId}/edit") // 실제 상품의 수정이 이루어 지는 부분
    public String edit(@PathVariable Long itemId, @ModelAttribute Item item) {
        itemRepository.update(itemId, item);
        return "redirect:/basic/items/{itemId}";
    }


    /**
     * 테스트용 데이터 추가
     */
    @PostConstruct
    public void init() { // 의존 관계가 주입되고 나서 초기화 용으로 호출. 데이터가 없으면 테스트를 할 수가 없기 때문에 넣어준다.
        itemRepository.save(new Item("itemA", 10000, 10));
        itemRepository.save(new Item("itemB", 20000, 20));
    }

}
