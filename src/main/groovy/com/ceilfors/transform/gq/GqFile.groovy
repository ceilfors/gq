package com.ceilfors.transform.gq

/**
 * @author ceilfors
 */
class GqFile implements CodeFlowListener {

    public static String TEMP_DIR = "GQTMP"

    private int methodCallStackSize = 0
    private String directory

    GqFile(String directory) {
        this.directory = directory
    }

    GqFile() {
    }

    @Override
    void methodStarted(MethodInfo methodInfo) {
        file.append(" " * (methodCallStackSize * 2))
        file.append("${methodInfo.name}(${methodInfo.args.join(", ")})")
        file.append("\n")

        methodCallStackSize++
    }

    @Override
    void methodEnded(Object result) {
        methodCallStackSize--

        file.append(" " * (methodCallStackSize * 2))
        file.append("-> $result")
        file.append("\n")
    }

    @Override
    void methodEnded() {
        methodCallStackSize--
    }

    @Override
    Object expressionProcessed(ExpressionInfo expressionInfo) {
        file.append(" " * (methodCallStackSize * 2))
        file.append("${expressionInfo.text}=${expressionInfo.value}")
        file.append("\n")

        return expressionInfo.value
    }

    File getFile() {
        String gqDir = directory
        if (!gqDir) {
            // By default not using java.io.tmpdir for better user usability
            gqDir = System.getProperty(TEMP_DIR) ? System.getProperty(TEMP_DIR) : "/tmp"
        }

        def file = new File(gqDir, "gq")
        if (!file.exists()) {
            file.createNewFile()
        }
        return file
    }
}
