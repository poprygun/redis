package io.microsamples.cache.redis.api;

import io.microsamples.cache.redis.service.DaysService;
import io.microsamples.cache.redis.service.MonthsService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class CachedDaysMonthsApi {

    private MonthsService monthsService;

    private DaysService daysService;


    public CachedDaysMonthsApi(MonthsService monthsService, DaysService daysService) {
        this.monthsService = monthsService;
        this.daysService = daysService;
    }

    @GetMapping("/months")
    public ResponseEntity<List<String>> months() {
        return new ResponseEntity<>(monthsService.springMonths(), HttpStatus.OK);
    }

    @GetMapping("/days")
    public ResponseEntity<List<String>> days() {
        return new ResponseEntity<>(daysService.daysOfWeek(), HttpStatus.OK);
    }




}

