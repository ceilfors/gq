package com.ceilfors.transform.gq

/**
 * @author ceilfors
 */
@Deprecated
class GqUtils {

    public static String TEMP_DIR = "GQTMP"

    public static <T> T printExpressionToFile(String expression, T value) {
        printToFile("$expression=$value")
        return value
    }

    public static void printToFile(Object value) {
        getGqFile().append("$value\n")
    }

    public static File getGqFile() {
        String directory
        if (System.getProperty(TEMP_DIR)) {
            directory = System.getProperty(TEMP_DIR)
        } else {
            // By default not using java.io.tmpdir for better user usability
            directory = "/tmp"
        }
        return new File(directory, "gq")
    }
}
