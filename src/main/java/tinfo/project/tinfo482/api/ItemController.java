package tinfo.project.tinfo482.api;

import com.nimbusds.jose.shaded.gson.Gson;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tinfo.project.tinfo482.dto.ErrorDto;
import tinfo.project.tinfo482.dto.inventory.AccDto;
import tinfo.project.tinfo482.dto.inventory.FlowerDto;
import tinfo.project.tinfo482.exceptions.DataNotFoundException;
import tinfo.project.tinfo482.service.ItemService;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api")
public class ItemController {

    private final ItemService itemService;

    //Fetching Flowers


    //Fetching target Flower

    //Fetching target Accs


    //Fetching All-Items


    //Posting Acc
    @PostMapping("/acc")
    public ResponseEntity<?> postAcc(@RequestParam("accDto") String accDto,@RequestParam("file") MultipartFile file){

        log.info("JsonString:::"+accDto);

        Gson gson = new Gson();
        AccDto mappedDto  = gson.fromJson(accDto, AccDto.class);

        log.info("mapped to:::"+mappedDto.toString());

        try {
            return ResponseEntity.status(HttpStatus.OK).body(itemService.postAcc(mappedDto, file));
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ErrorDto.builder().errorCode(500).description("file_upoad error"));
        }
    }
    // Posting Flower without pairing Acc recommendation
    @PostMapping("/flower")
    public ResponseEntity<?> postFlower_only(@RequestParam("flowerDto") String accDto,@RequestParam("file") MultipartFile file){
        log.info("JsonString:::"+accDto);
        Gson gson = new Gson();
        FlowerDto mappedDto  = gson.fromJson(accDto, FlowerDto.class);
        log.info("mapped to:::"+mappedDto.toString());

        try {
            return ResponseEntity.status(HttpStatus.OK).body(itemService.postFlower_only_service(mappedDto,file));
        } catch (IOException e) {
            e.printStackTrace();
           return  ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ErrorDto.builder().errorCode(500).description("file_upload error").build());
        }
    }

    //relate Flower pairing with recommendation Acc
    @PostMapping("/flower/acc/relate/{flower_id}/{acc_id}")
    public ResponseEntity<?> relateAccToFlower(@PathVariable("flower_id") Long flower_id,
                                               @PathVariable("acc_id") Long acc_id) {

        try {
            return ResponseEntity.status(HttpStatus.OK).body(itemService.relateAccToFlowerService(flower_id,acc_id));
        } catch (DataNotFoundException e) {
            e.printStackTrace();
            return  ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ErrorDto.builder().errorCode(500).description("data not found").build());
        }
    }

    //Deleting target Flower
    @DeleteMapping("/flower/{flower_id}")
    public ResponseEntity<?> deleteFlower(@PathVariable Long flower_id){

        try {
            itemService.deleteTargetFlower(flower_id);
        } catch (DataNotFoundException e) {
            e.printStackTrace();
            return  ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ErrorDto.builder().errorCode(500).description("cannot delete flower").build());
        }



        return ResponseEntity.status(HttpStatus.OK).body(new HashMap<String,String>(){{
            put("resp", "successuflly deleted flower");
        }});


    }



    //Deleting target Acc



    //remove target Acc from target Flower's Recommended List


}
