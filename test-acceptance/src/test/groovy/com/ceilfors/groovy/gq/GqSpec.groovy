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

package com.ceilfors.groovy.gq

import spock.lang.Unroll

import static com.ceilfors.groovy.spock.FileComparisonHelper.fileContentEquals

/**
 * @author ceilfors
 */
class GqSpec extends BaseSpecification {

    def "Should return the expression value"() {
        when:
        def result = execute "q(1 + 1)"

        then:
        result == 2
    }

    @Unroll
    def "Should be able to use CALL method: #input"() {
        when:
        execute input

        then:
        fileContentEquals gqFile, "test: $result\n"

        where:
        input      || result
        "q(3 + 5)" || "3 + 5=8"
        "2+q(2)"   || "2=2"
    }

    @Unroll
    def "Should be able to use OR operator: #input"() {
        when:
        execute input

        then:
        fileContentEquals gqFile, "test: $result\n"

        where:
        input                || result
        "q | 3 + 5 "         || "3 + 5=8"
        "q|2+2"              || "2 + 2=4"
        "3 + (q | 5)"        || "5=5"
        "true && q | 'test'" || "'test'='test'"
    }

    @Unroll
    def "Should be able to use DIV operator: #input"() {
        when:
        execute input

        then:
        fileContentEquals gqFile, "test: $result\n"

        where:
        input                  || result
        "q / 3 + 5 "           || "3=3"
        "1 + q / 1"            || "1=1"
        "'test' && q / 'test'" || "'test'='test'"
    }

    def "Should convert multi line variable expression to one line"() {
        when:
        execute """q(1 +
                  |         2 +
                  |         3)"""

        then:
        fileContentEquals gqFile, "test: 1 +         2 +         3=6\n"
    }

    def "Should convert multi line method call expression to one line"() {
        setup:
        def instance = toInstance(wrapMethodInClass(
                """int sum(one, two, three) {
                  |    return q(one +
                  |         two +
                  |         three)
            }
        """))

        when:
        instance.sum(1, 2, 3)

        then:
        fileContentEquals gqFile, "sum: one +         two +         three=6\n"
    }

    def "Should write method call expression statement and the evaluated expression"() {
        setup:
        def instance = toInstance(wrapMethodInClass("""
            int nested1(int value) { q(nested2(value)) }
            int nested2(int value) { return value }
        """))

        when:
        instance.nested1(5)

        then:
        fileContentEquals gqFile, "nested1: nested2(value)=5\n"
    }

    def "Should be able to be used in standalone Groovy script"() {
        setup:
        def instance = toInstance(insertPackageAndImport("q(1 + 1)"))

        when:
        instance.main()

        then:
        fileContentEquals gqFile,
                """run: 1 + 1=2
                  |""".stripMargin()
    }

    def "Should be able to gracefully accept void method call expression"() {
        setup:
        def instance = toInstance(insertPackageAndImport("""
            void nothing(args) {}
            q(nothing(5))
        """))

        when:
        instance.main()

        then:
        fileContentEquals gqFile,
                """run: nothing(5)=null
                  |""".stripMargin()
    }

    def "Should be able to be called nested-ly"() {
        when:
        execute 'q(q(q(5) + 5) + 5) + 5'

        then:
        fileContentEquals gqFile,
                """test: 5=5
                  |test: q(5) + 5=10
                  |test: q(q(5) + 5) + 5=15
                  |""".stripMargin()
    }

    def "Should be able to use all operators at the same time"() {
        when:
        execute 'q | q / 1 + q(2)'

        then:
        fileContentEquals gqFile,
                """test: 1=1
                  |test: 2=2
                  |test: q / 1 + q(2)=3
                  |""".stripMargin()
    }

    def "Should not hit unexpected exception when gq is used wrongly"() {
        when:
        execute "1 + q | 1"

        then:
        Throwable exception = thrown(MissingMethodException)
        exception.message.contains('java.lang.Integer.plus()')
    }
}
