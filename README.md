# PersonalCode
Eesti isikukoodi kontrollimiseks, info saamiseks ja juhusliku isikukoodi genereerimiseks

<h2>Isikukoodi kontrollimiseks ja info saamiseks</h2>

<pre>
String personalcode = "48105215716";
PersonalCode pc = new PersonalCode(personalcode);
if(pc.getCodeIsValid()) {
  for(int i = 0; i < pc.getPersonalCodeParts().size(); i++) {
    System.out.println(pc.getPersonalCodeParts().get(i).getField() + " " + pc.getPersonalCodeParts().get(i).getResult());
  }
} else {
  System.out.println(pc.getErrorText());
}
</pre>

  <h4>Result</h4>
  <pre>
  Personalcode 48105215716
  Birth date 21.05.1981
  Day 21
  Month 05
  Full year 1981
  Short year 81
  Sex W
  Gender number 4
  Hospital code 571
  Hospital name Valga Haigla
  Control code 6
  Age today 37
  Child # in day 1
  </pre>

<h2>Isikukoodi genereerimiseks soo, sünniaja ja haigla koodiga</h2>

<pre>
String gender = "w";                // Sugu m/w või M/W
String birthDate = "21.05.1981";    // Sünniaeg kujul DD.MM.YYYY
int hospitalCode = 10;              // Haiglate kood 10 => Valga haigla. Number vahemikus 0 - 13 k.a. 13 => Välismaalane
        
PersonalCode pcGen = new PersonalCode(gender, birthDate, hospitalCode);
if(!pcGen.getError()) {
  System.out.println(pcGen.getPersonalCode());
}
</pre>
  <h4>Result<h4>
  Tulemus tuleb erinev, kuna tegemist on juhuslikkusega.
  <pre>
    48105215847
  <pre>

<h2>Isikukoodi genereerimiseks aastate vahemikus</h2>

<pre>
int start = 1981;
int end = 1981;
PersonalCode pcYear = new PersonalCode(start, end);
if(!pcYear.getError()) {
    System.out.println("Isikukood aastate " + start + " ja " + end + " vahel k.a. => " + pcYear.getPersonalCode());
}
</pre>

  <h4>Result<h4>
  <pre>
    Isikukood aastate 1981 ja 1981 vahel k.a. => 38112153961
  <pre>
