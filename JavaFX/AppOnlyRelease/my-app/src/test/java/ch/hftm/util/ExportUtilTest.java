package ch.hftm.util;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

class ExportUtilTest {

    @Test
    void exportCsv_fileBased_escapesValues() throws Exception {
        Path tmp = Files.createTempFile("kud-export-", ".csv");
        File file = tmp.toFile();

        List<String> items = List.of("a,b", "x\"y");
        boolean ok = ExportUtil.exportCsv(
                file,
                items,
                List.of("Col1"),
                s -> List.of(s)
        );

        assertTrue(ok);

        List<String> lines = Files.readAllLines(tmp);
        assertEquals("Col1", lines.get(0));
        assertEquals("\"a,b\"", lines.get(1));
        assertEquals("\"x\"\"y\"", lines.get(2));
    }

    @Test
    void exportCsv_legacy_escapesHeadersToo() throws Exception {
        Path tmp = Files.createTempFile("kud-export-legacy-", ".csv");

        ExportUtil.exportCsv(
                tmp.toString(),
                List.of("value"),
                List.of("A,B"),
                List.of(s -> s)
        );

        List<String> lines = Files.readAllLines(tmp);
        assertEquals("\"A,B\"", lines.get(0));
        assertEquals("value", lines.get(1));
    }
}
