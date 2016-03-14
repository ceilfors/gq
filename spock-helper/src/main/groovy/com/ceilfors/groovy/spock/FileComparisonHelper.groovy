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

package com.ceilfors.groovy.spock

import org.spockframework.runtime.Condition
/**
 * @author ceilfors
 */
class FileComparisonHelper {

    /**
     * Compares the specified file with Spock's comparison mechanism but with an additional
     * information added to the top of the error message. This added information is added
     * to help developers' eye compare multi-line String failures.
     *
     * @param file the file which content to be asserted
     * @param expectedContent the expected content
     */
    static void fileContentEquals(File file, String expectedContent) {
        def actualContent = file.text

        // To support cross platform testing as the expected content might be created with \n character
        expectedContent = expectedContent.denormalize()

        if (actualContent != expectedContent) {
            def specificComparison = getSpecificComparisonMessage(actualContent, expectedContent)
            def fullComparison = getSpockStyleComparison('actualContent == expectedContent', actualContent, expectedContent)
            throw new AssertionError("\nSpecific comparison:\n$specificComparison\nFull comparison:\n${fullComparison}")
        }
    }

    private static String getSpecificComparisonMessage(String actualContent, String expectedContent) {
        def actualLines = actualContent.readLines()
        def expectedLines = expectedContent.readLines()

        if (actualLines.size() != expectedLines.size()) {
            return "Number of lines don't match. Actual: [${actualLines.size()}], expected: [${expectedLines.size()}]\n"
        } else {
            for (int i = 0; i < actualLines.size(); i++) {
                def actual = actualLines[i]
                def expected = expectedLines[i]
                if (!actual.equals(expected)) {
                    return "Line [${i + 1}] is not equal:\n${getSpockStyleComparison('actualLine == expectedLine', actual, expected)}\n"
                }
            }
        }

        throw new IllegalStateException('Could not find difference in text file!')
    }

    private static String getSpockStyleComparison(String text, String value1, String value2) {
        return new Condition([value1, value2, false], text, null, null).getRendering()
    }
}
