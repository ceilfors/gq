/*
 * Copyright 2016 Wisen Tanasa
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
    void exceptionThrown(ExceptionInfo exceptionInfo) {
        methodCallStackSize--
    }

    @Override
    Object expressionProcessed(ExpressionInfo expressionInfo) {
        file.append(" " * (methodCallStackSize * 2))
        file.append("${expressionInfo.methodName}: ${expressionInfo.text}=${expressionInfo.value}")
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
