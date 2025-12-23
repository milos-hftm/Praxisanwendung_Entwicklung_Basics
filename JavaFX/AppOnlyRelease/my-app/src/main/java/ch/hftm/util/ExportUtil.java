package ch.hftm.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.function.Function;

public class ExportUtil {

    /**
     * Exportiert eine Liste von Objekten als CSV-Datei
     *
     * @param file Zieldatei (File-Objekt)
     * @param items Liste der zu exportierenden Objekte
     * @param headers Spaltenüberschriften
     * @param rowMapper Funktion, die ein Objekt auf eine Liste von Strings
     * abbildet
     * @return true bei Erfolg, false bei Fehler
     */
    public static <T> boolean exportCsv(File file,
            List<T> items,
            List<String> headers,
            Function<T, List<String>> rowMapper) {
        if (items == null || items.isEmpty()) {
            return false;
        }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, StandardCharsets.UTF_8))) {
            // Header
            writer.write(String.join(",", headers.stream().map(ExportUtil::safeCsv).toList()));
            writer.newLine();
            // Rows
            for (T item : items) {
                List<String> row = rowMapper.apply(item);
                writer.write(String.join(",", row.stream().map(ExportUtil::safeCsv).toList()));
                writer.newLine();
            }
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * Exportiert eine Liste von Objekten als CSV-Datei (Legacy-Signatur mit
     * Dateiname)
     *
     * @param filename Dateiname (String)
     * @param items Liste der zu exportierenden Objekte
     * @param headers Spaltenüberschriften
     * @param mappers Liste von Funktionen, die jeweils einen String aus dem
     * Objekt extrahieren
     * @return File-Objekt der erstellten Datei
     * @throws IOException bei Fehler
     */
    public static <T> File exportCsv(String filename,
            List<T> items,
            List<String> headers,
            List<Function<T, String>> mappers) throws IOException {
        if (items == null || items.isEmpty()) {
            throw new IOException("Keine Daten zum Exportieren vorhanden.");
        }
        if (headers.size() != mappers.size()) {
            throw new IllegalArgumentException("Headers und Mappers müssen gleich viele Einträge haben.");
        }
        File file = new File(filename);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, StandardCharsets.UTF_8))) {
            // Header
            writer.write(String.join(",", headers.stream().map(ExportUtil::safeCsv).toList()));
            writer.newLine();
            // Rows
            for (T item : items) {
                StringBuilder row = new StringBuilder();
                for (int i = 0; i < mappers.size(); i++) {
                    String value = safeCsv(mappers.get(i).apply(item));
                    row.append(value);
                    if (i < mappers.size() - 1) {
                        row.append(',');
                    }
                }
                writer.write(row.toString());
                writer.newLine();
            }
        }
        return file;
    }

    private static String safeCsv(String s) {
        if (s == null) {
            return "";
        }
        boolean needsQuotes = s.contains(",") || s.contains("\n") || s.contains("\r") || s.contains("\"");
        String escaped = s.replace("\"", "\"\"");
        return needsQuotes ? '"' + escaped + '"' : escaped;
    }
}
