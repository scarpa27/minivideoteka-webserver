package hr.tvz.tim2.webserver.controller;

import hr.tvz.tim2.webserver.stock.logic.StockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

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
    public Map<String, Integer> getAllStocks() {
        return stockService.getAllStocks();
    }

    @GetMapping("/{movieId}")
    public Integer getStockById(@PathVariable String movieId) {
        return stockService.getStockById(movieId);
    }
}