package tinfo.project.tinfo482;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;
import tinfo.project.tinfo482.entity.inventory.Acc;
import tinfo.project.tinfo482.entity.inventory.CompleteItem;
import tinfo.project.tinfo482.entity.inventory.Flower;
import tinfo.project.tinfo482.repo.inventory.AccRepository;
import tinfo.project.tinfo482.repo.inventory.CompleteItemRepository;
import tinfo.project.tinfo482.repo.inventory.FlowerRepository;
import tinfo.project.tinfo482.repo.inventory.ItemRepository;
import tinfo.project.tinfo482.service.ItemService;

@SpringBootTest
@Slf4j
class Tinfo482ApplicationTests {
	@Autowired
	AccRepository accRepository;
	@Autowired
	FlowerRepository flowerRepository;
	@Autowired
	ItemRepository itemRepository;
	@Autowired
	CompleteItemRepository completeItemRepository;

	@Autowired
	ItemService itemService;


	@Test
	@DisplayName("Generate sample item for the real server")
	@Disabled
	void contextLoads() {
		if(itemRepository.findAll().size()==0){
			generator();
		}
		else{
			completeItemRepository.deleteAllInBatch();
			flowerRepository.deleteAllInBatch();
			accRepository.deleteAllInBatch();
			itemRepository.deleteAllInBatch();

			generator();

		}
	}
	private  void generator(){
		Acc acc1 = Acc.builder().content("content").price(1l).stock(10).name("acc1").build();
		Flower flower1 = Flower.builder().category("category1").content("content").price(1l).stock(10).name("flower1").build();
		completeItemRepository.save(CompleteItem.builder().acc(acc1).flower(flower1).build());

		Acc acc2 = Acc.builder().content("content").price(1l).stock(10).name("acc1").build();
		Flower flower2 = Flower.builder().category("category1").content("content").price(1l).stock(10).name("flower1").build();
		completeItemRepository.save(CompleteItem.builder().acc(acc2).flower(flower2).build());
	}

}
