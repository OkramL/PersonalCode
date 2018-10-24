/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.okram.personalcode;

/**
 * Haiglate nimekirja kasutamiseks
 * @author Marko
 */
public class HospitalsList {
    String codes;
    String name;
    
    /**
     * Haiglate nimede klassifaili konstruktor
     * @param codes Haigla koodide vahemik
     * @param name Haigla nimi
     */
    public HospitalsList(String codes, String name) {
        //this.id = id;
        this.codes = codes;
        this.name = name;
    }
    
    /**
     * Võta haigla kood
     * @return Tagastab haigla koodide vahemiku stringina. Näiteks 001-010
     */
    public String getHospitalCode() {
        return this.codes;
    }
    
    /**
     * Võta haigla nimi
     * @return Tagastab haigla nime vastavalt koodide vahemikule. Näiteks Pärnu haigla
     */
    public String getHospitalName() {
        return this.name;
    }
}
