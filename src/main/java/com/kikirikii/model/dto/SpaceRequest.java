/*
 * Proprietary and Confidential
 *
 * Copyright (c) [2018] -  [] Marcelo H. Krebber - European Union 2018
 * All Rights Reserved.
 *
 * Dissemination or reproduction of this file [SpaceRequest.java] or parts within
 * via any medium is strictly forbidden unless prior written permission is obtained
 * from <marcelo.krebber@gmail.com>
 *
 * Last modified: 06.12.18 16:47
 */

package com.kikirikii.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kikirikii.model.Space;
import com.kikirikii.model.SpaceData;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SpaceRequest {
    // mandatory
    private String name;
    private String description;
    private Integer ranking;
    private Space.Access access;

    //spacedata
    private String startDate;
    private String endDate;
    private String generalInformation;
    private String theVenue;
    private String theCity;
    private String travelInformation;
    private String accommodation;
    private String charityRun;
    private String organization;
    private String keyDates;
    private String tickets;
    private String dates;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Space.Access getAccess() {
        return access;
    }

    public void setAccess(Space.Access access) {
        this.access = access;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getGeneralInformation() {
        return generalInformation;
    }

    public void setGeneralInformation(String generalInformation) {
        this.generalInformation = generalInformation;
    }

    public String getTheVenue() {
        return theVenue;
    }

    public void setTheVenue(String theVenue) {
        this.theVenue = theVenue;
    }

    public String getTheCity() {
        return theCity;
    }

    public void setTheCity(String theCity) {
        this.theCity = theCity;
    }

    public String getTravelInformation() {
        return travelInformation;
    }

    public void setTravelInformation(String travelInformation) {
        this.travelInformation = travelInformation;
    }

    public String getAccommodation() {
        return accommodation;
    }

    public void setAccommodation(String accommodation) {
        this.accommodation = accommodation;
    }

    public String getCharityRun() {
        return charityRun;
    }

    public void setCharityRun(String charityRun) {
        this.charityRun = charityRun;
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public String getKeyDates() {
        return keyDates;
    }

    public void setKeyDates(String keyDates) {
        this.keyDates = keyDates;
    }

    public String getTickets() {
        return tickets;
    }

    public void setTickets(String tickets) {
        this.tickets = tickets;
    }

    public String getDates() {
        return dates;
    }

    public void setDates(String dates) {
        this.dates = dates;
    }

    public int getRanking() {
        return ranking;
    }

    public void setRanking(int ranking) {
        this.ranking = ranking;
    }

    public Space update(Space space) {

        if(name != null) { space.setName(name); }
        if(description != null) { space.setDescription(description); }
        if(access != null) { space.setAccess(access); }
        if(ranking != null) {space.setRanking(ranking); }

        /* ISO_DATE = YYYY-MM-DD */
        LocalDate startDate = this.startDate != null ? LocalDate.parse(this.startDate, DateTimeFormatter.ISO_DATE) : null;
        LocalDate endDate = this.endDate != null ? LocalDate.parse(this.endDate, DateTimeFormatter.ISO_DATE) : null;

        if(space.getSpacedata() == null) {
            SpaceData spaceData = SpaceData.of(space, null, startDate, endDate, generalInformation,
                    theVenue, theCity, travelInformation, accommodation, charityRun, organization,
                    keyDates, tickets, dates);
            space.setSpacedata(spaceData);
        } else {
            SpaceData spaceData = space.getSpacedata();
            spaceData.setStartDate(startDate);
            spaceData.setEndDate(endDate);
            spaceData.setGeneralInformation(generalInformation);
            spaceData.setTheVenue(theVenue);
            spaceData.setTheCity(theCity);
            spaceData.setTravelInformation(travelInformation);
            spaceData.setAccommodation(accommodation);
            spaceData.setCharityRun(charityRun);
            spaceData.setOrganization(organization);
            spaceData.setKeyDates(keyDates);
            spaceData.setTickets(tickets);
            spaceData.setDates(dates);
        }

        return space;
    }

}
