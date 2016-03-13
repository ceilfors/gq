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

package com.ceilfors.transform.gq.ast

import com.ceilfors.transform.gq.GqFile
import com.ceilfors.transform.gq.SingletonCodeFlowManager
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import org.spockframework.runtime.ExpressionInfoBuilder
import org.spockframework.runtime.ExpressionInfoRenderer
import org.spockframework.runtime.ExpressionInfoValueRenderer
import org.spockframework.runtime.model.TextPosition
import spock.lang.Specification

/**
 * @author ceilfors
 */
class BaseSpecification extends Specification {

    @Rule
    TemporaryFolder temporaryFolder

    File gqFile

    def setup() {
        gqFile = new File(temporaryFolder.newFolder().absolutePath, "gq")
        SingletonCodeFlowManager.INSTANCE.codeFlowListeners = [new GqFile(gqFile)]
    }

    void gqContentEquals(String expectedContent) {
        // Assign to individual variables to reduce noise in Spock comparison display
        def actualText = gqFile.text
        def expectedText = expectedContent.stripMargin().denormalize()

        // TODO: Move to utility class
        assert actualText == expectedText : {
            def actualLines = actualText.readLines()
            def expectedLines = expectedText.readLines()

            def errorMessage
            if (actualLines.size() != expectedLines.size()) {
                errorMessage = "Number of lines don't match. Actual lines: [${actualLines.size()}], actual lines: [${expectedLines.size()}]"
            } else {
                for (int i = 0; i < actualLines.size(); i++) {
                    def actualLine = actualLines[i]
                    def expectedLine = expectedLines[i]
                    if (!actualLine.equals(expectedLines.get(i))) {
                        errorMessage = "Line [${i + 1}] is not equal:\n"

                        def lineExpression = new ExpressionInfoBuilder('actualLine == expectedLine', TextPosition.create(1, 1), [actualLine, expectedLine, false]).build()
                        ExpressionInfoValueRenderer.render(lineExpression)
                        errorMessage += ExpressionInfoRenderer.render(lineExpression)
                        break
                    }
                }
            }

            // From Spock org.spockframework.runtime.Condition
            def fullExpression = new ExpressionInfoBuilder('actualText == expectedText', TextPosition.create(1, 1), [actualText, expectedText, false]).build()
            ExpressionInfoValueRenderer.render(fullExpression)
            def spockRenderedValue = ExpressionInfoRenderer.render(fullExpression)

            return "$errorMessage\n\nFull difference:\n${spockRenderedValue}"
        }()
    }

    /**
     * Only use this method when it is absolutely necessary. Using this method for test case
     * will drop test readability. Example of good use case: comparing line numbers
     * where it is easier to assert when comparing it against IDE.
     *
     * @param clasz the class to be instantiated with GroovyClassLoader
     * @return the object of the example class
     */
    static <T> T newExample(Class<T> clasz) {
        def file = new File("src/test/groovy/${clasz.package.name.replace('.', '/')}/${clasz.simpleName}.groovy")
        assert file.exists()

        GroovyClassLoader invoker = new GroovyClassLoader()
        def clazz = invoker.parseClass(file)
        return clazz.newInstance() as T
    }

    static toInstance(String text) {
        GroovyClassLoader invoker = new GroovyClassLoader()
        def clazz = invoker.parseClass(text)
        return clazz.newInstance()
    }

    static wrapMethodInClass(String text) {
        return insertPackageAndImport(
                """class Test {
                  |  $text
                  |}""".stripMargin())
    }


    static insertPackageAndImport(String text) {
        return """package com.ceilfors.transform.gq.ast
                 |
                 |import static com.ceilfors.transform.gq.GqSupport.gq
                 |
                 |$text""".stripMargin()
    }
}
