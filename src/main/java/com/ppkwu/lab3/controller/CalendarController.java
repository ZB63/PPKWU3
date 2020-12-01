package com.ppkwu.lab3.controller;

import biweekly.Biweekly;
import biweekly.ICalendar;
import biweekly.component.VEvent;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@RestController
public class CalendarController {

    @GetMapping(value = "calendar/{year}/{month}")
    public ResponseEntity buildCalendar(@PathVariable String year, @PathVariable String month) throws IOException, ParseException {
        ICalendar calendar = new ICalendar();
        calendar.setExperimentalProperty("X-WR-CALNAME", "Spring Releases");

        // Download data from university website
        Document doc = Jsoup
                .connect("http://www.weeia.p.lodz.pl/pliki_strony_kontroler/kalendarz.php?rok=" + year + "&miesiac=" + month)
                .get();

        Element table = doc.select("table").get(0);

        Elements rows = table.select("tr");

        for(int i=2;i<rows.size();i++) {
            Element row = rows.get(i);
            Elements cols = row.select("td");

            for(int j=0 ;j<cols.size();j++) {
                Element td = cols.get(j);
                if(td.hasClass("active")) {
                    System.out.println(cols.get(j).text());
                    Element a = td.selectFirst("a");
                    System.out.println("day: " + a.text());
                    System.out.println("link/description: " + a.attr("href"));
                    System.out.println("event name: " + td.selectFirst("div").text());
                }
            }
        }

        // event
        VEvent event = new VEvent();
        event.setSummary("Team Meeting");
        Date start = new SimpleDateFormat("yyyy-MM-dd").parse("2020-11-18");
        event.setDateStart(start);
        Date end = new SimpleDateFormat("yyyy-MM-dd").parse("2020-11-18");
        event.setDateEnd(end);
        calendar.addEvent(event);

        File ics = new File("events" + year + "_" + month + ".ics");
        Biweekly.write(calendar).go(ics);

        Path path = Paths.get("events" + year + "_" + month + ".ics");
        Resource resource = null;
        try {
            resource = new UrlResource(path.toUri());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("application/octet-stream"))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

}
