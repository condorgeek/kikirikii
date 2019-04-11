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

import com.kikirikii.exceptions.InvalidAuthorizationException;
import com.kikirikii.exceptions.InvalidResourceException;
import com.kikirikii.model.*;
import com.kikirikii.model.dto.RankingRequest;
import com.kikirikii.model.enums.MediaType;
import com.kikirikii.model.enums.State;
import com.kikirikii.repos.MemberRepository;
import com.kikirikii.repos.SpaceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    public Space getSpace(String spacename) {
        try {
            Optional<Space> space = spaceRepository.findBySpacename(spacename);
            if(space.isPresent()) {
                return space.get();
            }
            throw new InvalidResourceException("Space " + spacename + " is invalid.");

        } catch (Exception e) { // try block to catch any duplicates in space name
            throw new InvalidResourceException("Space " + spacename + " is invalid or duplicate." + e.getMessage());
        }
    }

    public List<Space> reorderRanking(RankingRequest rankingRequest) {
        List<Space> spaces = new ArrayList<>();
        Arrays.stream(rankingRequest.getRanks()).forEach(rank -> {
            spaceRepository.findById(rank.getSpaceId()).ifPresent(space ->{
                space.setRanking(rank.getRanking());
                spaces.add(spaceRepository.save(space));
            });
        });

        return spaces;
    }

    /* Achtung! potentially dangerous call might return more than one result / for testing only */
    public Optional<Space> findBySpacename(String spacename) {
        return spaceRepository.findBySpacename(spacename);
    }

    public Member getMember(Long id) {
        Optional<Member> member = memberRepository.findById(id);
        if (member.isPresent()) {
            return member.get();
        }
        throw new InvalidResourceException("Member Id " + id + " is invalid.");
    }

    public Stream<Space> searchByTermAsStream(String term, Integer size) {
        return spaceRepository.searchActiveByTerm(term);
    }

    public Space addChildren(Space parent, String[] children) {
        parent = removeChildren(parent);

        for (String name : children) {
            try {
                spaceRepository.findBySpacename(name).ifPresent(parent::addChild);
            } catch (Exception e) {
                logger.info(e.getMessage()); // gracefully handle space name duplicates
            }
        }

        return spaceRepository.save(parent);
    }

    public Space addChild(Space parent, Space child) {

        parent.getChildren();

        return spaceRepository.save(parent.addChild(child));
    }

    public Space removeChild(Space parent, Space child) {
        return spaceRepository.save(parent.removeChild(child));
    }

    public Space removeChildren(Space space) {
        if(space.getChildren() == null || space.getChildren().size() > 0) {
            return spaceRepository.save(space.removeChildren());
        }
        return space;
    }

    public Member getMember(Long spaceId, String username) {
        Optional<Member> member = memberRepository.findMemberByUsername(spaceId, username);
        if (member.isPresent()) {
            return member.get();
        }
        throw new InvalidResourceException("Member Id " + username + " and Space Id " + spaceId + " is invalid.");
    }

    public Space save(Space space) {
        return spaceRepository.save(space);
    }

    public Space updateCoverPath(Space space, String path) {
        space.setCover(path);
        space.addMedia(SpaceMedia.of(path, MediaType.PICTURE));
        return spaceRepository.save(space);
    }

    public Space createSpaceAndJoin(User user, String type, String name, String description, String access) {
        try {
            Space space = spaceRepository.save(Space.of(user, name, description,
                    Space.Type.valueOf(type.toUpperCase()), Space.Access.valueOf(access)));
            space.setSpacedata(SpaceData.of());

             memberRepository.save(Member.of(space, user, State.ACTIVE, Member.Role.OWNER));
            return space;

        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        throw new InvalidResourceException("Cannot create space " + name);
    }

    public Space createSpaceAndJoin(User user, String type, String name, String description,
                                    String access, String startdate, String enddate) {
        try {
            LocalDate start = LocalDate.parse(startdate, DateTimeFormatter.ISO_DATE); // YYYY-MM-DD
            LocalDate end = LocalDate.parse(enddate, DateTimeFormatter.ISO_DATE); // YYYY-MM-DD

            return createSpaceAndJoin(user, type, name, null, null, description, 0, access,
                    SpaceData.of(null, start, end));

        } catch (DateTimeParseException e) {
            throw new InvalidResourceException("Start date or End date invalid. Cannot create space " + name);
        }
    }

    public Space createSpaceAndJoin(User user, String type, String name, String icon, String cover, String description,
                                    int ranking, String access, SpaceData spaceData) {

        // TODO cover support deprecated - use media
        try {
            Space space = Space.of(user, name, cover, null, icon, description, Space.Type.valueOf(type),
                    ranking, Space.Access.valueOf(access));
            if(cover != null) {
                space.addMedia(SpaceMedia.of(cover, MediaType.PICTURE));
            }
            space.setSpacedata(spaceData);
            spaceRepository.save(space);

            memberRepository.save(Member.of(space, user, State.ACTIVE, Member.Role.OWNER));

            return space;

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

    public Space deleteSpace(Space space) {
        space = removeChildren(space);
        space.setState(State.DELETED);
        return spaceRepository.save(space);
    }

    public Space blockSpace(Space space) {
        space.setState(State.BLOCKED);
        return spaceRepository.save(space);
    }

    public Space unblockSpace(Space space) {
        space.setState(State.ACTIVE);
        return spaceRepository.save(space);
    }

    public boolean isMember(Long spaceId, User user) {
        Optional<Member> member = memberRepository.findActiveMemberByUserId(spaceId, user.getId());
        return member.isPresent();
    }

    public boolean isMember(Long spaceId, Long userId) {
        Optional<Member> member = memberRepository.findActiveMemberByUserId(spaceId, userId);
        return member.isPresent();
    }

    public Member findMember(Long spaceId, User user) {
        Optional<Member> member = memberRepository.findActiveMemberByUserId(spaceId, user.getId());
        return member.orElse(null);
    }

    public Member findMember(Long spaceId, Long userId) {
        Optional<Member> member = memberRepository.findActiveMemberByUserId(spaceId, userId);
        return member.orElse(null);
    }

    public Member findMember(Long spaceId, String username) {
        Optional<Member> member = memberRepository.findActiveMemberByUsername(spaceId, username);
        return member.orElse(null);
    }

    public Member addMember(Space space, User user, User reference, String role) {
        if(isMember(space.getId(), user)) {
            throw new InvalidResourceException("User " + user.getUsername() + " already member of " + space.getName());
        }

        try {
            return memberRepository.save(Member.of(space, user, reference, State.ACTIVE,
                    Member.Role.valueOf(role)));

        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        throw new InvalidResourceException("Cannot add member " + user.getUsername());
    }

    public Member leaveSpace(Space space, Member member) {
        if(!isOwner.apply(space, member) && member.getSpace().getId() == space.getId()) {
            member.setState(State.DELETED);
            return memberRepository.save(member);
        }
        throw new InvalidResourceException(member.getUser().getUsername() + " cannot leave space " + space.getName());
    }

    public Member deleteMember(Space space, Member admin, Member member) {
        try {
            return _updateMemberState(space, admin, member, State.DELETED);
        } catch (Exception e) { logger.warn(e.getMessage()); }

        throw new InvalidResourceException("Cannot delete member " + member.getUser().getUsername() + " from space " + space.getName());
    }

    public Member blockMember(Space space, Member admin, Member member) {
        try {
            return _updateMemberState(space, admin, member, State.BLOCKED);
        } catch (Exception e) { logger.warn(e.getMessage()); }

        throw new InvalidResourceException("Cannot block member " + member.getUser().getUsername() + " from space " + space.getName());
    }

    private Member _updateMemberState(Space space, Member admin, Member member, State state) {
        if(!isOwner.apply(space, member) && member.getSpace().getId() == space.getId()) {
            if(admin.getRole() == Member.Role.OWNER) {
                member.setState(state);
                return memberRepository.save(member);

            } else if(admin.getRole() == Member.Role.ADMIN && member.getRole() != Member.Role.ADMIN) {
                member.setState(state);
                return memberRepository.save(member);
            }
        }
        throw new InvalidAuthorizationException(member.getUser().getUsername() + " cannot " + state + member.getUser().getUsername());
    }

    public Long getMembersCount(Long spaceId) {
        return spaceRepository.countActiveBySpaceId(spaceId);
    }

    public List<Member> getMembersBySpace(Long spaceId) {
        return memberRepository.findActiveBySpaceId(spaceId);
    }

    public Page<Member> getPageableMembersBySpace(Long spaceId, int page, int size) {
        return memberRepository.findActivePageBySpaceId(spaceId, PageRequest.of(page, size));
    }

    /* returns ownership spaces only */
    public List<Space> getGenericSpacesByUser(Long userId) {
        return spaceRepository.findActiveByUserId(userId);
    }

    /* generic and active */
    public List<Space> getMemberOfGenericSpaces(Long userId) {
        return memberRepository.findMemberOfGenericByUserId(userId).stream()
//                .filter(member -> {
//                    Space space = member.getSpace();
//                    return space.getType() == Space.Type.GENERIC && space.getState() == Space.State.ACTIVE;
//                })
                .map(Member::getSpace).collect(Collectors.toList());
    }

    public List<Space> getMemberOfSpacesByType(String type, Long userId) {
        return memberRepository.findMemberOfByTypeAndUserId(Space.Type.valueOf(type), userId).stream()
                .map(Member::getSpace).collect(Collectors.toList());
    }

    public List<Space> getPublicSpacesByType(String type, Long userId) {
        return memberRepository.findPublicByTypeAndUserId(Space.Type.valueOf(type), userId).stream()
                .map(Member::getSpace).collect(Collectors.toList());
    }

    public List<Space> getMemberOfSpacesByType(Space.Type type, Long userId) {
        return memberRepository.findMemberOfByTypeAndUserId(type, userId).stream()
                .map(Member::getSpace).collect(Collectors.toList());
    }

    public List<Space> getEventsByUser(Long userId) {
        return spaceRepository.findActiveEventsByUserId(userId);
    }

    public List<Space> getShopsByUser(Long userId) {
        return spaceRepository.findActiveShopsByUserId(userId);
    }

    private BiFunction<Space, Member, Boolean> isOwner = (s, m) -> s.getUser().getUsername().equals(m.getUser().getUsername());

}
