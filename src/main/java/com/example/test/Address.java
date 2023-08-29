package com.example.test;


import com.github.javafaker.Faker;
import lombok.extern.slf4j.Slf4j;
import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.javacpp.Loader;
import org.bytedeco.libpostal.libpostal_address_parser_options_t;
import org.bytedeco.libpostal.libpostal_address_parser_response_t;
import org.bytedeco.libpostal.libpostal_normalize_options_t;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import static org.bytedeco.libpostal.global.postal.*;

@Slf4j
public class Address {

    private static libpostal_address_parser_options_t options = libpostal_get_address_parser_default_options();
    private static libpostal_normalize_options_t normalizeOptions = libpostal_get_default_options();
    private static Faker faker = new Faker();
    public static void main(String[] args) throws IOException, InterruptedException {

        String dataDir = args.length >= 1 ? args[0] : "data/";

        if (!Files.exists(Paths.get(dataDir))) {
            String libpostal_data = Loader.load(org.bytedeco.libpostal.libpostal_data.class);
            ProcessBuilder pb = new ProcessBuilder("bash", libpostal_data, "download", "all", dataDir);
            pb.inheritIO().start().waitFor();
        }

        boolean setup1 = libpostal_setup_datadir(dataDir);
        boolean setup2 = libpostal_setup_parser_datadir(dataDir);
        boolean setup3 = libpostal_setup_language_classifier_datadir(dataDir);

        if (setup1 && setup2 && setup3) {

            log.info("Starting to parse addresses");
            int count = 0;
            while (count < 1000) {

                String address = faker.address().fullAddress();
                //normalizeAddress(address);
                parseAddress(address);
                count++;
            }
            log.info("Finished parsing addresses");
        } else {
            log.info("Cannot setup libpostal, check if the training data is available at the specified path!");
            System.exit(-1);
        }
    }

    private static String normalizeAddress(String address) {
        return libpostal_normalize_string(address, normalizeOptions.num_languages());
    }

    private static Map<String, String> parseAddress(String addressStr) throws UnsupportedEncodingException {
        try (libpostal_address_parser_options_t options = libpostal_get_address_parser_default_options();
             BytePointer address = new BytePointer(addressStr, "UTF-8");
             libpostal_address_parser_response_t response = libpostal_parse_address(address, options)) {

            long count = response.num_components();
            Map<String, String> fields = new HashMap<>();

            for (int i = 0; i < count; i++) {
                fields.put(response.labels(i).getString(), response.components(i).getString());
            }

            return fields;
        }
    }
}
