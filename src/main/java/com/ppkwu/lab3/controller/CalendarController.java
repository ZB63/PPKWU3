package com.ppkwu.lab3.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CalendarController {

    @GetMapping("calendar/{year}/{month}")
    public void buildCalendar(@PathVariable String year, @PathVariable String month) {
        System.out.println("Year: " + year + ", Month: " + month);
    }

}
