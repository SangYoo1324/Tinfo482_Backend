package tinfo.project.tinfo482.service;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import tinfo.project.tinfo482.dto.transaction.CartDto;
import tinfo.project.tinfo482.exceptions.DataNotFoundException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Slf4j
class TransactionServiceTest {

    @Autowired
    TransactionService transactionService;


    @Test
    void addCart() {
        CartDto cartDto = null;
        try {
           cartDto = transactionService.addCart(64l,1000l);
            log.info(cartDto.toString());
        } catch (DataNotFoundException e) {
            e.printStackTrace();
        }
        catch (DataIntegrityViolationException e){
            e.printStackTrace();
            log.info("Item already inside current cart");
        }
    }
}