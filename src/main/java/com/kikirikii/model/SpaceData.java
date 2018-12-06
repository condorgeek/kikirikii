/*
 * Proprietary and Confidential
 *
 * Copyright (c) [2018] -  [] Marcelo H. Krebber - European Union 2018
 * All Rights Reserved.
 *
 * Dissemination or reproduction of this file [SpaceData.java] or parts within
 * via any medium is strictly forbidden unless prior written permission is obtained
 * from <marcelo.krebber@gmail.com>
 *
 * Last modified: 05.12.18 13:05
 */

package com.kikirikii.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.Date;

@Entity
@Table(name = "space_data")
public class SpaceData {

    public enum State {ACTIVE, BLOCKED, DELETED}

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @JsonIgnore
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "space_id")
    private Space space;

    private Address address;

    private LocalDate startDate;

    private LocalDate endDate;

    @Column(columnDefinition = "text", length = 10485760)
    private String generalInformation;

    @Column(columnDefinition = "text", length = 10485760)
    private String theVenue;

    @Column(columnDefinition = "text", length = 10485760)
    private String theCity;

    @Column(columnDefinition = "text", length = 10485760)
    private String travelInformation;

    @Column(columnDefinition = "text", length = 10485760)
    private String accommodation;

    @Column(columnDefinition = "text", length = 10485760)
    private String charityRun;

    @Column(columnDefinition = "text", length = 10485760)
    private String organization;

    @Column(columnDefinition = "text", length = 10485760)
    private String keyDates;

    @NotNull
    @Enumerated(EnumType.STRING)
    private State state;

    @NotNull
    private Date created;

    private SpaceData() { /* empty */}

    public static SpaceData of(Address address, LocalDate startDate, LocalDate endDate,
                               String generalInformation, String theVenue, String theCity,
                               String travelInformation, String accommodation, String charityRun,
                               String organization, String keyDates) {

        return of(null, address, startDate, endDate, generalInformation, theVenue, theCity,
                travelInformation, accommodation, charityRun, organization, keyDates);
    }

    public static SpaceData of(Space space, Address address, LocalDate startDate, LocalDate endDate,
                               String generalInformation, String theVenue, String theCity,
                               String travelInformation, String accommodation, String charityRun,
                               String organization, String keyDates) {
        SpaceData data = new SpaceData();
        data.space = space;
        data.address = address;
        data.startDate = startDate;
        data.endDate = endDate;
        data.generalInformation = generalInformation;
        data.theVenue = theVenue;
        data.theCity = theCity;
        data.travelInformation = travelInformation;
        data.accommodation = accommodation;
        data.charityRun = charityRun;
        data.organization = organization;
        data.keyDates = keyDates;
        data.state = State.ACTIVE;
        data.created = new Date();
        return data;
    }

    public Space getSpace() {
        return space;
    }

    public void setSpace(Space space) {
        this.space = space;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
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

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public long getId() {
        return id;
    }
}
