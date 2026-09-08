package com.maxgot.shortener_service.service;

import com.maxgot.shortener_service.dto.CreateLinkRequest;
import com.maxgot.shortener_service.dto.LinkResponse;
import com.maxgot.shortener_service.entity.Link;
import com.maxgot.shortener_service.exception.LinkExpiredException;
import com.maxgot.shortener_service.exception.LinkNotFoundException;
import com.maxgot.shortener_service.repository.LinkRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class LinkService {
    private final LinkRepository linkRepository;
    public LinkService(LinkRepository linkRepository) {
        this.linkRepository = linkRepository;
    }

    public LinkResponse createLink(CreateLinkRequest request) {
        String originalUrl;
        String shortCode;
        /*
        - Сгенерировать `shortCode` через `UUID.randomUUID().toString().substring(0, 8)`
        - Сохранить в БД
        - Вернуть `LinkResponse`
         */
        originalUrl = request.getOriginalUrl();

        shortCode =  UUID.randomUUID().toString().substring(0, 8);

        Link link = new Link();
        link.setOriginalUrl(originalUrl);
        link.setShortCode(shortCode);
        link.setClicks(0L);
        link.setCreatedAt(LocalDateTime.now());
        link.setExpiresAt(LocalDateTime.now().plusDays(30));

        Link savedLink = linkRepository.save(link);
        return new LinkResponse(
                savedLink.getShortCode(),
                savedLink.getOriginalUrl());
    }

    public Link getLinkByShortCode(String shortCode) {
            Link link = linkRepository.findByShortCode(shortCode)
                    .orElseThrow(() -> new LinkNotFoundException("запись не найдена: " + shortCode)); //TODO:искл
            //проверить срок действия (expiresAt)
        if (link.getExpiresAt() != null && LocalDateTime.now().isAfter(link.getExpiresAt())){
            throw new LinkExpiredException("Ccылка истекла:" + shortCode); //TODO:искл
        }
        link.setClicks(link.getClicks() + 1);
        linkRepository.save(link);
        return link;
    }

    public LinkResponse getInfoByShortCode(String shortCode) {
        //найти
        Link link = linkRepository.findByShortCode(shortCode)
                .orElseThrow(() -> new LinkNotFoundException("запись не найдена: " + shortCode)); //TODO:искл
        //чек срок
        if (link.getExpiresAt() != null && LocalDateTime.now().isAfter(link.getExpiresAt())){
            throw new LinkExpiredException("Ccылка истекла:" + shortCode); //TODO:искл
        }
        return new LinkResponse(link.getShortCode(), link.getOriginalUrl());
    }

    public void deleteLink(String shortCode) {
        Link link = linkRepository.findByShortCode(shortCode)
                .orElseThrow(() -> new LinkNotFoundException("запись не найдена: " + shortCode)); //TODO:искл
        linkRepository.delete(link);
    }
}
