package espenotlo.jaba;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CsvManager {
    private File csvOutputFile;
    private String saveFileDirectory;

    /**
     * Creates a new instance of the class.
     */
    public CsvManager() {
        //Intentionally empty
    }

    /**
     * Converts a string array to a .csv-friendly format.
     * @param data {@code String[]} of data to be converted.
     * @return {@code String} of converted data.
     */
    public String convertToCsv(String[] data) {
        return Stream.of(data)
                .map(this::escapeSpecialCharacters)
                .collect(Collectors.joining(";"));
    }

    /**
     * Encapsulates semicolons and double quotes in double quotes,
     * and replaces new lines with whitespace.
     * This all to make the String .csv-friendly.
     * @param data {@code String} of data to be converted.
     * @return {@code String} of .csv-compatible data.
     */
    public String escapeSpecialCharacters(String data) {
        String escapedData = data.replaceAll("\\R", " ");
        if (data.contains(";") || data.contains("\"") || data.contains("'")) {
            data = data.replace("\"", "\"\"");
            escapedData = "\"" + data + "\"";
        }
        return escapedData;
    }

    /**
     * Creates a new .csv file in the given directory.
     * @param fileName {@code String} name of the file to be created.
     * @return {@code boolean} true if the file was created,
     *      or {@code boolean} false if the file already exists.
     */
    public boolean createCsvFile(String fileName) {
        boolean success = false;
        try {
            csvOutputFile = new File(saveFileDirectory,fileName + ".csv");
            if (csvOutputFile.createNewFile()) {
                success = true;
            }
        } catch (IOException io) {
            io.printStackTrace();
        }
        return success;
    }

    /**
     * Sets the directory where new files will be created.
     * @param url the directory where new files will be created.
     */
    public void setSaveFileDirectory(String url) {
        this.saveFileDirectory = url;
    }

    /**
     * Writes the given list of string to this object's csvOutPutFile, in a .csv format.
     * @param dataList {@code List<String[]>} of data to be written to file.
     */
    public void writeToCsv(List<String[]> dataList) {
        try (BufferedWriter bw = new BufferedWriter((
                new OutputStreamWriter(
                        new FileOutputStream(csvOutputFile), StandardCharsets.UTF_8)))) {
            for (String[] strings : dataList) {
                String s = convertToCsv(strings);
                bw.write(s);
                bw.newLine();
            }
        } catch (IOException io) {
            io.printStackTrace();
        }
    }

    /**
     * Parses a .csv file and returns it in a list format.
     * @param file the file to be imported.
     * @return {@code List<List<String} of the content of the .csv file, returns an empty list if no file is chosen,
     *      and returns a List with one empty String if unable to parse file.
     * @throws ArrayIndexOutOfBoundsException if file is of wrong format.
     */
    public Budget importCsv(File file) throws ArrayIndexOutOfBoundsException {
        Budget returnBudget = null;
        ArrayList<List<String>> records = new ArrayList<>();
        Charset charset = StandardCharsets.UTF_8;
        if (null != file) {
            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(new FileInputStream(file), charset))) {
                String line;
                while ((line = br.readLine()) != null) {
                    String[] values = line.split(";");
                    records.add(Arrays.asList(values));
                }
            } catch (IOException io) {
                return null;
            }
            Budget budget = new Budget();
            records.forEach(list -> budget.addTransaction(new Transaction(LocalDate.parse(list.get(0)), list.get(1), list.get(2), Integer.parseInt(list.get(3)))));

            returnBudget = budget;
        }
        return returnBudget;
    }

    /**
     * Parses all .csv files in given directory and returns them as a list of Projects.
     * @param directory {@code File} directory to be imported.
     * @return {@code ArrayList<Project>} of projects.
     * @throws IOException if unable to read a file.
     */
    public List<Budget> importFolder(File directory) throws IOException {
        List<Budget> budgets = new ArrayList<>();
        Path dir = directory.toPath();
        DirectoryStream<Path> stream =
                Files.newDirectoryStream(dir, "*.csv");
        for (Path entry : stream) {
            budgets.add(importCsv(entry.toFile()));
        }
        stream.close();
        return budgets;
    }
}
