package tinfo.project.tinfo482.service;


import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import tinfo.project.tinfo482.dto.transaction.CartBundleDto;
import tinfo.project.tinfo482.dto.transaction.CartDto;
import tinfo.project.tinfo482.dto.transaction.ReceiptDto;
import tinfo.project.tinfo482.entity.inventory.Item;
import tinfo.project.tinfo482.entity.transaction.Cart;
import tinfo.project.tinfo482.entity.transaction.CartBundle;
import tinfo.project.tinfo482.entity.transaction.Receipt;
import tinfo.project.tinfo482.exceptions.DataNotFoundException;
import tinfo.project.tinfo482.functionalities.auth.entity.Member;
import tinfo.project.tinfo482.functionalities.auth.repo.MemberRepository;
import tinfo.project.tinfo482.functionalities.mail.dto.MailDto;
import tinfo.project.tinfo482.functionalities.mail.service.MailService;
import tinfo.project.tinfo482.repo.inventory.ItemRepository;
import tinfo.project.tinfo482.repo.transaction.CartBundleRepository;
import tinfo.project.tinfo482.repo.transaction.CartRepository;
import tinfo.project.tinfo482.repo.transaction.ReceiptRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Transactional
@Service
@Slf4j
@RequiredArgsConstructor
public class TransactionService {

    private final MemberRepository memberRepository;

    private final ItemRepository itemRepository;

    private final CartRepository cartRepository;

    private final CartBundleRepository cartBundleRepository;

    private final ReceiptRepository receiptRepository;

    private final EntityManager entityManager;

    private final MailService mailService;

    // add Cart
    public CartDto addCart(Long item_id, Long member_id) throws DataNotFoundException, DataIntegrityViolationException {



        Item item = itemRepository.findById(item_id).orElseThrow(()->new DataNotFoundException("Cannot find Item"));

        Member member = memberRepository.findById(member_id).orElseThrow(()->new DataNotFoundException("Cannot find Member"));


            Cart cart = cartRepository.save(
                    Cart.builder()
                            .quantity(1)
                            .cartBundle(null)
                            .member(member)
                            .item(item)
                            .build()
            );
     // throw data integrity exception implies duplicate item exist inside current cart


        // first cart registration is always 1 & bundleId won't be assigned until transaction completion
        return cart.toCartDto(1,null);

        }




    // after transaction complete, register carts to single cart bundle
    private CartBundle generateCartBundle(CartBundleDto cartBundleDto, Long user_id) throws DataNotFoundException {
        List<Cart> carts = new ArrayList<>();
        CartBundle cartBundle = CartBundle.builder().request(cartBundleDto.getRequest()).build();

        log.info("Request:::::::::"+ cartBundleDto.getRequest());

        //to make sure, chk user_id matches
        for(CartDto i : cartBundleDto.getCartDtos()){
           Cart cart = cartRepository.findById(i.getId()).orElseThrow(()-> new DataNotFoundException("cannot find Cart with id: _"+i));

           //item's count might vary, so we need to update
            cart.setQuantity(i.getQuantity());
           //adding all carts
            carts.add(cart);

            // important. cart is the owner of the association, so must set cartbundle from cart side.
            cart.setCartBundle(cartBundle);

        }

        cartBundle.setCartList(carts);


       CartBundle savedCartBundle =  cartBundleRepository.save(cartBundle);

        return savedCartBundle;

//        return CartBundleDto.builder()
//                .cartDtos(cartBundleDto.getCartDtos())
//                .request(savedCartBundle.getRequest())
//                .build();
    }


    // delete Cart
    public void deleteCart(Long cart_id){
        cartRepository.deleteById(cart_id);
    }

    // generate receipt
    public byte[] generateReceipt(CartBundleDto cartBundleDto, Long member_id) throws DataNotFoundException {
       CartBundle cartBundle = this.generateCartBundle(cartBundleDto, member_id);
        Member member = memberRepository.findById(member_id).orElseThrow(()->new DataNotFoundException("Cannot find Member"));
        AtomicReference<Float> subtotal = new AtomicReference<>((float) 0);
        AtomicReference<Float> tax = new AtomicReference<>((float) 0);

        cartBundle.getCartList().stream().forEach(cart->{
           float price =  cart.getItem().getPrice()*cart.getQuantity();
           tax.updateAndGet(v -> ((float) (v + price / 10)));
           subtotal.updateAndGet(v -> (float) (v + price));
        });
        log.info("calc Tax= "+tax.toString());
        log.info("calc Subtotla= "+subtotal.toString());

        Receipt receipt =
                receiptRepository.save(Receipt.builder()
                        .cartBundle(cartBundle)
                        .date(LocalDate.now())
                        .member(member)
                        .tax(tax.get())
                        .subTotal(subtotal.get())
                        .build());



//        this.sendEmailAfterTransaction(receipt);


        //cartList clear


//        cartBundle.getCartList().stream().forEach(cart->{
//            cart.setTransaction(true);
//        });


        return mailService.pdfGenerator(receipt);
    }


    // email sender
    private void sendEmailAfterTransaction(Receipt receipt){
        String message =

                "    <h1>Thanks for your Purchase!</h1>\n" +
                "\n" +
                "    <div class=\"receipt\">\n" +
                "        <h2>Invoice</h2>\n" +
                "        <div class=\"subtotal\">Total: $ "+receipt.getSubTotal()+"</div>\n" +
                "        <div class=\"total\">Tax: $"+receipt.getTax()+"</div>\n" +
                "        <div class=\"email\"> Email Address: "+receipt.getMember().getEmail()+"</div>\n" +
                "        <div class=\"date\"> Date of Purchase: "+receipt.getDate()+"</div>\n" +
                "    </div>\n" +
                "<style>\n" +
                "    h1{\n" +
                "        text-align: center;\n" +
                "    }\n" +
                "    body {\n" +
                "font-family: Arial, sans-serif;\n" +
                "margin: 0;\n" +
                "padding: 0;\n" +
                "background-color: #f2f2f2;\n" +
                "}\n" +
                "\n" +
                ".receipt {\n" +
                "background-color: #fff;\n" +
                "border-radius: 5px;\n" +
                "box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);\n" +
                "padding: 20px;\n" +
                "margin: 20px auto;\n" +
                "max-width: 400px;\n" +
                "}\n" +
                "\n" +
                ".receipt h2 {\n" +
                "margin-top: 0;\n" +
                "text-align: center;\n" +
                "}\n" +
                "\n" +
                ".receipt div {\n" +
                "margin-bottom: 10px;\n" +
                "}\n" +
                "\n" +
                ".subtotal, .total {\n" +
                "font-weight: bold;\n" +
                "}\n" +
                "\n" +
                ".email {\n" +
                "font-style: italic;\n" +
                "}\n" +
                "</style>\n"
           ;
        mailService.sendMail(
                MailDto.builder()
                        .message(message)
                        .subject("Thanks for your Purchase! - Terra Treasures")
                        .to(receipt.getMember().getEmail())
                        .build(),
                (MultipartFile) null
        );
    }


    // fetch All transactions(Receipt) by userId



    // fetch current carts by userId
    public List<CartDto> fetchCartDtosByMemberId(Long member_id){

        List<Cart> carts = cartRepository.findAllByMember_IdAndTransactionFalse(member_id);
        List<CartDto> cartDtos = carts.stream().map((cart)->{
            return cart.toCartDto(cart.getQuantity(),null);
        }).collect(Collectors.toList());

        return cartDtos;
    }



    }





