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

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class AddressController {

    private final AddressService addressService;


    @GetMapping("/address/{username}")
    public ResponseEntity<?> getAddress(@PathVariable("username") String username){

        try {
            return ResponseEntity.status(HttpStatus.OK).body(addressService.getAddress(username));
        } catch (DataNotFoundException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ErrorDto.builder().errorCode(500).description("cannot find address with given username"));
        }
    }

    @PatchMapping("/address/{username}")
    public ResponseEntity<?> updateAddress(@PathVariable("username") String username, AddressDto addressDto){
        try {
            return ResponseEntity.status(HttpStatus.OK).body(addressService.updateAddress(username, addressDto));
        } catch (DataNotFoundException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ErrorDto.builder().errorCode(500).description("cannot find address with given username"));
        }
    }

}
