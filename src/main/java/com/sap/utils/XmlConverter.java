package com.sap.utils;

import com.sap.exceptions.SAPException;
import org.springframework.stereotype.Service;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.StringWriter;

@Service
public class XmlConverter {


    public <T> String convertToXml(T object) {
        final JAXBContext context;
        try {
            context = JAXBContext.newInstance(object.getClass());

            final Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            final StringWriter stringWriter = new StringWriter();
            marshaller.marshal(object, stringWriter);

            return stringWriter.toString();
        } catch (JAXBException e) {
            e.printStackTrace();
            throw new SAPException("XML Conversion Failed...");
        }
    }
}
