package hello.itemservice.domain.item;

import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class ItemRepository {

    private static final Map<Long, Item> store = new HashMap<>(); // static. 실무에선 HashMap사용안함. 동시성 문제 concurrentHashMap
    private static long sequence = 0L;

    // 아이템 저장
    public Item save(Item item) {
        item.setId(++sequence); // 아이템 순번 올려주고
        store.put(item.getId(), item); // 저장소에 아이템 넣어주기

        return item; // 방금 저장한 아이템 반환
    }

    // 아이템 찾기. 조회
    public Item findById(Long id) {
        // 아이템 id로 찾기
        return store.get(id);
    }

    // 아이템 전체 조회
    public List<Item> findAll() {
        return new ArrayList<>(store.values());
    }

    // 아이템 수정. 업데이트
    public void update(Long itemId, Item updateParam) {
        Item findItem = findById(itemId);
        findItem.setItemName(updateParam.getItemName());
        findItem.setPrice(updateParam.getPrice());
        findItem.setQuantity(updateParam.getQuantity());
    }

    // 저장소 다 날리기
    public void clearStore() {
        store.clear();
    }


}