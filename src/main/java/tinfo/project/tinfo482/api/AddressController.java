package tinfo.project.tinfo482.api;


import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tinfo.project.tinfo482.dto.AddressDto;
import tinfo.project.tinfo482.dto.ErrorDto;
import tinfo.project.tinfo482.exceptions.DataNotFoundException;
import tinfo.project.tinfo482.service.AddressService;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class AddressController {

    private final AddressService addressService;


    @GetMapping("/address/{id}")
    public ResponseEntity<?> getAddress(@PathVariable("id") Long id){

        try {
            return ResponseEntity.status(HttpStatus.OK).body(addressService.getAddress(id));
        } catch (DataNotFoundException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ErrorDto.builder().errorCode(500).description("cannot find address with given username"));
        }
    }

    @PatchMapping("/address/{id}")
    public ResponseEntity<?> updateAddress(@PathVariable("id") Long id, @RequestBody AddressDto addressDto){
        try {
            return ResponseEntity.status(HttpStatus.OK).body(addressService.updateAddress(id, addressDto));
        } catch (DataNotFoundException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ErrorDto.builder().errorCode(500).description("cannot find address with given username"));
        }
    }

    @DeleteMapping("/address/{id}")
    public ResponseEntity<?> detachAddress(@PathVariable("id") Long id){

        try {
            addressService.detachExistingAddr(id);
            return ResponseEntity.status(HttpStatus.OK).body(new HashMap<String,String>(){{
                put("resp", "successfully deleted");
            }});
        } catch (DataNotFoundException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ErrorDto.builder().errorCode(500).description("cannot find address with given username"));
        }


    }

}
