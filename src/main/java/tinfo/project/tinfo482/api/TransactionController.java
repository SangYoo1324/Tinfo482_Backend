package tinfo.project.tinfo482.api;


import com.nimbusds.jose.shaded.gson.*;
import com.nimbusds.jose.shaded.gson.reflect.TypeToken;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tinfo.project.tinfo482.config.CustomItemDtoDeserializer;
import tinfo.project.tinfo482.dto.ErrorDto;
import tinfo.project.tinfo482.dto.inventory.FlowerDto;
import tinfo.project.tinfo482.dto.inventory.ItemDto;
import tinfo.project.tinfo482.dto.transaction.CartBundleDto;
import tinfo.project.tinfo482.dto.transaction.CartDto;
import tinfo.project.tinfo482.exceptions.DataNotFoundException;
import tinfo.project.tinfo482.service.TransactionService;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api")
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping("/cart/{item_id}/{member_id}")
    public ResponseEntity<?> addCart(@PathVariable Long item_id, @PathVariable Long member_id){
        try {
            return ResponseEntity.status(HttpStatus.OK).body(transactionService.addCart(item_id,member_id));
        }
        // Cannot find data for item_id or member_id
        catch (DataNotFoundException e) {
           e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ErrorDto.builder()
                            .description("Cannot find item_id or member_id")
                    .errorCode(500).build());
           //Same item already exist inside cart
        } catch (DataIntegrityViolationException e){
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ErrorDto.builder()
                    .description("Same item already exist inside your cart")
                    .errorCode(500).build());
        }
    }

    @GetMapping("/carts/{member_id}")
    public ResponseEntity<?> fetchAllCart(@PathVariable Long member_id){
        return ResponseEntity.status(HttpStatus.OK).body(transactionService.fetchCartDtosByMemberId(member_id));
    }

    @DeleteMapping("/cart/{cart_id}")
    public ResponseEntity<?> deleteCart(@PathVariable Long cart_id){
        try{
            transactionService.deleteCart(cart_id);
        }catch(Exception e){
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.OK).body(
                   ErrorDto.builder()
                           .description("Error occured during deletion")
                           .errorCode(500)
                           .build()
            );
        }
        return ResponseEntity.status(HttpStatus.OK).body(
                new HashMap<String,String>(){{
                    put("resp", "cart successfully deleted");
                }}
        );
    }

    @PostMapping("/receipt/{user_id}")
    public ResponseEntity<?> generateReceipt(@PathVariable Long user_id,
                                                @RequestParam("cartBundleDto") String cartBundleDto
                                             ){


        Gson gson = new GsonBuilder()
                .registerTypeAdapter(ItemDto.class, new CustomItemDtoDeserializer())
                .create();
        CartBundleDto cartBundleDto1 = gson.fromJson(cartBundleDto, CartBundleDto.class);

        log.info("cartDtoList::::::"+cartBundleDto1.toString());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("filename", "example.pdf");

        try {
            return ResponseEntity.status(HttpStatus.OK).headers(headers).body(transactionService.generateReceipt(cartBundleDto1,user_id));
        } catch (DataNotFoundException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ErrorDto.builder().description("cannot find Dto"));
        }
    }



    }


