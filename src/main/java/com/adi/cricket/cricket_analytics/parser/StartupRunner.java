package com.adi.cricket.cricket_analytics.parser;

import com.adi.cricket.cricket_analytics.dto.CricsheetMatch;
import com.adi.cricket.cricket_analytics.service.CricsheetImportService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.Arrays;

@Component
@RequiredArgsConstructor
public class StartupRunner implements CommandLineRunner {

    private final CricsheetImportService importService;

    @Override
    public void run(String... args) throws Exception {

        ObjectMapper mapper = new ObjectMapper();
        File folder = new File("data");
        File[] files =
                folder.listFiles((dir, name) ->
                        name.endsWith(".json"));

        if (files == null) {
            System.out.println(
                    "No Cricsheet data folder found"
            );

            return;
        }

        Arrays.sort(files);

        for(File file : files)
        {
            CricsheetMatch match =
                    mapper.readValue(
                            file,
                            CricsheetMatch.class);
            String fileName = file.getName();
            String matchId =
                    fileName.replace(".json", "");

            importService.importMatch(match, matchId);
        }
    }
}
