package com.example.test;

import org.bytedeco.javacpp.Loader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.bytedeco.libpostal.global.postal.*;

public class LipostalDataLoader {

    public static boolean loadDataLibrary() throws IOException, InterruptedException {
        String dataDir = "data/";

        if (!Files.exists(Paths.get(dataDir))) {
            String libpostal_data = Loader.load(org.bytedeco.libpostal.libpostal_data.class);
            ProcessBuilder pb = new ProcessBuilder("bash", libpostal_data, "download", "all", dataDir);
            pb.inheritIO().start().waitFor();
        }

        boolean setup1 = libpostal_setup_datadir(dataDir);
        boolean setup2 = libpostal_setup_parser_datadir(dataDir);
        boolean setup3 = libpostal_setup_language_classifier_datadir(dataDir);

        return setup1 && setup2 && setup3;
    }
}
