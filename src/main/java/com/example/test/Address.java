package com.example.test;


import lombok.extern.slf4j.Slf4j;
import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.libpostal.libpostal_address_parser_options_t;
import org.bytedeco.libpostal.libpostal_address_parser_response_t;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.bytedeco.libpostal.global.postal.*;

@Slf4j
public class Address {

    public static void main(String[] args) throws IOException, InterruptedException {

        boolean dataLibrariesLoaded = LipostalDataLoader.loadDataLibrary();
        if (dataLibrariesLoaded) {
            log.info("Starting to parse and normalize streets");
            String address = "1142 BELLVIEW DR WISCONSIN RAPIDS WI 54494-9293 USA";
            for (int i = 0; i < 10000; i++) {
                normalizeAddress(address);
                parseAddress(address);
            }
            log.info("Ending the street parsing");
        }
    }

    private static String normalizeAddress(String address) throws UnsupportedEncodingException {
        try (BytePointer addressPointer = new BytePointer(address);
             BytePointer normalizedPointer = libpostal_normalize_string(addressPointer, address.length())) {

            return normalizedPointer.getString();
        }
    }

    private static Map<String, String> parseAddress(String addressStr) throws UnsupportedEncodingException {

        try (libpostal_address_parser_options_t options = libpostal_get_address_parser_default_options();
             BytePointer address = new BytePointer(addressStr, UTF_8);
             libpostal_address_parser_response_t response = libpostal_parse_address(address, options)) {

            long count = response.num_components();
            Map<String, String> fields = new HashMap<>();

            for (int i = 0; i < count; i++) {
                try (BytePointer label = response.labels(i);
                     BytePointer component = response.components(i)) {
                    fields.put(label.getString(), component.getString());
                }
            }

            libpostal_address_parser_response_destroy(response);

            return fields;
        }
    }

}
