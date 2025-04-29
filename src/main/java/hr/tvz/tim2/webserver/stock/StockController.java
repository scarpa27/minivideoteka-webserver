package hr.tvz.tim2.webserver.stock;

import hr.tvz.tim2.webserver.common.exception.ApiError;
import hr.tvz.tim2.webserver.dto.StockDto;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/stock")
@ApiResponse(responseCode = "200")
@ApiResponse(description = "Error", content = @Content(schema = @Schema(implementation = ApiError.class)))
public class StockController {
    final StockService stockService;

    @Autowired
    public StockController(StockService stockService) {
        this.stockService = stockService;
    }

    @GetMapping()
    public ResponseEntity<List<StockDto>> getAllStocks() {
        try {
            return ResponseEntity.ok().body(stockService.getAllStocksDto());
        }
        catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/{movieId}")
    public ResponseEntity<StockDto> getStockById(@PathVariable String movieId) {
        try {
            return ResponseEntity.ok().body(stockService.getStockDtoById(movieId));
        }
        catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}