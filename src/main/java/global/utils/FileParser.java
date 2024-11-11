package global.utils;

import static global.constants.GlobalStatic.ERROR_MSG_PREFIX;
import static global.constants.GlobalStatic.FILE_DATA_SEPARATOR;

import global.constants.FileType;
import global.constants.GlobalErrorCode;
import global.utils.Validator.FileValidator;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileParser {

    public static List<Map<String, String>> parsingByFilePath(String filePath, FileType fileType) {
        List<Map<String, String>> values = new ArrayList<>();
        try {
            BufferedReader br = new BufferedReader(new FileReader(filePath));
            List<String> keys = parsingKeysByFirstLine(br.readLine(), fileType);
            values = parsingValues(keys, br);
            br.close();
        } catch (Exception e) {
            throw new IllegalArgumentException(GlobalErrorCode.FILE_SPECIFIC_ERROR.getMsgWithPrefix());
        }
        return values;
    }

    private static List<String> parsingKeysByFirstLine(String firstLine, FileType fileType) {
        List<String> keys = Arrays.asList(firstLine.split(FILE_DATA_SEPARATOR));
        FileValidator.presetKeyValidate(keys, fileType);
        return keys;
    }

    private static List<Map<String, String>> parsingValues(List<String> keys, BufferedReader br) {
        String line;
        List<Map<String, String>> values = new ArrayList<>();
        try {
            while ((line = br.readLine()) != null) {
                values.add(getDataSet(line, keys));
            }
        } catch (Exception e) {
            throw new IllegalArgumentException(GlobalErrorCode.FILE_CONTENTS_INVALID.getMsgWithPrefix());
        }
        return values;
    }

    private static Map<String, String> getDataSet(String line, List<String> keys) {
        String[] splitLine = line.split(FILE_DATA_SEPARATOR);
        Map<String, String> dataSet = new HashMap<>();
        for (int i = 0; i < keys.size(); i++) {
            dataSet.put(keys.get(i), splitLine[i]);
        }
        return dataSet;
    }
}
