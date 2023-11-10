package com.example.cashcard;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cashcards")
public class CashCardController {


    @GetMapping("/{requestedId}")
    public ResponseEntity<CashCard> findById(@PathVariable Long requestedId) {
         /* Here would be the code to retrieve the CashCard */
         if(requestedId.equals(99L)){
             return ResponseEntity.ok(new CashCard(99L, 123.45));
         }else return ResponseEntity.notFound().build();
    }
}
