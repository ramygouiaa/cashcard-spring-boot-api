package com.example.cashcard;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.net.URI;
import java.util.Optional;

@RestController
@RequestMapping("/cashcards")
public class CashCardController {

    private final CashCardRepository cashCardRepository;

    public CashCardController(CashCardRepository cashCardRepository) {
        this.cashCardRepository = cashCardRepository;
    }

    @GetMapping("/{requestedId}")
    public ResponseEntity<CashCard> findById(@PathVariable Long requestedId) {
         /* Here would be the code to retrieve the CashCard */
       Optional<CashCard> cashCardOptional = cashCardRepository.findById(requestedId);

        if(cashCardOptional.isPresent()){
            return ResponseEntity.ok(cashCardOptional.get());
        }else{
            return ResponseEntity.notFound().build();
        }

       /*
       * OR with functional style expression
       *
       * return cashCardOptional.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
       * */
    }

    @PostMapping
    private ResponseEntity<Void> createCashCard(
            @RequestBody CashCard newCashCardRequest,
            UriComponentsBuilder ucb) {

        CashCard savedCashCard = cashCardRepository.save(newCashCardRequest);
        URI locationOfNewCashCard = ucb
                .path("cashcards/{id}")
                .buildAndExpand(savedCashCard.id())
                .toUri();

        return ResponseEntity.created(locationOfNewCashCard).build();
    }

    @GetMapping
    public ResponseEntity<List<CashCard>> findAll(Pageable pageable) {
        Page<CashCard> page = cashCardRepository.findAll(
                PageRequest.of(
                        //Spring provides the default page and size values (they are 0 and 20, respectively)
                        //The getPageNumber() method extracts the page query parameter from the request URI
                        pageable.getPageNumber(),
                        //The getPageSize() method extracts the size query parameter from the request URI
                        pageable.getPageSize(),
                        //The getSort() method extracts the sort query parameter from the request URI
                        pageable.getSortOr(Sort.by(Sort.Direction.ASC, "amount"))
                ));
        return ResponseEntity.ok(page.getContent());
    }


}
