package hr.tvz.tim2.webserver.controller;

import hr.tvz.tim2.webserver.dto.StockDto;
import hr.tvz.tim2.webserver.stock.logic.StockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/stock")
public class StockController {
    final StockService stockService;

    @Autowired
    public StockController(StockService stockService) {
        this.stockService = stockService;
    }

    @GetMapping()
    @Secured({"ROLE_USER"})
    public ResponseEntity<List<StockDto>> getAllStocks() {
        try {
            return ResponseEntity.ok().body(stockService.getAllStocks());
        }
        catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/{movieId}")
    public ResponseEntity<StockDto> getStockById(@PathVariable String movieId) {
        try {
            return ResponseEntity.ok().body(stockService.getStockById(movieId));
        }
        catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}