package tinfo.project.tinfo482.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import tinfo.project.tinfo482.dto.inventory.AccDto;
import tinfo.project.tinfo482.dto.inventory.CompleteItemDto;
import tinfo.project.tinfo482.dto.inventory.FlowerDto;
import tinfo.project.tinfo482.entity.inventory.Acc;
import tinfo.project.tinfo482.entity.inventory.CompleteItem;
import tinfo.project.tinfo482.entity.inventory.Flower;
import tinfo.project.tinfo482.exceptions.DataNotFoundException;
import tinfo.project.tinfo482.repo.inventory.AccRepository;
import tinfo.project.tinfo482.repo.inventory.CompleteItemRepository;
import tinfo.project.tinfo482.repo.inventory.FlowerRepository;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class ItemService {

    private final FlowerRepository flowerRepository;

    private final AccRepository accRepository;

    private final CompleteItemRepository completeItemRepository;

    private final S3Service s3Service;

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

        return flowerRepository.save(Flower.builder()
                        .category(flowerDto.getCategory())
                        .img_url(img_url)
                        .stock(flowerDto.getStock())
                        .price(flowerDto.getPrice())
                        .name(flowerDto.getName())
                        .content(flowerDto.getContent())
                .build()
        ).toFlowerDto();
    }

    public CompleteItemDto relateAccToFlowerService(Long flower_id, Long acc_id) throws DataNotFoundException {

        CompleteItem completeItem = CompleteItem.builder()
                .flower(flowerRepository.findById(flower_id).orElseThrow(()-> new DataNotFoundException("flower not found")))
                .acc( accRepository.findById(acc_id).orElseThrow(()-> new DataNotFoundException("acc not found")))
                .build();

       completeItemRepository.save(completeItem);

       return completeItem.toCompleteItemDto();

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

        log.info(count.get()+" related completeItem has beenremoved");


        //now we can safely delete flower
        Flower flower = flowerRepository.findById(flower_id).orElseThrow(()-> new DataNotFoundException("flower not found"));
        flowerRepository.delete(flower);

    }

//    public void deleteTargetAcc(Long acc_id){
//        // owenership is on Child table 'CompleteItem'
//
//
//    }
//
//    public CompleteItemDto remove_acc_from_flower(int flower_id){
//
//    }


//    public void List<CompleteItemDto> fetch_all_complete_items(){
//
//    }



}
