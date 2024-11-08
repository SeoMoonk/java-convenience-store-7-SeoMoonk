package global.dto.response;

import java.util.List;

public record FileParsedResponse(
        List<String> keys,

        List<List<String>> values
) {}