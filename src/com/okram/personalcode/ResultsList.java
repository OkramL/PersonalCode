/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.okram.personalcode;

/**
 * Isikukoodi tükeldatud osad Listis
 * @author Marko
 */
public class ResultsList {
    
    String field;
    String result;
    
    /**
     * Klassi konstruktor korrektse isikukoodi andmetega
     * @param field Väljanimi
     * @param result Tulemus vastavalt väljale
     */
    public ResultsList(String field, String result) {
        this.field = field;
        this.result = result;
    }
    
    /**
     * Võta välja nimi
     * @return tagastab välja nime. Näiteks Personalcode
     */
    public String getField() {
        return field;
    }
    
    /**
     * Võta vastavalt väljale tulemus
     * @return Tagastab tulemuse vastavalt väljale. Näiteks 39111194215
     */
    public String getResult() {
        return result;
    }
}
