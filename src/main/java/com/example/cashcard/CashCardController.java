package com.example.cashcard;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;


import java.security.Principal;
import java.util.List;
import java.net.URI;

@RestController
@RequestMapping("/cashcards")
public class CashCardController {

    private final CashCardRepository cashCardRepository;

    @Autowired
    public CashCardController(CashCardRepository cashCardRepository) {
        this.cashCardRepository = cashCardRepository;
    }

    private CashCard findCashCard(Long requestedId, Principal principal) {
        return cashCardRepository.findByIdAndOwner(requestedId, principal.getName());
    }

    @GetMapping("/{requestedId}")
    public ResponseEntity<CashCardWithLinks> findById(
            @PathVariable Long requestedId,
            Principal principal) {

         /* Here would be the code to retrieve the CashCard */
        CashCard cashCard = findCashCard(requestedId, principal);
        if (cashCard != null) {

            // Manually create a self link
            URI selfLink = ServletUriComponentsBuilder.fromCurrentRequest()
                    .build()
                    .toUri();

            CashCardWithLinks cashCardWithLinks = new CashCardWithLinks(cashCard, selfLink.toString(), null);


            return ResponseEntity.ok(cashCardWithLinks);
        }

        return ResponseEntity.notFound().build();
    }

    @PostMapping
    private ResponseEntity<Void> createCashCard(
            @RequestBody CashCard newCashCardRequest,
            UriComponentsBuilder ucb,
            Principal principal) {

        CashCard cashCardWithOwner = new CashCard(null, newCashCardRequest.getAmount(), principal.getName());
        CashCard savedCashCard = cashCardRepository.save(cashCardWithOwner);
        URI locationOfNewCashCard = ucb
                .path("cashcards/{id}")
                .buildAndExpand(savedCashCard.getId())
                .toUri();

        return ResponseEntity.created(locationOfNewCashCard).build();
    }

    @GetMapping
    public ResponseEntity<CashCardWithLinks> findAll(Pageable pageable ,Principal principal) {
        Page<CashCard> page = cashCardRepository.findByOwner(principal.getName(),
                PageRequest.of(
                        //Spring provides the default page and size values (they are 0 and 20, respectively)
                        //The getPageNumber() method extracts the page query parameter from the request URI
                        pageable.getPageNumber(),
                        //The getPageSize() method extracts the size query parameter from the request URI
                        pageable.getPageSize(),
                        //The getSort() method extracts the sort query parameter from the request URI
                        pageable.getSortOr(Sort.by(Sort.Direction.ASC, "amount"))
                ));
        // Manually create a self link
        URI selfLink = ServletUriComponentsBuilder.fromCurrentRequest()
                .build()
                .toUri();

        CashCardWithLinks cashCardWithLinks = new CashCardWithLinks(selfLink.toString(), page.getContent());

        return ResponseEntity.ok(cashCardWithLinks);
    }

    @PutMapping("/{requestedId}")
    private ResponseEntity<Void> putCashCard(
            @PathVariable Long requestedId,
            @RequestBody CashCard cashCardUpdate,
            Principal principal) {

        CashCard cashCard = findCashCard(requestedId, principal);
        if (cashCard != null){
            CashCard updatedCashCard = new CashCard(cashCard.getId(), cashCardUpdate.getAmount(), principal.getName());
            cashCardRepository.save(updatedCashCard);
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    private ResponseEntity<Void> deleteCashCard(@PathVariable Long id, Principal principal) {

        if (cashCardRepository.existsByIdAndOwner(id, principal.getName())) {

            cashCardRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.notFound().build();

    }

    public static class CashCardWithLinks {
        private CashCard cashCard;
        private final String self;

        private final List<CashCard> page;

        public CashCardWithLinks(CashCard cashCard, String self, List<CashCard> page) {
            this.cashCard = cashCard;
            this.self = self;
            this.page = page;
        }

        public CashCardWithLinks(String self, List<CashCard> page) {
            this.self = self;
            this.page = page;
        }

        public CashCard getCashCard() {
            return cashCard;
        }

        public String getSelf() {
            return self;
        }

        public List<CashCard> getPage() {
            return page;
        }
    }
}
