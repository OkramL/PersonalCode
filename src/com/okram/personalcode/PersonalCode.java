/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.okram.personalcode;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.time.Year;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Random;

/**
 * Isikukoodi kontroll koos seal leiduva infoga
 * @author Marko
 */
public class PersonalCode {
    /**
     * Lubatud miinimum ja maksimum aastat aastate genereerimiseks
     */
    private final int MINYEAR = 1800;                       // Miinimum aasta sünniaja jaoks
    private final int MAXYEAR = Year.now().getValue() - 1;  // Maximum aasta sünniaja jaoks jooksvast aastast miinus 1
    /**
     * Lubatud kuupäeva vorming    
     */    
    private final String DATE_FORMAT = "dd.MM.yyyy";
    
    /**
     * Sisaldab haiglate nimekirja. Koodide vahemik ja haigla(te) nimed
     */
    ArrayList<HospitalsList> hospitals = new ArrayList<>(); 
    
    /**
     * Isikukoodi kontrolliga seotud muutujad
     */
    String personalCode;
    String gender, yearShrt, century, yearLong, month, day, hospital, controlCode;
    int childCounter;
    boolean codeIsValid = false;
    
    /**
     * Isikukoodi tegemiseks vajalikud muutujad
     */
    int orderNr, hospitalStart, hospitalEnd, startYear, endYear;
    String birthDate, strGender;
    
    /**
     * Isikukoodi vigadega seotud muutujad
     */
    boolean wasError = true;    
    String errorText = "";
    
    /**
     * Klassi konstruktor isikukoodi kontrolliks ja kogu info kätte saamiseks
     * @param personalCode 
     */
    public PersonalCode(String personalCode) {
        // Pikkus, sisaldab ainult numbreid, kuupäev on korrektne ja isikukoodi viimane numebr on õige
        if(personalCode.length() == 11 && personalCode.matches("\\d+") && 
                isDateCorrect(personalCode) && isPersonalCodeValid(personalCode)) {
            this.personalCode = personalCode;
            this.gender = personalCode.substring(0, 1);
            this.yearShrt = personalCode.substring(1, 3);
            this.century = getStrCentury(this.personalCode);
            this.yearLong = century + yearShrt;
            this.month = personalCode.substring(3, 5);
            this.day = personalCode.substring(5, 7);
            this.hospital = personalCode.substring(7, 10);
            this.controlCode = personalCode.substring(10, 11);
            setHospitals();
            setChildNumber();
            wasError = false;
            codeIsValid = true;
        } else {
            if (personalCode.length() != 11) {
                System.err.println("Personal code length not 11. Found: " + personalCode.length());
                errorText = "Personal code length not 11. Found: " + personalCode.length();
            } else if (!personalCode.matches("\\d+")) {
                System.err.println("Personal code not a numeric. Found: " + personalCode);
                errorText = "Personal code not a numeric. Found: " + personalCode;
            } else if (!isDateCorrect(personalCode)) {
                System.err.println("Date inside personal code not legal. " + personalCode);
                errorText = "Date inside personal code not legal. " + personalCode;
            } else if (!isPersonalCodeValid(personalCode)) {
                System.err.println("Personal code is not valid. " + personalCode);
                errorText = "Personal code is not valid. " + personalCode;
            }
        }
    }
    
    /**
     * Klassi konstruktor kahe parameetri järgi. Genereeritakse isikukood soo, 
     * sünniaja ja haiga (järjekorra numbri) järgi. Haigla koodid näeb 
     * setHospitals() meetodi kommentaartis
     * 
     * @param gender Soo täht inglise keelne M/m või W/w
     * @param birthDate Sünniaeg kujul DD.MM.YYYY
     * @param orderNr  Haigla kood vahemikus 0 - 13 k.a. 
     */
    public PersonalCode(String gender, String birthDate, int orderNr) {
        if (isGenderCorrect(gender) && isOrderCorrect(orderNr) && isBirthDateCorrect(birthDate) && isCenturyCorrect(birthDate)) {
            this.gender = gender;
            this.orderNr = orderNr;
            this.birthDate = birthDate;
            this.yearLong = birthDate.split("\\.")[2];
            this.yearShrt = birthDate.split("\\.")[2].substring(2, 4);
            this.month = birthDate.split("\\.")[1];
            this.day = birthDate.split("\\.")[0];
            this.century = birthDate.split("\\.")[2].substring(0, 2);
            setHospitals();     // Teeme haiglate nimekirja
            setStrGender();     // Määrame soo numbri 1, 2, 3, 4, 5 või 6
            hospital = getHospitalCode();
            hospitalStart = Integer.parseInt(hospital.split("-")[0]);
            hospitalEnd = Integer.parseInt(hospital.split("-")[1]);
            genCorrectPersonalCode();
            wasError = false;
        } else {
            if (!isGenderCorrect(gender)) {
                System.err.println("Gender in not correct. Accepted M/m or W/w. Found: " + gender);
                errorText = "Gender in not correct. Accepted M/m or W/w. Found: " + gender;
            } else if (!isBirthDateCorrect(birthDate)) {
                System.err.println("Birth is not correct. Accepted format DD.MM.YYYY Found: " + birthDate);
                errorText = "Birth is not correct. Accepted format DD.MM.YYYY Found: " + birthDate;
            } else if (!isOrderCorrect(orderNr)) {
                System.err.println("Hospital order number not correct. Accepted 0 - 12. Found: " + orderNr);
                errorText = "Hospital order number not correct. Accepted 0 - 12. Found: " + orderNr;
            } else if (!isCenturyCorrect(birthDate)) {
                System.err.println("Birth year century not correct. Accepted 18, 19 or 20. Found: " + birthDate.split("\\.")[2].substring(0, 2));
                errorText = "Birth year century not correct. Accepted 18, 19 or 20. Found: " + birthDate.split("\\.")[2].substring(0, 2);
            }
        }
    }
    
    /**
     * Klassi konstruktor kahe parameetri järgi. Genereeritakse juhuslik 
     * isikukood etteantud aastate vahemikus
     *
     * @param startYear algus aasta kaasaarvatud
     * @param endYear lõpu aast akaasaarvatud
     */
    public PersonalCode(int startYear, int endYear) {
        if (isStartEndYearCorrect(1, startYear) && isStartEndYearCorrect(2, endYear)) {
            this.startYear = startYear;
            this.endYear = endYear;
            PersonalCode pcg = new PersonalCode(genRandomGender(), genRandomDate(), randInt(0, 13));
            personalCode = pcg.getPersonalCode();
            wasError = false;
        } else {
            if (!isStartEndYearCorrect(1, startYear)) {
                System.err.println("Birth min year is not correct. Accepted 1800 or later. Found: " + startYear);
                errorText = "Birth min year is not correct. Accepted 1800 or later. Found: " + startYear;
            } else if (!isStartEndYearCorrect(2, endYear)) {
                System.err.println("Birth max year is not correct. Accepted last year or earlier. Found: " + endYear);
                errorText = "Birth max year is not correct. Accepted last year or earlier. Found: " + endYear;
            }
        }
    }
    /**
     * Kas kuupäev on korrektne, et ei tekiks imelikke kuid ja päevasid.
     * @param personalCode Isikukood
     * @return Tagastab True, kui on korrektne ja False kui on vale
     */
    private boolean isDateCorrect(String personalCode) {
        String y = personalCode.substring(1, 3);
        String m = personalCode.substring(3, 5);
        String d = personalCode.substring(5, 7);
        String c = getStrCentury(personalCode);
        if (!c.equals("")) {
            String bDate = d + "." + m + "." + c + y;
            // Kas kokku miksitud sünniaeg (DD.MM.YYYY) on õige
            try {
                DateFormat df = new SimpleDateFormat(DATE_FORMAT);
                df.setLenient(false);
                df.parse(bDate);
                return true;
            } catch (ParseException e) {
                return false;
            }
        }
        return false;
    }
    /**
     * Kontrollib kas kuupäev on õige vorminguga (DD.MM.YYYY) ja õige.
     * 
     * @param date Kuupäev kujul DD.MM.YYYY tekstina
     * @return Tagastab True kui on õige ja False, kui on vale
     */
    
    private boolean isBirthDateCorrect(String date) {
        try {
            DateFormat df = new SimpleDateFormat(DATE_FORMAT);
            df.setLenient(false);
            df.parse(date);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }
    /**
     * Isikukoodi viimase numbri kontroll
     *
     * @param personalCode Isikukood stringina
     * @return True, kui on korrektne, False kui ei ole õige.
     */
    private boolean isPersonalCodeValid(String personalCode) {
        String[] parts = personalCode.split("(?!^)");
        boolean result = false;
        int[] firstTierNumbers = {1, 2, 3, 4, 5, 6, 7, 8, 9, 1};
        int[] secondTierNumbers = {3, 4, 5, 6, 7, 8, 9, 1, 2, 3};
        int total = 0;
        for (int i = 0; i < firstTierNumbers.length; i++) {
            total = total + (firstTierNumbers[i] * Integer.parseInt(parts[i]));
        }
        int jaak = total % 11;
        if (jaak == 10) {
            total = 0;
            for (int i = 0; i < secondTierNumbers.length; i++) {
                total = total + (secondTierNumbers[i] * Integer.parseInt(parts[i]));
            }
            jaak = total % 11;
        }
        if (Integer.parseInt(parts[10]) == jaak) {
            result = true;
        }
        return result;
    }
    
    /**
     * Kontrollib kas sugu on M või m või W või w
     *
     * @param gender Soo täht
     * @return Tagastab True kui sugu on õige ja False, kui sugu on vale
     */
    private boolean isGenderCorrect(String gender) {
        return gender.equalsIgnoreCase("m") || gender.equalsIgnoreCase("w");
    }
    
    /**
     * Kontrollib kas haiglate järjekorranumber on õiges vahemikus
     * 
     * @param orderNr Järjekorranumber
     * @return Tagastab True kui on õige ja False kui on vale
     */
    private boolean isOrderCorrect(int orderNr) {
        return orderNr >= 0 && orderNr <= 13;
    }
    
    /**
     * Kontrollib kas aasta kaks esimest numbrit on korrektsed. 
     * Lubatud vahemik on 18 - 20 k.a.
     * 
     * @param date Kuupäev kujul DD.MM.YYYY tekstina
     * @return Tagastab True, kui on õige ja False, kui on vale
     */
    private boolean isCenturyCorrect(String date) {
        int c = Integer.parseInt(date.split("\\.")[2].substring(0, 2));
        return c >= 18 && c <= 20;
    }
    
    /**
     *
     * @param startEnd 1 on start ja 2 on end
     * @param year aasta mida kontrollida
     * @return Tagastab True, kui on õiges vahemikus ja False kui on vales
     * vahemikus
     */
    private boolean isStartEndYearCorrect(int startEnd, int year) {
        if(startEnd == 1 && year >= MINYEAR) {            
            return true;
        } else return startEnd == 2 && year <= MAXYEAR;
    }
    
    /**
     * Soo põhjal teeb kindlaks sünniaasta ette käiva kahekohalise numbri
     *
     * @return Tagastab sünniaasta ette käiva numbri stringina: "18", "19" või
     * "20"
     */
    private String getStrCentury(String personalCode) {
        int g = Integer.parseInt(personalCode.substring(0, 1));
        if (g > 0 && g < 3) {
            return "18";
        } else if (g > 2 && g < 5) {
            return "19";
        } else if (g > 4 && g < 7) {
            return "20";
        } else {
            return "";
        }
    }
    
    /**
     * Otsib etteantud koodile kindlat haigla nime
     *
     * @param hospitalCode
     * @return
     */
    private String getHospitalName(String hospitalCode) {
        String result = "";
        for (int i = 0; i < hospitals.size(); i++) {
            String[] parts = hospitals.get(i).getHospitalCode().split("-");
            int start = Integer.parseInt(parts[0]);
            int end = Integer.parseInt(parts[1]);
            int code = Integer.parseInt(hospitalCode);
            if (code >= start && code <= end) {
                result = hospitals.get(i).getHospitalName();
            }
        }
        return result;
    }
    /**
     * Meetod mis tagastab korrektse isikukoodi info
     * @return Tagastab List'i<br>
     * 0 - Personalcode (testitav isikukood)<br>
     * 1 - Birthdate (sünniaeg kujul DD.MM.YYYY)<br>
     * 2 - Day (päeva number. Kuni 10. kuupäevani koos nulliga)<br>
     * 3 - Month (kuu number. Kuni oktoobrini koos nulliga)<br>
     * 4 - Full year (aasta neljakohalise numbrina)<br>
     * 5 - Short year (aasta kaks viimast numbrit)<br>
     * 6 - Sex (sugu)<br>
     * 7 - Gender number (soo number)<br>
     * 8 - Hospital code (haigla kood)<br>
     * 9 - Hospital name (haigla nimi)<br>
     * 10 - Control code (viimane number)<br>
     * 11 - Age today (vanus aastates täna. -1 on sünniaeg tulevikus)<br>
     * 12 - Antud päeva mitmes laps (võib-olla küsitav)<br>
     * @throws ParseException 
     */
    
    public List<ResultsList> getPersonalCodeParts() throws ParseException {
        List<ResultsList> result = new ArrayList<>();
        result.add(new ResultsList("Personalcode", personalCode));        
        result.add(new ResultsList("Birth date", day + "." + month + "." + yearLong));
        result.add(new ResultsList("Day", day));
        result.add(new ResultsList("Month", month));
        result.add(new ResultsList("Full year", yearLong));        
        result.add(new ResultsList("Short year", yearShrt));
        result.add(new ResultsList("Sex", setGenderString()));
        result.add(new ResultsList("Gender number", gender));
        result.add(new ResultsList("Hospital code", hospital));
        result.add(new ResultsList("Hospital name", getHospitalName(hospital)));
        result.add(new ResultsList("Control code", controlCode));
        result.add(new ResultsList("Age today", Integer.toString(calcAgeInYears())));
        result.add(new ResultsList("Child # in day", Integer.toString(childCounter)));

        return result;
    }
    
    /**
     * Võtab aigla koodide vahemiku. See on stringina kujul XXX-YYY.
     * @return Tagastab eelpool mainitud numbrite vahemiku tekstina.
     */
    private String getHospitalCode() {
        return hospitals.get(orderNr).getHospitalCode();
    }
    
    /**
     * Teeb esimese osa isikukoodist v.a. kontrollnumber
     * @return Tagastab isikukoodi esimsed 10 numbrit
     */
    private String getFirstPartsPersonalCode() {
        int h = randInt(hospitalStart, hospitalEnd);
        String strH;
        if (h < 10) {
            strH = "00" + h;
        } else if (h < 100) {
            strH = "0" + h;
        } else {
            strH = String.valueOf(h);
        }
        return strGender + yearShrt + month + day + strH;
    }
    
    /**
     * Kirjutab kõik haiglad massiivi<br>
     * 0 - Kuressaare Haigla<br>
     * 1 - Tartu Ülikooli Naistekliinik, Tartumaa, Tartu<br>
     * 2 - Ida-Tallinna Keskhaigla, Pelgulinna sünnitusmaja, Hiiumaa, Keila, Rapla haigla<br>
     * 3 - Ida-Viru Keskhaigla (Kohtla-Järve, endine Jõhvi)<br>
     * 4 - Maarjamõisa Kliinikum (Tartu), Jõgeva Haigla<br>
     * 5 - Narva Haigla<br>
     * 6 - Pärnu Haigla<br>
     * 7 - Pelgulinna Sünnitusmaja (Tallinn), Haapsalu haigla<br>
     * 8 - Järvamaa Haigla (Paide)<br>
     * 9 - Rakvere, Tapa haigla<br>
     * 10 - Valga Haigla<br>
     * 11 - Viljandi Haigla<br>
     * 12 - Lõuna-Eesti Haigla (Võru), Põlva Haigla<br>
     * 13 - Välismaalane<br>
     */
    private void setHospitals() {
        hospitals.add(new HospitalsList("001-010", "Kuressaare Haigla"));
        hospitals.add(new HospitalsList("011-019", "Tartu Ülikooli Naistekliinik, Tartumaa, Tartu"));
        hospitals.add(new HospitalsList("021-220", "Ida-Tallinna Keskhaigla, Pelgulinna sünnitusmaja, Hiiumaa, Keila, Rapla haigla"));
        hospitals.add(new HospitalsList("221-270", "Ida-Viru Keskhaigla (Kohtla-Järve, endine Jõhvi)"));
        hospitals.add(new HospitalsList("271-370", "Maarjamõisa Kliinikum (Tartu), Jõgeva Haigla"));
        hospitals.add(new HospitalsList("371-420", "Narva Haigla"));
        hospitals.add(new HospitalsList("421-470", "Pärnu Haigla"));
        hospitals.add(new HospitalsList("471-490", "Pelgulinna Sünnitusmaja (Tallinn), Haapsalu haigla"));
        hospitals.add(new HospitalsList("491-520", "Järvamaa Haigla (Paide)"));
        hospitals.add(new HospitalsList("521-570", "Rakvere, Tapa haigla"));
        hospitals.add(new HospitalsList("571-600", "Valga Haigla"));
        hospitals.add(new HospitalsList("601-650", "Viljandi Haigla"));
        hospitals.add(new HospitalsList("651-710", "Lõuna-Eesti Haigla (Võru), Põlva Haigla"));
        hospitals.add(new HospitalsList("711-990", "Välismaalane"));
    }
    
    /**
     * Seadistab lapse järjekorranumbri antud päeval. See arvutatakse haiglakoodi põhjal.
     */
    private void setChildNumber() {
        int hospCode = Integer.parseInt(hospital);
        String result = "";
        for (int i = 0; i < hospitals.size(); i++) {
            String code = hospitals.get(i).getHospitalCode();
            int startCode = Integer.parseInt(code.split("-")[0]);
            int endCode = Integer.parseInt(code.split("-")[1]);            
            if (hospCode >= startCode && hospCode <= endCode) {
                if (hospCode == endCode) {
                    this.childCounter = 1;
                } else {
                    this.childCounter = (hospCode - startCode) + 1;
                }
                break;
            }
        }
    }
    
    /**
     * Seadistab soo kas M (paaritu number) või W (paaris number)
     * @return M või W
     */
    private String setGenderString() {
        if (Integer.parseInt(gender) % 2 == 0) {
            return "W";
        } else {
            return "M";
        }
    }
    
    /**
     * Seadistab soo numbri tekstina. Vastavalt aasta kahele esimesele numbrile 
     * ja soole on numbrid vahemikus 1 - 6 k.a. Paaris numbrid on naised ja 
     * paaritud numbrid on mehed.
     */
    private void setStrGender() {
        switch (Integer.parseInt(century)) {
            case 18:
                if (gender.equalsIgnoreCase("M")) {
                    strGender = "1";
                } else {
                    strGender = "2";
                }
                break;
            case 19:
                if (gender.equalsIgnoreCase("M")) {
                    strGender = "3";
                } else {
                    strGender = "4";
                }
                break;
            case 20:
                if (gender.equalsIgnoreCase("M")) {
                    strGender = "5";
                } else {
                    strGender = "6";
                }
                break;
        }
    }
    
    /**
     * Arvutab vanuse aastates
     * @return Vanus täisaastates tänase päeva seisuga. Kui on -1, siis on sünniaeg tulevikus
     * @throws ParseException 
     */
    private int calcAgeInYears() throws ParseException {
        LocalDate today = LocalDate.now();
        LocalDate birthday = LocalDate.of(Integer.parseInt(yearLong), Integer.parseInt(month), Integer.parseInt(day));
        Period p = Period.between(birthday, today);        
        if (p.getYears() > 0) {
            return p.getYears();
        } else {
            return -1;  // Kuupäev on tulevikus, seega pole ta veel sündinud
        }
    }
    
    /**
     * Juhuslik number etteantud vahemiks kaasa arvatud.
     *
     * @param min Miinimum number.
     * @param max Maksimum number.
     * @return Juhuslik number miinimum ja maksimum vahemikus k.a.
     */
    private int randInt(int min, int max) {
        // Usually this can be a field rather than a method variable
        Random rand = new Random();
        // nextInt is normally exclusive of the top value,
        // so add 1 to make it inclusive
        int randomNum = rand.nextInt((max - min) + 1) + min;
        return randomNum;
    }
    
    /**
     * Isikukoodi genereerimine
     *
     */
    private void genCorrectPersonalCode() {
        String noCtrlCode = getFirstPartsPersonalCode();
        personalCode = "";
        boolean makeCtrlCode = true;
        int i = 0;
        while (makeCtrlCode) {
            personalCode = noCtrlCode + String.valueOf(i);
            if (isPersonalCodeValid(personalCode)) {
                //makeCtrlCode = false;
                break;
            }
            i++; // See ei või üle 9 minna, sest siis läheb isikukood pikemaks kui lubatud
            if (i == 10) {
                //System.out.println("Isikukoodi ei saanud teha");
                personalCode = personalCode.substring(0, 10) + "X";
                break;
            }
        }
        //return personalCode;
    }
    
    /**
     * Genereerib juhusliku soo tähe. 
     * @return Tagastab kas (M)an või (W)oman
     */
    private String genRandomGender() {
        if ((randInt(0, 1) % 2) == 0) {
            //System.out.println("W");
            return "W";
        } else {
            //System.out.println("M");
            return "M";
        }
    }
    
    /**
     * Genereerib juhusliku kuupäeva kujul DD.MM.YYYY
     * @return Tagastab korrektse kuupäeva ette antud aastate vahemikust. 
     * Jooksev aasta omasid ei tule!
     */
    private String genRandomDate() {
        GregorianCalendar gc = new GregorianCalendar();
        int year = randInt(startYear, endYear);
        gc.set(GregorianCalendar.YEAR, year);
        int dayOfYear = randInt(1, gc.getActualMaximum(GregorianCalendar.DAY_OF_YEAR));
        gc.set(GregorianCalendar.DAY_OF_YEAR, dayOfYear);
        int d = gc.get(GregorianCalendar.DAY_OF_MONTH);
        int m = (gc.get(GregorianCalendar.MONTH) + 1);
        String strD;
        String strM;
        if (d < 10) {
            strD = "0" + d;
        } else {
            strD = String.valueOf(d);
        }
        if (m < 10) {
            strM = "0" + m;
        } else {
            strM = String.valueOf(m);
        }
        // System.out.println(strD + "." + strM + "." + gc.get(GregorianCalendar.YEAR));
        return strD + "." + strM + "." + gc.get(GregorianCalendar.YEAR);
    }
    
    /**
     * Kas isikukood on korrektne
     * @return True jah, False ei
     */
    public boolean getCodeIsValid() {
        return codeIsValid;
    }
    
    /**
     * Tagastab vea teate tekstina
     * @return Tagastab teksti (String)
     */
    public String getErrorText() {
        return errorText;
    }
    /**
     * Kas isikukood sai tehtud etteantud parameetritega
     *
     * @return True jah, False ei
     */
    public boolean getError() {
        return wasError;
    }
    /**
     * Tagastab andmete põhjal tehtud isikukoodi
     * @return Tagastab isikukoodi tekstina (String)
     */
    public String getPersonalCode() {
        return personalCode;
    }
}
