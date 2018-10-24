# PersonalCode
Eesti isikukoodi kontrollimiseks, info saamiseks ja juhusliku isikukoodi genereerimiseks

Isikukoodi kontrollimiseks ja info saamiseks

String personalcode = "48105215716";
PersonalCode pc = new PersonalCode(personalcode);
if(pc.getCodeIsValid()) {
  for(int i = 0; i < pc.getPersonalCodeParts().size(); i++) {
    System.out.println(pc.getPersonalCodeParts().get(i).getField() + " " + pc.getPersonalCodeParts().get(i).getResult());
  }
} else {
  System.out.println(pc.getErrorText());
}

Isikukoodi genereerimiseks soo, sünniaja ja haigla koodiga

String gender = "w";                // Sugu m/w või M/W
String birthDate = "21.05.1981";    // Sünniaeg kujul DD.MM.YYYY
int hospitalCode = 10;              // Haiglate kood 10 => Valga haigla. Number vahemikus 0 - 13 k.a. 13 => Välismaalane
        
PersonalCode pcGen = new PersonalCode(gender, birthDate, hospitalCode);
if(!pcGen.getError()) {
  System.out.println(pcGen.getPersonalCode());
}

Isikukoodi genereerimiseks aastate vahemikus

int start = 1981;
int end = 1981;
PersonalCode pcYear = new PersonalCode(start, end);
if(!pcYear.getError()) {
    System.out.println("Isikukood aastate " + start + " ja " + end + " vahel k.a. => " + pcYear.getPersonalCode());
}
