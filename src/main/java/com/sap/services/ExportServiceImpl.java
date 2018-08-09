package com.sap.services;

import com.sap.exceptions.SAPException;
import com.sap.model.Workforce;
import com.sap.utils.ExceptionUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

import static com.sap.config.Constants.XLSX_FORMAT;

@Component
public class ExportServiceImpl implements ExportService {


    private static final String API = "/ads.restapi/v1";
    private static final String ADS_RENDER_PDF = "/adsRender/pdf";
    private static final String FORM_TYPE = "print";
    private static final String TAGGED_PDF = "1";
    private static final String EMBED_FONT = "0";
    private static final Logger log = LoggerFactory.getLogger(ExportServiceImpl.class);
    private final RestTemplate restTemplate;
    private final OAuthClient oAuthClient;

    @Value("${adobe.url}")
    private String adobeUrl;

    @Autowired
    public ExportServiceImpl(RestTemplate restTemplate, OAuthClient oAuthClient) {
        this.restTemplate = restTemplate;
        this.oAuthClient = oAuthClient;
    }


    @Override
    public String exportToPdf(String encodedXdpTemplate, String encodedXmlData) {
        //<editor-fold desc="Log info">
        log.info("Sending HTTP Request to ADS Service...");
        //</editor-fold>

        failIfNulls(encodedXdpTemplate, encodedXmlData);

        final ResponseEntity<String> response = restTemplate
                .exchange(getEndpointUrl(), HttpMethod.POST, buildRequest(encodedXdpTemplate, encodedXmlData), String.class);

        failIfResponseIsNotOk(response);

        final String fileContent = "fileContent";
        final String responseBody = new JSONObject(response.getBody()).get(fileContent).toString();

        //<editor-fold desc="Log info">
        log.info("Request finished successfully...");
        //</editor-fold>
        return responseBody;
    }

    private String getEndpointUrl() {
        return adobeUrl + API + ADS_RENDER_PDF;
    }


    @Override
    public File exportToExcel(List<Workforce> workforces, String fileName) {
        final Workbook workbook;
        try {
            File file = new File(fileName + "." + XLSX_FORMAT);
            workbook = new XSSFWorkbook();

            FileOutputStream fos = new FileOutputStream(file);


            Sheet sheet = workbook.createSheet("Time Recording data");

            Row row = sheet.createRow(0);

            addHeaders(row);

            addEmployeNo(workforces, row);


            workforces.forEach(wf -> writeWorkforce(wf, sheet, workforces.indexOf(wf) + 1));

            row = sheet.createRow(workforces.size() + 1);

            addTotalHours(workforces, row);

            addEmployeeName(workforces, sheet);

            autoSizeColumns(sheet);
            workbook.write(fos);
            workbook.close();

            return file;
        } catch (IOException e) {
            e.printStackTrace();
            throw new SAPException("Export to excel failed!");
        }
    }

    private void addTotalHours(List<Workforce> workforces, Row row) {
        setBorder(row.createCell(7, CellType.STRING))
                .setCellValue("Total: " + workforces
                        .stream().map(Workforce::getRecordedHours)
                        .reduce(BigDecimal.ZERO, BigDecimal::add));
    }

    private void addEmployeeName(List<Workforce> workforces, Sheet sheet) {
        setBorder(sheet.getRow(1)
                .createCell(9, CellType.STRING))
                .setCellValue("Employee Name: " + workforces.stream()
                        .findFirst()
                        .map(Workforce::getBusinessPartnerFullName)
                        .orElse(""));
    }

    private void addEmployeNo(List<Workforce> workforces, Row row) {
        setBorder(row.createCell(9, CellType.STRING))
                .setCellValue("Employee No.: " + workforces.stream()
                        .findFirst()
                        .map(Workforce::getPersonWorkAgreement)
                        .orElse(""));
    }

    private void addHeaders(Row row) {
        setBorder(row.createCell(0, CellType.STRING)).setCellValue("#");
        setBorder(row.createCell(1, CellType.STRING)).setCellValue("Date");
        setBorder(row.createCell(2, CellType.STRING)).setCellValue("Customer");
        setBorder(row.createCell(3, CellType.STRING)).setCellValue("Project");
        setBorder(row.createCell(4, CellType.STRING)).setCellValue("Work Package");
        setBorder(row.createCell(5, CellType.STRING)).setCellValue("Work Item");
        setBorder(row.createCell(6, CellType.STRING)).setCellValue("Timesheet Note");
        setBorder(row.createCell(7, CellType.STRING)).setCellValue("Recorded Hours");
    }

    private HttpEntity<String> buildRequest(String encodedXdpTemplate, String encodedXmlData) {

        log.info("Building request to Form service by adobe...");

        final JSONObject body = new JSONObject();

        body.put("xdpTemplate", encodedXdpTemplate);
        body.put("xmlData", encodedXmlData);
        body.put("formType", FORM_TYPE);
        body.put("taggedPDF", TAGGED_PDF);
        body.put("embedFont", EMBED_FONT);

        log.info("Building request finished successfully...");
        return new HttpEntity<>(body.toString(), buildHeaders());
    }

    private void failIfResponseIsNotOk(ResponseEntity<String> response) {
        if (!isOk(response)) {
            throw SAPException.create("HTTP Request to ADS Service failed! " + response);
        }
    }

    private HttpHeaders buildHeaders() {

        final HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + oAuthClient.getToken());
        headers.add("Content-Type", "application/json");
        return headers;
    }

    private boolean isOk(ResponseEntity<String> response) {
        return response.getStatusCode().equals(HttpStatus.OK);
    }

    private void failIfNulls(String encodedXdpTemplate, String encodedXmlData) {
        ExceptionUtils.nullCheck(encodedXdpTemplate, "XDP template must not be null!");
        ExceptionUtils.nullCheck(encodedXmlData, "XML data must not be null!");
    }

    private void autoSizeColumns(Sheet sheet) {
        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(1);
        sheet.autoSizeColumn(2);
        sheet.autoSizeColumn(3);
        sheet.autoSizeColumn(4);
        sheet.autoSizeColumn(5);
        sheet.autoSizeColumn(6);
        sheet.autoSizeColumn(7);
        sheet.autoSizeColumn(8);
        sheet.autoSizeColumn(9);

    }

    private void writeWorkforce(Workforce wf, Sheet sheet, int index) {

        final Row row = sheet.createRow(index);
        setBorder(row.createCell(0)).setCellValue(index);
        setBorder(row.createCell(1, CellType.STRING)).setCellValue(wf.getCalendarDate().toString());
        setBorder(row.createCell(2, CellType.STRING)).setCellValue(wf.getCustomerFullName());
        setBorder(row.createCell(3, CellType.STRING)).setCellValue(wf.getEngagementProjectName());
        setBorder(row.createCell(4, CellType.STRING)).setCellValue(wf.getWorkPackageName());
        setBorder(row.createCell(5, CellType.STRING)).setCellValue(wf.getWorkItemName());
        setBorder(row.createCell(6, CellType.STRING)).setCellValue(wf.getTimeSheetNote());
        setBorder(row.createCell(7, CellType.NUMERIC)).setCellValue(wf.getRecordedHours().toString());

    }


    private Cell setBorder(Cell cell) {
        final CellStyle cellStyle = cell.getSheet().getWorkbook().createCellStyle();
        cellStyle.setBorderLeft(BorderStyle.THIN);
        cellStyle.setBorderRight(BorderStyle.THIN);
        cellStyle.setBorderTop(BorderStyle.THIN);
        cellStyle.setBorderBottom(BorderStyle.THIN);
        cell.setCellStyle(cellStyle);
        return cell;
    }
}
