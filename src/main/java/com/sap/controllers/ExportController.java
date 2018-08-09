package com.sap.controllers;

import com.sap.exceptions.SAPBadRequestException;
import com.sap.exceptions.SAPException;
import com.sap.model.ExportWorkforceContent;
import com.sap.model.PdfData;
import com.sap.model.Workforce;
import com.sap.processor.FileProcessor;
import com.sap.services.ExportService;
import com.sap.services.WorkforceService;
import com.sap.utils.XmlConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.sap.config.Constants.*;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@RestController
@RequestMapping("/export")
public class ExportController {


    private static final Logger log = LoggerFactory.getLogger(ExportController.class);
    private final ExportService exportService;
    private final FileProcessor fileProcessor;
    private final WorkforceService workforceService;
    private final XmlConverter xmlConverter;


    @Autowired
    public ExportController(ExportService exportService, FileProcessor fileProcessor,
                            WorkforceService workforceService, XmlConverter xmlConverter) {
        this.exportService = exportService;
        this.fileProcessor = fileProcessor;
        this.workforceService = workforceService;
        this.xmlConverter = xmlConverter;
    }


    @PostMapping
    public ResponseEntity<InputStreamResource> exportFile(@RequestBody ExportWorkforceContent content) {
        log.info("Export file endpoint called...");

        if (content.getIds().isEmpty()) {
            throw new SAPBadRequestException("ID list must not be empty!");
        }

        if (isNull(content.getSignature())) {
            throw new SAPBadRequestException("Signature must not be null!");
        }


        final List<Workforce> workforces = content.getIds().stream()
                .map(workforceService::getById)
                .filter(hasId())
                .collect(Collectors.toList());

        if (isPdf(content.getFormat())) {
            final PdfData data = PdfData.create(workforces, content.getSignature());

            try {
                final String xmlData = xmlConverter.convertToXml(data);
                log.info("xml data: " + xmlData);
                final String encodedXmlData = Base64.getEncoder().encodeToString(xmlData.getBytes());
                final String encodedXdpTemplate = fileProcessor.encodeBase64("CustomFormTemplate.xdp");
                final String pdfContent = exportService.exportToPdf(encodedXdpTemplate, encodedXmlData);


                final File file = new File(FILE_NAME + "." + PDF_FORMAT);
                final FileOutputStream fos = new FileOutputStream(file);

                fos.write(Base64.getDecoder().decode(pdfContent));
                fos.close();

                final InputStreamResource isr = new InputStreamResource(new FileInputStream(file));

                log.info("Exporting pdf finished successfully...");
                return new ResponseEntity<>(isr, buildHeaders(file, APPLICATION_PDF), HttpStatus.OK);
            } catch (IOException e) {
                throw new SAPException("XML conversion failed...");
            }
        } else if (isExcel(content.getFormat())) {
            final File file = exportService.exportToExcel(workforces, FILE_NAME);
            try {

                final InputStreamResource isr = new InputStreamResource(new FileInputStream(file));

                return new ResponseEntity<>(isr, buildHeaders(file, APPLICATION_BIFF), HttpStatus.OK);
            } catch (IOException e) {
                throw new SAPException("Writing to excel file failed...");
            }
        } else {
            throw new SAPBadRequestException("Format: " + content.getFormat() + " is not supported!");
        }
    }

    private boolean isExcel(String format) {
        return format.equalsIgnoreCase(XLSX_FORMAT) || format.equalsIgnoreCase(XLS_FORMAT);
    }

    private Predicate<Workforce> hasId() {
        return wf -> nonNull(wf.getId())
                && !wf.getId().isEmpty();
    }


    private boolean isPdf(String format) {
        return format.equalsIgnoreCase(PDF_FORMAT);
    }


    private HttpHeaders buildHeaders(File file, String contentType) {

        final HttpHeaders respHeaders = new HttpHeaders();
        respHeaders.add(CONTENT_TYPE, contentType);
        respHeaders.add(CONTENT_DISPOSITION, ATTACHMENT_FILENAME + file.getName());
        respHeaders.setContentLength(file.length());
        return respHeaders;
    }

}
