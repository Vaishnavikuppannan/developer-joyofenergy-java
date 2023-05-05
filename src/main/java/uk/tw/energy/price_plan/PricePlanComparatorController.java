package uk.tw.energy.price_plan;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/price-plans")
public class PricePlanComparatorController {

    public final static String PRICE_PLAN_ID_KEY = "pricePlanId";
    public final static String PRICE_PLAN_COMPARISONS_KEY = "pricePlanComparisons";
    private final PricePlanService pricePlanService;
    private final AccountService accountService;

    public PricePlanComparatorController(PricePlanService pricePlanService, AccountService accountService) {
        this.pricePlanService = pricePlanService;
        this.accountService = accountService;
    }

    @GetMapping("/compare-all/{smartMeterId}")
    public ResponseEntity<Map<String, Object>> calculatedCostForEachPricePlan(@PathVariable String smartMeterId) {
       String pricePlanId = accountService.getPricePlanIdForSmartMeterId(smartMeterId);

        return pricePlanService.getConsumptionCostOfElectricityReadingsForEachPricePlan(smartMeterId)
                .map(costOfElectricityByPrice -> buildPricePlanComparisons(costOfElectricityByPrice, pricePlanId))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());

    }

    private Map<String, Object> buildPricePlanComparisons(Map<String, BigDecimal> costOfElectricityByPrice,String pricePlanId) {
        Map<String, Object> pricePlanComparisons = new HashMap<>();
        pricePlanComparisons.put(PRICE_PLAN_ID_KEY, pricePlanId);
        pricePlanComparisons.put(PRICE_PLAN_COMPARISONS_KEY, costOfElectricityByPrice);
        return pricePlanComparisons;
    }

    @GetMapping("/recommend/{smartMeterId}")
    public ResponseEntity<List<Map.Entry<String, BigDecimal>>> recommendCheapestPricePlans(@PathVariable String smartMeterId,
                                                                                           @RequestParam(value = "limit", required = false) Integer limit) {
        Optional<Map<String, BigDecimal>> consumptionsForPricePlans =
                pricePlanService.getConsumptionCostOfElectricityReadingsForEachPricePlan(smartMeterId);


        return consumptionsForPricePlans
                .map(consumptionsPricePlans -> buildCheapestPricePlans(limit, consumptionsPricePlans))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());

    }

    private List<Map.Entry<String, BigDecimal>> buildCheapestPricePlans(Integer limit,Map<String, BigDecimal> consumptionsPricePlans) {
        return consumptionsPricePlans.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue())
                .limit(limit != null ? limit : Long.MAX_VALUE)
                .collect(Collectors.toList());
    }
}
