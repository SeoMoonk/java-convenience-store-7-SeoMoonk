package global.constants;

import static global.constants.GlobalStatic.ERROR_MSG_PREFIX;

public enum GlobalErrorCode {
    FILE_SPECIFIC_ERROR("파일의 경로나 내용이 올바르지 않습니다"),
    FILE_CONTENTS_INVALID("파일의 내용이 올바르지 않습니다."),

    CANNOT_PARSING_NUMBER("파일의 내용을 숫자로 변환할 수 없습니다."),
    CANNOT_PARSING_DATETIME("파일의 데이터를 날짜로 변환할 수 없습니다"),

    FILE_KEY_FORMAT_NOT_MATCHED_PRESET("파일의 키 형식이 사전 설정과 일치하지 않습니다."),
    FILE_KEY_COUNT_NOT_MATCHED_PRESET("파일의 키 갯수가 설정 내용과 일지하지 않습니다");

    private String msg;

    GlobalErrorCode(String msg) {
        this.msg = msg;
    }

    public String getMsgWithPrefix() {
        return ERROR_MSG_PREFIX + msg;
    }
}
