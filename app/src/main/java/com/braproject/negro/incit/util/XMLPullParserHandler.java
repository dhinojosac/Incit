package com.braproject.negro.incit.util;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by negro on 10-08-2015.
 */
public class XMLPullParserHandler {
    List<Incidente> incidentes;
    private Incidente incidente;
    private String text;

    public XMLPullParserHandler() {
        incidentes = new ArrayList<Incidente>();
    }

    public List<Incidente> getIncidentes() {
        return incidentes;
    }

    public List<Incidente> parse(InputStream is) {
        XmlPullParserFactory factory = null;
        XmlPullParser parser = null;
        try {
            factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            parser = factory.newPullParser();

            parser.setInput(is, null);

            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                String tagname = parser.getName();
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        if (tagname.equalsIgnoreCase("Incidencia")) {
                            // crea una nueva instancia de incidente
                            incidente = new Incidente();
                        }
                        break;

                    case XmlPullParser.TEXT:
                        text = parser.getText();
                        break;

                    case XmlPullParser.END_TAG:
                        if (tagname.equalsIgnoreCase("Incidencia")) {
                            // agrega un objeto incidente a la lista de incidentes
                            incidentes.add(incidente);
                        } else if (tagname.equalsIgnoreCase("Titulo")) {
                            incidente.setTitulo(text);
                        } else if (tagname.equalsIgnoreCase("Subtitulo")) {
                            incidente.setSubtitulo(text);
                        } else if (tagname.equalsIgnoreCase("Latitud")) {
                            incidente.setLatitud(text);
                        } else if (tagname.equalsIgnoreCase("Longitud")) {
                            incidente.setLongitud(text);
                        } else if (tagname.equalsIgnoreCase("Categoria")) {
                            incidente.setCategoria(text);
                        } else if (tagname.equalsIgnoreCase("TransporteAfectado")) {
                            incidente.setTransporteAfectado(text);
                        }
                        break;

                    default:
                        break;
                }
                eventType = parser.next();
            }

        } catch (XmlPullParserException | IOException e) {
            e.printStackTrace();
        }

        return incidentes;
    }
}
