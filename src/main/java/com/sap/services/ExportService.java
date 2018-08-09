package com.sap.services;

import com.sap.model.Workforce;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;

@Service
public interface ExportService {

    /**
     * @param encodedXdpTemplate Base64 encoded xdp template.
     * @param encodedXmlData     Base64 encoded xml data.
     * @return Base64 encoded PDF.
     */

    String exportToPdf(String encodedXdpTemplate, String encodedXmlData);


    /**
     * @param workforces List of workforce objects to be exported
     * @param fileName   Name of the resulted file.
     * @return Resulted excel file.
     */
    File exportToExcel(List<Workforce> workforces, String fileName);
}
