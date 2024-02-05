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

    //Fetching all Accs
    @GetMapping("/accs")
    public ResponseEntity<?> fetch_all_accs(){
        return ResponseEntity.status(HttpStatus.OK).body(itemService.fetch_all_accs());
    }

    //Fetching targetAcc
    @GetMapping("/acc/{acc_id}")
    public ResponseEntity<?> fetch_target_acc(@PathVariable("acc_id")Long acc_id){
        try {
            return ResponseEntity.status(HttpStatus.OK).body(itemService.fetch_target_acc(acc_id));
        } catch (DataNotFoundException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ErrorDto.builder().errorCode(500).description("internal server Error"));
        }
    }

    //Fetching target completeItem
    @GetMapping("/item/{flower_id}")
    public ResponseEntity<?> fetch_target_item(@PathVariable("flower_id") Long flower_id){
        try {
            return ResponseEntity.status(HttpStatus.OK).body(itemService.fetch_target_item(flower_id));
        } catch (DataNotFoundException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ErrorDto.builder().errorCode(500).description("internal server Error"));
        }
    }

    //Fetching All-Items
    @GetMapping("/items")
    public ResponseEntity<?> fetch_all_items(){
        return ResponseEntity.status(HttpStatus.OK).body(itemService.fetch_all_complete_items());
    }



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
            itemService.relateAccToFlowerService(flower_id,acc_id);
            return ResponseEntity.status(HttpStatus.OK).body(new HashMap<String,String>(){{
                put("resp", "successfully connected acc id"+acc_id+" with "+"flower id "+flower_id);
            }});
        } catch (DataNotFoundException e) {
            e.printStackTrace();
            return  ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ErrorDto.builder().errorCode(500).description("data not found").build());
        }
    }

    //completely remove flower (which means also remove all completeItem related to flower)
    @DeleteMapping("/flower/{flower_id}")
    public ResponseEntity<?> deleteFlower(@PathVariable Long flower_id){

        try {
            itemService.complete_remove_flower(flower_id);
        } catch (DataNotFoundException e) {
            e.printStackTrace();
            return  ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ErrorDto.builder().errorCode(500).description("cannot delete flower").build());
        }



        return ResponseEntity.status(HttpStatus.OK).body(new HashMap<String,String>(){{
            put("resp", "successuflly deleted flower");
        }});


    }



    //Deleting target Acc
    @DeleteMapping("/acc/{acc_id}")
    public ResponseEntity<?> deleteAcc(@PathVariable Long acc_id){

        try {
            itemService.deleteTargetAcc(acc_id);
        } catch (DataNotFoundException e) {
            e.printStackTrace();
            return  ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ErrorDto.builder().errorCode(500).description("cannot delete flower").build());
        }



        return ResponseEntity.status(HttpStatus.OK).body(new HashMap<String,String>(){{
            put("resp", "successuflly deleted acc & closed relationship to parent table 'complteItem'");
        }});


    }


    //remove target Acc from target Flower's Recommended List
    @DeleteMapping("/completeitem/remove/all/acc/{flower_id}")
    public ResponseEntity<?> deleteAccFromFlower(@PathVariable Long flower_id){

        try {
            itemService.remove_all_acc_from_flower(flower_id);
        } catch (DataNotFoundException e) {
            e.printStackTrace();
            return  ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ErrorDto.builder().errorCode(500).description("cannot delete flower").build());
        }
        return ResponseEntity.status(HttpStatus.OK).body(new HashMap<String,String>(){{
            put("resp", "successuflly deleted acc & closed relationship to parent table 'complteItem'");
        }});


    }


    //test Data Generate
    @GetMapping("/test/generate")
    public ResponseEntity<?> testGen(){
        itemService.generateDummyData();
        return ResponseEntity.status(HttpStatus.OK).body(new HashMap<String,String>(){{
            put("resp","successfully generated test data");
        }});
    }

    //test Data Delete
    @DeleteMapping("/test/delete")
    public ResponseEntity<?> testDel(){
        itemService.deleteDummyData();
        return ResponseEntity.status(HttpStatus.OK).body(new HashMap<String,String>(){{
            put("resp","successfully generated test data");
        }});
    }


}
