package tinfo.project.tinfo482.repo.inventory;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import tinfo.project.tinfo482.entity.inventory.Item;

import java.util.List;
import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, Long> {


    // inheritance 어노테이션이 붙은 객체는 자식까지 다 싸잡아서 끌어와야 한다
    @Query(value = "SELECT i.*, f.* FROM Item i \n" +
            "left join Flower f on i.id=f.id \n" +
            "left join Acc a on i.id = a.id\n" +
            "WHERE ITEM_CAT = 'flower_indicator';" , nativeQuery = true)
   List<Item> findAllByITEM_CAT(String indicator);
}
