package ch.epfl.javelo.routing;

import ch.epfl.javelo.projection.PointCh;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

public class GpxGenerator {

    //non-instantiable class
    private GpxGenerator(){}

    public static Document createGpx(Route route, ElevationProfile elevationProfile){
        Document doc = newDocument(); // see below

        Element root = doc
                .createElementNS("http://www.topografix.com/GPX/1/1",
                        "gpx");
        doc.appendChild(root);

        root.setAttributeNS(
                "http://www.w3.org/2001/XMLSchema-instance",
                "xsi:schemaLocation",
                "http://www.topografix.com/GPX/1/1 "
                        + "http://www.topografix.com/GPX/1/1/gpx.xsd");
        root.setAttribute("version", "1.1");
        root.setAttribute("creator", "JaVelo");

        Element metadata = doc.createElement("metadata");
        root.appendChild(metadata);

        Element name = doc.createElement("name");
        metadata.appendChild(name);
        name.setTextContent("Route JaVelo");

        //todo plutôt faire plusieurs routes dans le cas d'un itinéraire multiroute ?
        Element rte = doc.createElement("rte");
        root.appendChild(rte);

        Element rtept; //todo dans la boucle plutôt ?
        Element ele; // J'ajoute à mon document tous les points de l'itinéraire.
        for(PointCh point : route.points()){
            rtept = doc.createElement("rtept");
            rte.appendChild(rtept);
            rtept.setAttribute("lat",  Double.toString(Math.toDegrees(point.lat())));
            rtept.setAttribute("lon", Double.toString(Math.toDegrees(point.lon())));

            ele = doc.createElement("ele");
            rtept.appendChild(ele);
            double elevationOfPoint = elevationProfile.elevationAt(route.pointClosestTo(point).position()); //Je dois passer par RoutePoint pour avoir la position sur l'itinéraire. Pas fou.
            ele.setTextContent(Double.toString(elevationOfPoint)); //todo on avait pas besoin de l'ElevationProfile ici ?
        }

        return doc; //todo immuability ?
    }

    public static void writeGpx(String fileName, Route route, ElevationProfile elevationProfile) throws IOException { //todo quid de catch IOException plutôt ?
        Document doc = createGpx(route, elevationProfile);


        try(Writer w = new FileWriter(fileName)){ //todo try with resources adapté ici ? Vérifier qu'il ne manque pas le chemin du dossier à fileName.
            Transformer transformer = TransformerFactory
                    .newDefaultInstance()
                    .newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.transform(new DOMSource(doc),
                    new StreamResult(w));
        } catch (TransformerException e) { //todo relire et bien vérifier la technique du prof sur le catch
            throw new Error(e); // Should never happen
        }
    }

    //Creates new document, used in createGpx
    private static Document newDocument() {
        try {
            return DocumentBuilderFactory
                    .newDefaultInstance()
                    .newDocumentBuilder()
                    .newDocument();
        } catch (ParserConfigurationException e) {
            throw new Error(e); // Should never happen
        }
    }

}
