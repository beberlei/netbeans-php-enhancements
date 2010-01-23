/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.whitewashing.php.cs.command;

import de.whitewashing.php.cs.CodingStandardError;
import de.whitewashing.php.cs.CodingStandardWarning;
import java.io.IOException;
import java.io.Reader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.*;
import org.xml.sax.*;
import javax.xml.parsers.*;

/**
 *
 * @author benny
 */
public class CodeSnifferXmlLogParser {

    CodeSnifferXmlLogResult parse(File fo)
    {
        if(fo == null || fo.exists() == false) {
            return createEmptyResult();
        }

        try {
            return parse(new InputStreamReader(new FileInputStream(fo)));
        } catch(FileNotFoundException e) {
            return createEmptyResult();
        }
    }

    CodeSnifferXmlLogResult parse(Reader reader)
    {
        List<CodingStandardWarning> csWarnings = new ArrayList<CodingStandardWarning>();
        List<CodingStandardError> csErrors = new ArrayList<CodingStandardError>();

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder;
        try {
            builder = factory.newDocumentBuilder();
            Document document;
            document = builder.parse(new InputSource(reader));
            NodeList ndList = document.getElementsByTagName("warning");
            int lineNum = 0;
            for (int i = 0; i < ndList.getLength(); i++) {
                String message = ndList.item(i).getTextContent();
                NamedNodeMap nm = ndList.item(i).getAttributes();
                lineNum = Integer.parseInt(nm.getNamedItem("line").getTextContent()) - 1;
                csWarnings.add(new CodingStandardWarning(message, lineNum));
            }
            ndList = document.getElementsByTagName("error");
            for (int i = 0; i < ndList.getLength(); i++) {
                String message = ndList.item(i).getTextContent();
                NamedNodeMap nm = ndList.item(i).getAttributes();
                lineNum = Integer.parseInt(nm.getNamedItem("line").getTextContent()) - 1;
                csErrors.add(new CodingStandardError(message, lineNum));
            }
        } catch (IOException ex) {
            
        } catch (ParserConfigurationException ex) {
            
        } catch(SAXParseException ex) {
            
        } catch (SAXException ex) {
            
        }

        return new CodeSnifferXmlLogResult(csErrors, csWarnings);
    }

    private CodeSnifferXmlLogResult createEmptyResult()
    {
        List<CodingStandardWarning> csWarnings = new ArrayList<CodingStandardWarning>();
        List<CodingStandardError> csErrors = new ArrayList<CodingStandardError>();

        return new CodeSnifferXmlLogResult(csErrors, csWarnings);
    }
}
