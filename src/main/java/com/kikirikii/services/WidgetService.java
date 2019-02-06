/*
 * Proprietary and Confidential
 *
 * Copyright (c) [2018] -  [] Marcelo H. Krebber - European Union 2018
 * All Rights Reserved.
 *
 * Dissemination or reproduction of this file [WidgetService.java] or parts within
 * via any medium is strictly forbidden unless prior written permission is obtained
 * from <marcelo.krebber@gmail.com>
 *
 * Last modified: 06.02.19 10:49
 */

package com.kikirikii.services;

import com.kikirikii.model.Space;
import com.kikirikii.model.User;
import com.kikirikii.model.Widget;
import com.kikirikii.repos.WidgetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

/**
 * Widgets are not bound to either a space or user. If present they are valid for the site, meaning
 * they will show for everybody.
 */

@Service
@Transactional
public class WidgetService {

    @Autowired
    private WidgetRepository widgetRepository;

    public List<Widget> getWidgets(Widget.Position pos) {
        return widgetRepository.findAllByPosition(pos);
    }

    public List<Widget> getWidgets() {
        return widgetRepository.findAll();
    }

    public Widget save(Space space, Widget.Position pos, int ranking) {
        return widgetRepository.save(Widget.of(space, pos, ranking));
    }

    public Widget save(User user, Widget.Position pos, int ranking) {
        return widgetRepository.save(Widget.of(user, pos, ranking));
    }

    public Widget save(String url, String cover, String title, String text, Widget.Position pos, int ranking) {
        return widgetRepository.save(Widget.of(url, cover, title, text, pos, ranking));
    }
}
