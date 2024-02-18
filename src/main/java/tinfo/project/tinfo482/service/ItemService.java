package tinfo.project.tinfo482.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import tinfo.project.tinfo482.dto.inventory.AccDto;
import tinfo.project.tinfo482.dto.inventory.CompleteItemDto;
import tinfo.project.tinfo482.dto.inventory.FlowerDto;
import tinfo.project.tinfo482.entity.inventory.Acc;
import tinfo.project.tinfo482.entity.inventory.CompleteItem;
import tinfo.project.tinfo482.entity.inventory.Flower;
import tinfo.project.tinfo482.exceptions.CacheNotFoundException;
import tinfo.project.tinfo482.exceptions.DataNotFoundException;
import tinfo.project.tinfo482.functionalities.redis.RedisUtilService;
import tinfo.project.tinfo482.repo.inventory.AccRepository;
import tinfo.project.tinfo482.repo.inventory.CompleteItemRepository;
import tinfo.project.tinfo482.repo.inventory.FlowerRepository;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ItemService {

    private final FlowerRepository flowerRepository;

    private final AccRepository accRepository;

    private final CompleteItemRepository completeItemRepository;

    private final S3Service s3Service;

    private final RedisUtilService redisUtilService;

    private String cacheName = "completeItemDto_list";
    private String cacheKey = "all";


    public void generateDummyData(){
        Acc acc1 = Acc.builder().content("content").price(1l).stock(10).name("acc1").build();
        Flower flower1 = Flower.builder().category("category1").content("content").price(1l).stock(10).name("flower1").build();
        completeItemRepository.save(CompleteItem.builder().acc(acc1).flower(flower1).build());

        Acc acc2 = Acc.builder().content("content").price(1l).stock(10).name("acc1").build();
        Flower flower2 = Flower.builder().category("category1").content("content").price(1l).stock(10).name("flower1").build();
        completeItemRepository.save(CompleteItem.builder().acc(acc2).flower(flower2).build());

    }

    public void deleteDummyData(){
        completeItemRepository.deleteAllByFlower_Content("content");
        accRepository.deleteAllByContent("content");
        flowerRepository.deleteAllByContent("content");
    }


    public AccDto postAcc(AccDto accDto, MultipartFile file) throws IOException {

       String img_url =  s3Service.imageUpload(file);
        return accRepository.save(Acc.builder()
                .name(accDto.getName())
                .stock(accDto.getStock())
                .price(accDto.getPrice())
                .content(accDto.getContent())
                .img_url(img_url)
                .build()).toAccDto();
    }

    public FlowerDto postFlower_only_service(FlowerDto flowerDto, MultipartFile file) throws IOException {
       String img_url = s3Service.imageUpload(file);
        FlowerDto finalOutcome = flowerRepository.save(Flower.builder()
                .category(flowerDto.getCategory())
                .img_url(img_url)
                .stock(flowerDto.getStock())
                .price(flowerDto.getPrice())
                .name(flowerDto.getName())
                .delivery(flowerDto.isDelivery())
                .content(flowerDto.getContent())
                .build()
        ).toFlowerDto();

        log.info("updating cache with newly updated Flower");
        // forming dto
        List<CompleteItemDto> data =
                generate_recent_CompleteItemDtoList();
        // register to redisCache
        redisUtilService.registerCache(cacheName,cacheKey, data );


       log.info("deliverable"+ flowerDto.isDelivery());
        return finalOutcome;
    }

    public void relateAccToFlowerService(Long flower_id, Long acc_id) throws DataNotFoundException {

        CompleteItem completeItem = CompleteItem.builder()
                .flower(flowerRepository.findById(flower_id).orElseThrow(()-> new DataNotFoundException("flower not found")))
                .acc( accRepository.findById(acc_id).orElseThrow(()-> new DataNotFoundException("acc not found")))
                .build();

       completeItemRepository.save(completeItem);

        log.info("updating cache with newly linked flower <-> Acc");
        // forming dto
        List<CompleteItemDto> data =
                generate_recent_CompleteItemDtoList();
        // register to redisCache
        redisUtilService.registerCache(cacheName,cacheKey, data );


    }



    //simple deleteTargetAcc.
    // but, ownership is on completeAcc.. So, need to delete from
    public void deleteTargetAcc(Long acc_id) throws DataNotFoundException {

        //    List<CompleteItem> targets = completeItemRepository.findAllByAcc_Id(acc_id);
        Acc target = accRepository.findById(acc_id).orElseThrow(()-> new DataNotFoundException("flower not found"));
        log.info("targetAcc = "+target.getName());
        // CompleteItem only with assigned acc_id
        // remove relationship by removing child's pk of parent
        // accesss child from parent's List<Acc>
        target.getCompleteItemList().stream().forEach(e->e.removeAcc());

//        accRepository.save(target);

        completeItemRepository.findAll().stream().forEach(e->{
            if(e.getAcc()!=null)
                log.info(e.getAcc().getName());});


        accRepository.delete(target);


        //Cache update logic
        List<CompleteItemDto> completeItemDtoList = null;
        try {
            completeItemDtoList = (List<CompleteItemDto>) redisUtilService.fetchingCache(cacheName, cacheKey);

            completeItemDtoList.stream().forEach(completeItemDto->{
                // filter deleted acc_id
                completeItemDto.getAccDto().stream().filter(accDto -> accDto.getId() !=acc_id).collect(Collectors.toList());
            });


        } catch (CacheNotFoundException e) {
            e.printStackTrace();
            log.info("cache doesn't exist. Generating CompleteItemDto_list from DB");
            completeItemDtoList = generate_recent_CompleteItemDtoList();

        }
        finally{
            if(completeItemDtoList !=null)
            redisUtilService.registerCache(cacheName,cacheKey,completeItemDtoList);
        }

    }

    //completely remove flower
    public void complete_remove_flower(Long flower_id) throws DataNotFoundException {
        Flower target = flowerRepository.findById(flower_id).orElseThrow(()->new DataNotFoundException("flower not found"));

        // 1. get completeItem List from target flower
        List<CompleteItem> completeItemList = target.getCompleteItemList();
        // 2. null all the acc relationship
        completeItemList.forEach(CompleteItem::removeAcc);
        // 3. clear the List<CompleteItem> = remove relationship to completeItem
        completeItemList.clear();
        flowerRepository.save(target);
        // 4. remove the target flower
        flowerRepository.delete(target);
    }

    public void deleteTargetFlower(Long flower_id) throws DataNotFoundException {

        //ownership is on Child table 'CompleteItem'
        List<CompleteItem> targets = completeItemRepository.findAllByFlower_Id(flower_id);
        AtomicInteger count = new AtomicInteger(0);
        targets.stream().forEach((t)->{
            completeItemRepository.delete(t);
            count.incrementAndGet();
//        count++;
        });

        log.info(count.get()+" related completeItem has been removed");


        //now we can safely delete flower
        Flower flower = flowerRepository.findById(flower_id).orElseThrow(()-> new DataNotFoundException("flower not found"));
        flowerRepository.delete(flower);

    }



    public void remove_all_acc_from_flower(Long flower_id) throws DataNotFoundException {
    // acc is not directely related with Flower

        // 1. get completeItem List from target flower
        Flower flower = flowerRepository.findById(flower_id).orElseThrow(()->new DataNotFoundException("flower not found"));
        List<CompleteItem> completeItems_with_flower =flower.getCompleteItemList();

        // 2. set null for alll Accs
        completeItems_with_flower.stream().forEach(e->{
            e.removeAcc();
        });


    }







    //Fetching all Flowers(not completeItem)
    public List<FlowerDto> fetch_all_flowers(){
       return flowerRepository.findAll().stream().map(Flower::toFlowerDto).collect(Collectors.toList());
    }


    // Fethcing all Accs
    public List<AccDto> fetch_all_accs(){
        return accRepository.findAll().stream().map(Acc::toAccDto).collect(Collectors.toList());
    }

    //Fetching target Acc
    public AccDto fetch_target_acc(Long acc_id) throws DataNotFoundException {
        return accRepository.findById(acc_id).orElseThrow(()->new DataNotFoundException("cannot find Acc")).toAccDto();
    }

    //Fetching target CompleteItem
    public CompleteItemDto fetch_target_item(Long flower_id) throws DataNotFoundException{
       Flower target= flowerRepository.findById(flower_id).orElseThrow(()->new DataNotFoundException("cannot find Acc"));


       return CompleteItemDto
               .builder()
               .flowerDto(target.toFlowerDto())
               .accDto(target.getCompleteItemList()
                       .stream().filter(flower->flower.getAcc() != null)
                       .collect(Collectors.toList())
                       .stream().map((fl)->fl.getAcc().toAccDto()).collect(Collectors.toList())
               )
               .build();

    }



    public List<CompleteItemDto> fetch_all_complete_items(){



        try {
            // search Cache
           return (List<CompleteItemDto>) redisUtilService.fetchingCache(cacheName, cacheKey);
        } catch (CacheNotFoundException e) {
            e.printStackTrace();
            log.info("cache not found. searching DB");
            // forming dto
            List<CompleteItemDto> data =
                    generate_recent_CompleteItemDtoList();
            // register to redisCache
            redisUtilService.registerCache(cacheName,cacheKey, data );

            return data;
        }






    }


    // generate_recentCompleteItemDtoList with DB(Not from Cache)
    private List<CompleteItemDto> generate_recent_CompleteItemDtoList(){
        return  flowerRepository.findAll().stream().map(flower->
                CompleteItemDto.builder()
                        .flowerDto(flower.toFlowerDto())
                        .accDto(
                                // extract acc Entities from completeItem -> toAccDto
                                flower.getCompleteItemList().stream()
                                        .map(completeItem->
                                        Optional.ofNullable(completeItem.getAcc())
                                                .map(Acc::toAccDto).orElse(null)
                                ).filter(Objects::nonNull)

                                        .collect(Collectors.toList())
                        )
                        .build()
        ).collect(Collectors.toList());
    }





}
