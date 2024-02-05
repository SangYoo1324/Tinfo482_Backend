package tinfo.project.tinfo482.repo.inventory;


import org.springframework.data.jpa.repository.JpaRepository;
import tinfo.project.tinfo482.entity.inventory.CompleteItem;

import java.util.List;
import java.util.Optional;

public interface CompleteItemRepository extends JpaRepository<CompleteItem, Long> {

   List<CompleteItem>deleteAllByFlower_Content(String content);

   List<CompleteItem> findAllByFlower_Id(Long flower_id);
   List<CompleteItem> findAllByAcc_Id(Long acc_id);
}
