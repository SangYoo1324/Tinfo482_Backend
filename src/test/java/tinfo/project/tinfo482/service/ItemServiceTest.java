package tinfo.project.tinfo482.service;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import tinfo.project.tinfo482.entity.inventory.Acc;
import tinfo.project.tinfo482.entity.inventory.CompleteItem;
import tinfo.project.tinfo482.entity.inventory.Flower;
import tinfo.project.tinfo482.entity.inventory.Item;
import tinfo.project.tinfo482.exceptions.DataNotFoundException;
import tinfo.project.tinfo482.repo.inventory.AccRepository;
import tinfo.project.tinfo482.repo.inventory.CompleteItemRepository;
import tinfo.project.tinfo482.repo.inventory.FlowerRepository;
import tinfo.project.tinfo482.repo.inventory.ItemRepository;

import java.util.List;



@SpringBootTest
@ActiveProfiles("test")
@Slf4j
class ItemServiceTest {

    @Autowired
    AccRepository accRepository;
    @Autowired
    FlowerRepository flowerRepository;
    @Autowired
    ItemRepository itemRepository;
    @Autowired
    CompleteItemRepository completeItemRepository;


    @BeforeEach
    public void setUp(){

    }

    @Test
    public void create_test(){
       Acc acc1 = Acc.builder().content("content").price(1l).stock(10).name("acc1").build();
       Flower flower = Flower.builder().category("category1").content("content").price(1l).stock(10).name("flower1").build();

       accRepository.save(acc1);
       flowerRepository.save(flower);



    }
    @Test
    public void single_fetch_test(){
        // getting single Item
        try {
            Item target = itemRepository.findById(1l).orElseThrow(()-> new DataNotFoundException("Data not Found for Acc..."));
            // class name will be the specific Item category(Flower, Acc)
            log.info(target.getClass().getSimpleName());

        } catch (DataNotFoundException e) {
            e.printStackTrace();
        }
        // always execute even with error situation
        finally {

        }
    }

    @Test
    public void multi_fetch_test(){
            List<Item> targets = itemRepository.findAll();
            // class name will be the specific Item category(Flower, Acc)
            targets.stream().forEach((e)->{
                log.info(e.getClass().getSimpleName());
            });


    }

    @Test
    public void only_fetch_flowers(){
        List<Item> targets = itemRepository.findAllByITEM_CAT("acc_indicator");
        targets.stream().forEach((e)->{
            log.info( e.getName());
            log.info(   e.getClass().getSimpleName());
        });

    }

    @Test
    public void only_fetch_flowers_without_custom_query(){
        List<Flower> flowers = flowerRepository.findAll();
        flowers.stream().forEach((e)->{
            log.info(e.getPrice().toString());
            log.info(String.valueOf(e.getStock()));
        });
    }

    @Test
    public void relate_flower_item(){
        Acc acc1 = Acc.builder().content("content").price(1l).stock(10).name("acc1").build();
        Flower flower = Flower.builder().category("category1").content("content").price(1l).stock(10).name("flower1").build();

        CompleteItem completeItem = CompleteItem.builder().acc(acc1).flower(flower).build();
        completeItemRepository.save(completeItem);


        // flower은 flower 클래스로 받아와진다
        CompleteItem item = completeItemRepository.findById(1l).orElse(null);
        log.info(item.getFlower().getName());
        log.info(item.getFlower().getClass().getSimpleName());
    }

    @Test
    public void get_all_completeItem_from_flower(){
        Flower flower = flowerRepository.findById(2l).orElse(null);

        flower.getCompleteItemList().stream().forEach((e)->{
            log.info(e.getAcc().getName());
        });
    }
}