package global.utils;

import global.dto.response.FileParsedResponse;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class FileParser {

    public static FileParsedResponse parsingByFilePath(String filePath) {
        List<String> keys = new ArrayList<>();
        List<List<String>> values = new ArrayList<>();
        try {
            BufferedReader br = new BufferedReader(new FileReader(filePath));
            keys = parsingKeysByFirstLine(br.readLine());
            values = parsingValues(br);
        } catch (Exception e) {
            System.out.println("파일의 내용이 올바르지 않습니다.");   //FIXME
        }
        return new FileParsedResponse(keys, values);
    }

    private static List<String> parsingKeysByFirstLine(String firstLine) {
        List<String> titles = new ArrayList<>();
        String[] splitFirstLine = firstLine.split(",");
        Collections.addAll(titles, splitFirstLine);
        return titles;
    }

    private static List<List<String>> parsingValues(BufferedReader br) {
        String line;
        List<List<String>> values = new ArrayList<>();
        try {
            while((line = br.readLine()) != null) {
                values.add(Arrays.asList(line.split(",")));
            }
        } catch (IOException e) {
            System.out.println("파일의 내용이 올바르지 않습니다.");
        }
        return values;
    }
}