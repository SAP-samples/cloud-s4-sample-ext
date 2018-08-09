package com.sap.processor;

import com.sap.exceptions.SAPException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.Objects;

@Component
public class FileProcessorImpl implements FileProcessor {

    private static final Logger log = LoggerFactory.getLogger(FileProcessorImpl.class);

    @Override
    public String encodeBase64(String fileName) {
        log.info("Encoding started...");
        try {
            final Path path = Paths.get(Objects.requireNonNull(getClass().getClassLoader().getResource(fileName)).toURI());
            return Base64.getEncoder().encodeToString(Files.readAllBytes(path));
        } catch (URISyntaxException | IOException e) {
            throw SAPException.create("File " + fileName + " encoding failed!");
        } finally {
            log.info("Encoding finished...");
        }

    }

    @Override
    public byte[] decodeBase64(String encodedFile) {
        log.info("Decoding started...");
        try {
            final Path path = Paths.get(Objects.requireNonNull(getClass().getClassLoader().getResource(encodedFile)).toURI());
            return Base64.getDecoder().decode(Files.readAllBytes(path));
        } catch (URISyntaxException | IOException e) {
            throw SAPException.create("File " + encodedFile + " decoding failed!");
        } finally {
            log.info("Decoding finished...");
        }
    }
}
