package ma.emsi.nahhal.tp2texte;

import ma.emsi.nahhal.tp2texte.outil.meteo.MeteoTool;

public class TestMeteo {
    public static void main(String[] args) {
        MeteoTool meteo = new MeteoTool();
        boolean pluiePrevue = meteo.isPluiePrevue(48.85, 2.35, 0, 3);
        System.out.println("Paris — pluie prévue sur les 3 jours : " + pluiePrevue);
    }
}