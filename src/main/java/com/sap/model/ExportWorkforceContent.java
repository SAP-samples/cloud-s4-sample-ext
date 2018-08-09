package com.sap.model;

import lombok.Data;

import java.util.Collections;
import java.util.List;

@Data
public class ExportWorkforceContent {

    private final List<String> ids;
    private final String signature;
    private final String format;


    private ExportWorkforceContent(List<String> ids, String signature, String format) {
        this.ids = ids;
        this.signature = signature;
        this.format = format;
    }

    private ExportWorkforceContent() {
        ids = Collections.emptyList();
        signature = "";
        format = "";

    }

    public static ExportWorkforceContent create(List<String> ids, String signature, String format) {
        return new ExportWorkforceContent(ids, signature, format);
    }
}
