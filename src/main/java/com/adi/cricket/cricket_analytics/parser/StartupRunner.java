package com.adi.cricket.cricket_analytics.parser;

import com.adi.cricket.cricket_analytics.dto.CricsheetMatch;
import com.adi.cricket.cricket_analytics.service.CricsheetImportService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.Arrays;

@Component
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(
        prefix = "cricket",
        name = "import-on-startup",
        havingValue = "true"
)
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
            log.warn("Cricsheet data folder not found: {}", folder.getAbsolutePath());

            return;
        }

        Arrays.sort(files);
        log.info(
                "Cricsheet startup import enabled; processing {} files from {}",
                files.length,
                folder.getAbsolutePath()
        );

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

        log.info("Cricsheet startup import completed; processed {} files", files.length);
    }
}
