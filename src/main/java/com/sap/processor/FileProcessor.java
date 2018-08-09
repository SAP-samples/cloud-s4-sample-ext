package com.sap.processor;

import org.springframework.stereotype.Service;

@Service
public interface FileProcessor {


    String encodeBase64(String fileName);

    byte[] decodeBase64(String encodedFile);

}
