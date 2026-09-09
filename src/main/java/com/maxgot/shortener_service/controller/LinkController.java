package com.maxgot.shortener_service.controller;

import com.maxgot.shortener_service.dto.CreateLinkRequest;
import com.maxgot.shortener_service.dto.LinkResponse;
import com.maxgot.shortener_service.entity.Link;
import com.maxgot.shortener_service.service.LinkService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@Valid
public class LinkController {
    @Autowired
    private LinkService linkService;

    public LinkController(LinkService linkService) {
        this.linkService = linkService;
    }


    //Создание ссылки POST /api/links
    @PostMapping("/api/links")
    public ResponseEntity<LinkResponse> create(@Valid @RequestBody CreateLinkRequest request) {
        LinkResponse created = linkService.createLink(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    //GET /{shortCode} → редирект (302)
    @GetMapping("/{shortCode}")
    public ResponseEntity<Void> getShortCode(@PathVariable String shortCode) {
        Link link = linkService.getLinkByShortCode(shortCode);
        String originalUrl = link.getOriginalUrl();
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(originalUrl))
                .build();
    }

    //GET /api/links/{shortCode} → информация
    @GetMapping("/api/links/{shortCode}")
    public ResponseEntity<LinkResponse> getInfo(@PathVariable String shortCode) {
        LinkResponse link = linkService.getInfoByShortCode(shortCode);
        return ResponseEntity.ok(link);
    }

    @DeleteMapping("/api/links/{shortCode}")
    public ResponseEntity<Void> delete(@PathVariable String shortCode) {
        linkService.deleteLink(shortCode);
        return ResponseEntity.noContent().build();
    }
}
