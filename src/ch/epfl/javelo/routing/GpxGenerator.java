package ch.epfl.javelo.routing;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Locale;

/**
 * GpxGenerator class
 * Non-instantiable class that is used to create Gpx files based on a route and an elevation profile
 *
 * @author Arthur Bigot (324366)
 * @author Léo Paoletti (342165)
 */
public class GpxGenerator {

    //non-instantiable class
    private GpxGenerator(){}

    /**
     * Given a route and an elevationProfile, outputs a Gpx Document.
     *
     * @param route Route on which the Gpx Document will be based
     * @param elevationProfile the ElevationProfile corresponding to the route
     * @return the Gpx Document corresponding to the given arguments
     */
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


        Element rte = doc.createElement("rte");
        root.appendChild(rte);

        // The first point of the route is added outside the loop, which only adds points at the end of edges.
        Element rtept = doc.createElement("rtept");
        rte.appendChild(rtept);
        rtept.setAttribute("lat", String.format(Locale.US, "%.5f",
                Math.toDegrees(route.points().get(0).lat())));
        rtept.setAttribute("lon", String.format(Locale.US, "%.5f",
                Math.toDegrees(route.points().get(0).lon())));

        Element ele = doc.createElement("ele");
        rtept.appendChild(ele);
        ele.setTextContent(String.format(Locale.US, "%.2f",elevationProfile.elevationAt(0)));

        // The loop navigates through the list of edges of the route, adding to the Gpx Document the data of the ending
        // point of each edge.
        int position = 0;
        for(Edge edge : route.edges()){

            position += edge.length();
            rtept = doc.createElement("rtept");
            rte.appendChild(rtept);
            rtept.setAttribute("lat",  String.format(Locale.US, "%.5f",
                    Math.toDegrees(edge.toPoint().lat())));
            rtept.setAttribute("lon", String.format(Locale.US, "%.5f",
                    Math.toDegrees(edge.toPoint().lon())));

            ele = doc.createElement("ele");
            rtept.appendChild(ele);
            ele.setTextContent(String.format(Locale.US, "%.2f",elevationProfile.elevationAt(position)));
        }

        return doc; //todo immuability ?
    }

    /**
     * Writes the Gpx Document based on the route and elevationProfile parameters in the fileName file.
     *
     * @param fileName relative path starting from the base folder of the project
     * @param route Route on which the Gpx Document will be based
     * @param elevationProfile the ElevationProfile corresponding to the route
     * @throws IOException  if the fileName exists but is a directory rather than a regular file,
     *                      does not exist but cannot be created, or cannot be opened for any other reason.
     */
    public static void writeGpx(String fileName, Route route, ElevationProfile elevationProfile) throws IOException { //todo quid de catch IOException plutôt ?
        Document doc = createGpx(route, elevationProfile);

        try(Writer w = new FileWriter(fileName)){ //todo try with resources adapté ici ?
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
