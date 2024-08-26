package com.valorant.tracker.api.controller;

import com.valorant.tracker.service.TrackerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TrackerAPIController {

    private TrackerService trackerService;

    @Autowired
    public TrackerAPIController(TrackerService trackerService){
        this.trackerService = trackerService;
    }

//  Current act matches
    @GetMapping("/act/matches")
    public String getActMatches(@RequestParam String user, @RequestParam String tag) {
        return trackerService.scrapeActMatches(user, tag).toString();
    }

//  Total matches
    @GetMapping("/total/matches")
    public String getTotalMatches(@RequestParam String user, @RequestParam String tag) {
        return trackerService.scrapeTotalMatches(user, tag).toString();
    }

//  Career Bottom Frags
    @GetMapping("/total/bottomfrags")
    public String getTotalBottomFrags(@RequestParam String user, @RequestParam String tag, @RequestParam(required = false, defaultValue = "200") Integer limit, @RequestParam(required = false, defaultValue = "0") Integer startLimit,  @RequestParam(required = false, defaultValue = "15000") Integer sleep) {
        return trackerService.scrapeBottomFrags(user, tag, limit, startLimit, sleep).toString();
    }

}
