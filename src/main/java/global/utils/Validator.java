package global.utils;

import static global.constants.GlobalStatic.ERROR_MSG_PREFIX;

import global.constants.FileType;
import java.util.List;

public class Validator {

    public static class FileValidator {

        public static void presetKeyValidate(List<String> keys, FileType fileType) {
            List<String> presetKeys = fileType.getPresetKeys(fileType);
            validatePresetKeyCount(keys.size(), presetKeys.size());
            validatePresetKeyOrder(keys, presetKeys);
        }

        private static void validatePresetKeyOrder(List<String> keys, List<String> presetKeys) {
            for (int i = 0; i < presetKeys.size(); i++) {
                if (!keys.get(i).equals(presetKeys.get(i))) {
                    throw new IllegalArgumentException("파일의 키 형식이 설정 내용과 일치하지 않습니다 : " + keys.get(i));
                }
            }
        }

        private static void validatePresetKeyCount(int keyCount, int presetKeyCount) {
            if (presetKeyCount != keyCount) {
                throw new IllegalArgumentException(ERROR_MSG_PREFIX + "파일의 키 갯수가 설정 내용과 일지하지 않습니다 : " + keyCount);
            }
        }
    }
}
