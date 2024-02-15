package tinfo.project.tinfo482.service;


import lombok.extern.slf4j.Slf4j;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import tinfo.project.tinfo482.entity.inventory.Acc;
import tinfo.project.tinfo482.entity.inventory.CompleteItem;
import tinfo.project.tinfo482.entity.inventory.Flower;
import tinfo.project.tinfo482.entity.inventory.Item;
import tinfo.project.tinfo482.exceptions.DataNotFoundException;
import tinfo.project.tinfo482.repo.inventory.AccRepository;
import tinfo.project.tinfo482.repo.inventory.CompleteItemRepository;
import tinfo.project.tinfo482.repo.inventory.FlowerRepository;
import tinfo.project.tinfo482.repo.inventory.ItemRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@SpringBootTest
@ActiveProfiles("test")
@Slf4j
class ItemServiceTest {

    @Autowired
    private ItemService itemService;

    @Autowired
    private AccRepository accRepository;
    @Autowired
    private FlowerRepository flowerRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private CompleteItemRepository completeItemRepository;






//    @BeforeEach
//    public void setUp(){
//        if(itemRepository.findAll().size()==0){
//            generator();
//        }
//    }

    private void generator(){
        Acc acc1 = Acc.builder().content("content").price(1l).stock(10).name("acc1").build();
        Flower flower1 = Flower.builder().category("category1").content("content").price(1l).stock(10).name("flower1").build();
        completeItemRepository.save(CompleteItem.builder().acc(acc1).flower(flower1).build());

        Acc acc2 = Acc.builder().content("content").price(1l).stock(10).name("acc1").build();
        Flower flower2 = Flower.builder().category("category1").content("content").price(1l).stock(10).name("flower1").build();
        completeItemRepository.save(CompleteItem.builder().acc(acc2).flower(flower2).build());
    }


    @Test
    @DisplayName("single_fetch_test")
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
    @DisplayName("multi_fetch_test")
    public void multi_fetch_test(){
            List<Item> targets = itemRepository.findAll();
            // class name will be the specific Item category(Flower, Acc)
            targets.stream().forEach((e)->{
                log.info(e.getClass().getSimpleName());
            });


    }

    @Test
    @DisplayName("only_fetch_flowers")
    public void only_fetch_flowers(){
        List<Item> targets = itemRepository.findAllByITEM_CAT("acc_indicator");
        targets.stream().forEach((e)->{
            log.info( e.getName());
            log.info(   e.getClass().getSimpleName());
        });

    }

    @Test
    @DisplayName("only fetch flowers without custom query")
    public void only_fetch_flowers_without_custom_query(){
        List<Flower> flowers = flowerRepository.findAll();
        flowers.stream().forEach((e)->{
            log.info(e.getPrice().toString());
            log.info(String.valueOf(e.getStock()));
        });
    }

    @Test
    @DisplayName("relate flower to recommended accs")
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
    @DisplayName("get all complete item with recommended accs list")
    public void get_all_completeItem_from_flower(){
        Flower flower = flowerRepository.findById(2l).orElse(null);

        flower.getCompleteItemList().stream().forEach((e)->{
            log.info(e.getAcc().getName());
        });
    }

    @Test
    @DisplayName("Delete target flower ")
    public void deleteTargetFlower_test(){
        try {
            itemService.deleteTargetFlower(2l);
        } catch (DataNotFoundException e) {
           e.printStackTrace();
        }

    }

    @Test
    @DisplayName("Delete target Acc")
    @Transactional
    public void deleteTargetAcc() throws DataNotFoundException {
//        Long acc_id = 3l;
//
//        //    List<CompleteItem> targets = completeItemRepository.findAllByAcc_Id(acc_id);
//        Acc target = accRepository.findById(acc_id).orElseThrow(()-> new DataNotFoundException("flower not found"));
//        log.info("targetAcc = "+target.getName());
//        // CompleteItem only with assigned acc_id
//        // remove relationship by removing child's pk of parent
//        // accesss child from parent's
//        target.getCompleteItemList().stream().forEach(e->e.removeAcc());
//
//        accRepository.save(target);
//
//        completeItemRepository.findAll().stream().forEach(e->{
//            if(e.getAcc()!=null)
//            log.info(e.getAcc().getName());});

        itemService.deleteTargetAcc(1l);

    }

    @Test
    @DisplayName("Delete relationship of all Acc from target flower")
    @Transactional
    public void remove_all_acc_from_flower_test(){
        try {
            itemService.remove_all_acc_from_flower(2l);
            log.info("markkkkk");



            Assertions.assertEquals(new ArrayList<>(),

                    completeItemRepository.findAllByFlower_Id(2l).stream()
                            .filter(e-> e.getAcc() !=null)
                            .map(e->{
//                        if(e.getAcc() !=null){
                            log.info("acc:: "+e.getAcc().getName());
                            return e.getAcc();
//                        }
//                        else {log.info("acc empty");
//                            return null;}

                    }).collect(Collectors.toList())

                    );
        } catch (DataNotFoundException e) {
            e.printStackTrace();
        }

    }


    @Test
    @DisplayName("Completely delete flower")
    @Transactional
    public void complete_remove_flower_test(){
        log.info("Chekc target Flower initial: "+flowerRepository.findById(2l).map(Flower::getName).orElse(null));
        try {
            itemService.complete_remove_flower(2l);

           log.info("Chekc target Flower deleted(if null, it's good): "+flowerRepository.findById(2l).map(Flower::toString).orElse(null));

        } catch (DataNotFoundException e) {
            e.printStackTrace();
        }


    }
}