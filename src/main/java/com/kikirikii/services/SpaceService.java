/*
 * Proprietary and Confidential
 *
 * Copyright (c) [2018] -  [] Marcelo H. Krebber - European Union 2018
 * All Rights Reserved.
 *
 * Dissemination or reproduction of this file [SpaceService.java] or parts within
 * via any medium is strictly forbidden unless prior written permission is obtained
 * from <marcelo.krebber@gmail.com>
 *
 * Last modified: 21.10.18 17:15
 */

package com.kikirikii.services;

import com.kikirikii.exceptions.InvalidResourceException;
import com.kikirikii.model.Member;
import com.kikirikii.model.Space;
import com.kikirikii.model.User;
import com.kikirikii.repos.MemberRepository;
import com.kikirikii.repos.SpaceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional
public class SpaceService {
    private static Logger logger = LoggerFactory.getLogger(SpaceService.class);

    @Autowired
    private SpaceRepository spaceRepository;

    @Autowired
    private MemberRepository memberRepository;

    public Space getSpace(Long id) {
        Optional<Space> space = spaceRepository.findById(id);
        if (space.isPresent()) {
            return space.get();
        }

        throw new InvalidResourceException("Space Id " + id + " is invalid.");
    }

    public Member getMember(Long id) {
        Optional<Member> member = memberRepository.findById(id);
        if (member.isPresent()) {
            return member.get();
        }

        throw new InvalidResourceException("Member Id " + id + " is invalid.");
    }

    public Map<String, Object> createSpaceCombined(User user, String name, String description, String access) {
        Map<String, Object> map = new HashMap<>();
        try {
            Space space = spaceRepository.save(Space.of(user, name, description,
                    Space.Type.GENERIC, Space.Access.valueOf(access)));

            Member member = memberRepository.save(Member.of(space, user, Member.State.ACTIVE, Member.Role.OWNER));

            map.put("space", space);
            map.put("member", member);

            return map;

        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        throw new InvalidResourceException("Cannot create space " + name);
    }

    public Space createGenericSpace(User user, String name, String description, String access) {
       return createSpace(user, name, description, access, Space.Type.GENERIC);
    }

    public Space createEvent(User user, String name, String description, String access) {
        return createSpace(user, name, description, access, Space.Type.EVENT);
    }

    public Space createShop(User user, String name, String description, String access) {
        return createSpace(user, name, description, access, Space.Type.SHOP);
    }

    private Space createSpace(User user, String name, String description, String access, Space.Type type) {
        try {
            return  spaceRepository.save(Space.of(user, name, description,
                    type, Space.Access.valueOf(access)));

        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        throw new InvalidResourceException("Cannot create space " + name);
    }

    public boolean isMember(Space space, User user) {
        Optional<Member> member = memberRepository.findMemberByUserId(space.getId(), user.getId());
        return member.isPresent();
    }

    public Member addMember(Space space, User user, User reference, String role) {
        if(isMember(space, user)) {
            throw new InvalidResourceException("User " + user.getUsername() + " already member of " + space.getName());
        }

        try {
            return memberRepository.save(Member.of(space, user, reference, Member.State.ACTIVE,
                    Member.Role.valueOf(role)));

        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        throw new InvalidResourceException("Cannot add member " + user.getUsername());
    }

    public List<Member> getMembersBySpace(Long spaceId) {
        return memberRepository.findActiveBySpaceId(spaceId);
    }

    public List<Space> getGenericSpacesByUser(Long userId) {
        return spaceRepository.findActiveByUserId(userId);
    }

    public List<Space> getEventsByUser(Long userId) {
        return spaceRepository.findActiveEventsByUserId(userId);
    }

    public List<Space> getShopsByUser(Long userId) {
        return spaceRepository.findActiveShopsByUserId(userId);
    }
}
