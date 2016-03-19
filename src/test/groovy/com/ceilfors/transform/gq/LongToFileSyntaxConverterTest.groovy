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

import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification
import spock.lang.Unroll

import java.util.regex.Matcher

/**
 * @author ceilfors
 */
class LongToFileSyntaxConverterTest extends Specification {

    @Rule
    TemporaryFolder folder = new TemporaryFolder()

    @Unroll
    def "Should shortened #expression to #expectedResult"() {
        when:
        LongToFileSyntaxConverter syntaxConverter = new LongToFileSyntaxConverter(folder.newFolder())
        syntaxConverter.limit = limit
        String result = syntaxConverter.convertExpressionValue(expression)

        then:
        result.startsWith(expectedResult)

        where:
        expression             | limit || expectedResult
        "1"                    | 10    || "1"
        "12345"                | 10    || "12345"
        "1234567890"           | 10    || "1234567890"
        "1234567890"           | 4     || "1..0"
        "12345678901"          | 10    || "1234..8901"
        "123456789012345"      | 9     || "1234..345"
        "123456789012345"      | 11    || "12345..2345"
        "123456789012345"      | 10    || "1234..2345"
        "12345678901234567890" | 10    || "1234..7890"
    }

    def "Should throw exception if limit is too low"() {
        when:
        LongToFileSyntaxConverter syntaxConverter = new LongToFileSyntaxConverter(folder.newFolder())
        syntaxConverter.setLimit(limit)

        then:
        thrown(IllegalArgumentException)

        where:
        limit << [3, 2, -9]
    }

    def "Should store the original expression to file"() {
        setup:
        File directory = folder.newFolder()
        LongToFileSyntaxConverter syntaxConverter = new LongToFileSyntaxConverter(directory)
        syntaxConverter.limit = 5

        when:
        String result = syntaxConverter.convertExpressionValue('1234567890')

        then:
        String fileContent = getFileFromExpression(result).text
        fileContent == '1234567890'
    }

    def "Should not store to file expression if it's not long"() {
        setup:
        LongToFileSyntaxConverter syntaxConverter = new LongToFileSyntaxConverter(folder.newFolder())
        syntaxConverter.limit = 100

        when:
        String result = syntaxConverter.convertExpressionValue('1234567890')

        then:
        Matcher fileLocationMatcher = result =~ ".*\\(file://(.*)\\)"
        !fileLocationMatcher.matches()
    }

    def "Should not override an existing expression file when called more than once"() {
        setup:
        File directory = folder.newFolder()
        LongToFileSyntaxConverter syntaxConverter = new LongToFileSyntaxConverter(directory)
        syntaxConverter.limit = 5

        when:
        String result1 = syntaxConverter.convertExpressionValue('1111111111')
        String result2 = syntaxConverter.convertExpressionValue('2222222222')
        String result3 = syntaxConverter.convertExpressionValue('3333333333')

        then:
        String fileContent1 = getFileFromExpression(result1).text
        fileContent1 == '1111111111'
        String fileContent2 = getFileFromExpression(result2).text
        fileContent2 == '2222222222'
        String fileContent3 = getFileFromExpression(result3).text
        fileContent3 == '3333333333'
    }

    private File getFileFromExpression(String expression) {
        Matcher fileLocationMatcher = expression =~ ".*\\(file://(.*)\\)"
        if (!fileLocationMatcher.matches()) {
            throw new AssertionError("Matcher [${fileLocationMatcher.pattern()}] doesn't match $expression")
        }
        File file = new File(fileLocationMatcher.group(1))
        assert file.exists()
        return file
    }
}
