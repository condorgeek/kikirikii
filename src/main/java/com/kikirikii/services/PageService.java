/*
 * Proprietary and Confidential
 *
 * Copyright (c) [2018] -  [] Marcelo H. Krebber - European Union 2018
 * All Rights Reserved.
 *
 * Dissemination or reproduction of this file [PageService.java] or parts within
 * via any medium is strictly forbidden unless prior written permission is obtained
 * from <marcelo.krebber@gmail.com>
 *
 * Last modified: 09.02.19 11:25
 */

package com.kikirikii.services;

import com.kikirikii.exceptions.InvalidResourceException;
import com.kikirikii.model.Page;
import com.kikirikii.model.dto.Site;
import com.kikirikii.repos.PageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Transactional
@Service
public class PageService {

    @Autowired
    private PageRepository pageRepository;

    public Page getPage(long id) {
        Optional<Page> page = pageRepository.findById(id);
        if(page.isPresent()) {
            return page.get();
        }
        throw new InvalidResourceException("Page " + id + " is invalid.");
    }

    public Page getPage(String name) {
        Optional<Page> page = pageRepository.findByPageName(name);
        if(page.isPresent()) {
            return page.get();
        }
        throw new InvalidResourceException("Page " + name + " is invalid.");
    }

    public List<Page> getPages() {
        return pageRepository.findAllByRanking();
    }

    public Optional<Page> findPage(long id) {
        return pageRepository.findById(id);
    }

    public Optional<Page> findByName(String name) {
        return pageRepository.findByPageName(name);
    }

    public List<Page> findAll() {
        return pageRepository.findAllByRanking();
    }

    public Page createPage(Page page) {
        return pageRepository.save(page);
    }
}
