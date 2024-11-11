package global.utils;

import global.constants.FileType;
import global.constants.GlobalErrorCode;
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
                    throw new IllegalArgumentException(
                            GlobalErrorCode.FILE_KEY_FORMAT_NOT_MATCHED_PRESET.getMsgWithPrefix() +
                                    " : " + keys.get(i));
                }
            }
        }

        private static void validatePresetKeyCount(int keyCount, int presetKeyCount) {
            if (presetKeyCount != keyCount) {
                throw new IllegalArgumentException(GlobalErrorCode.FILE_KEY_COUNT_NOT_MATCHED_PRESET.getMsgWithPrefix()
                        + " : " + keyCount);
            }
        }
    }
}
