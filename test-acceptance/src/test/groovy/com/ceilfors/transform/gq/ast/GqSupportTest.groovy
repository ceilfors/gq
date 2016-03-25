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

import spock.lang.Unroll

import static com.ceilfors.groovy.spock.FileComparisonHelper.fileContentEquals

/**
 * @author ceilfors
 */
class GqSupportTest extends BaseSpecification {

    def "Should write variable expression statement and the evaluated expression"() {
        setup:
        def instance = toInstance(wrapMethodInClass("""
            int "3 plus 5"() {
                gq(3 + 5)
            }
        """))

        when:
        def result = instance."3 plus 5"()

        then:
        result == 8
        fileContentEquals gqFile, "3 plus 5: 3 + 5=8\n"
    }

    def "Should convert multi line variable expression to one line"() {
        setup:
        def instance = toInstance(wrapMethodInClass(
                """int test() {
                  |    return gq(1 +
                  |         2 +
                  |         3)
            }
        """))

        when:
        def result = instance.test()

        then:
        result == 6
        fileContentEquals gqFile, "test: 1 +         2 +         3=6\n"
    }

    def "Should convert multi line method call expression to one line"() {
        setup:
        def instance = toInstance(wrapMethodInClass(
                """int sum(one, two, three) {
                  |    return gq(one +
                  |         two +
                  |         three)
            }
        """))

        when:
        def result = instance.sum(1, 2, 3)

        then:
        result == 6
        fileContentEquals gqFile, "sum: one +         two +         three=6\n"
    }

    def "Should write method call expression statement and the evaluated expression"() {
        setup:
        def instance = toInstance(wrapMethodInClass("""
            int nested1(int value) { gq(nested2(value)) }
            int nested2(int value) { return value }
        """))

        when:
        def result = instance.nested1(5)

        then:
        result == 5
        fileContentEquals gqFile, "nested1: nested2(value)=5\n"
    }

    def "Should be able to be used in standalone Groovy script"() {
        setup:
        def instance = toInstance(insertPackageAndImport("""
            gq(1 + 1)
        """))

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
            gq(nothing(5))
        """))

        when:
        instance.main()

        then:
        fileContentEquals gqFile,
                """run: nothing(5)=null
                  |""".stripMargin()
    }

    def "Should be able to be called nested-ly"() {
        setup:
        def instance = toInstance(insertPackageAndImport("""
            gq(gq(gq(5) + 5) + 5) + 5
        """))

        when:
        instance.main()

        then:
        fileContentEquals gqFile,
                """run: 5=5
                  |run: gq(5) + 5=10
                  |run: gq(gq(5) + 5) + 5=15
                  |""".stripMargin()
    }

    @Unroll
    def "Should be able to use OR operator: #input"() {
        when:
        execute input

        then:
        fileContentEquals gqFile, "test: $result\n"

        where:
        input                 || result
        "gq | 3 + 5 "         || "3 + 5=8"
        "gq|2+2"              || "2 + 2=4"
        "3 + (gq | 5)"        || "5=5"
        "true && gq | 'test'" || "'test'='test'"
    }

    @Unroll
    def "Should be able to use DIV operator: #input"() {
        when:
        execute input

        then:
        fileContentEquals gqFile, "test: $result\n"

        where:
        input                   || result
        "gq / 3 + 5 "           || "3=3"
        "1 + gq / 1"            || "1=1"
        "'test' && gq / 'test'" || "'test'='test'"
    }

    def "Should be able to use all operators at the same time"() {
        when:
        execute 'gq | gq / 1 + gq(2)'

        then:
        fileContentEquals gqFile,
                """test: 1=1
                  |test: 2=2
                  |test: gq / 1 + gq(2)=3
                  |""".stripMargin()
    }

    def "Should not hit unexpected exception when gq is used wrongly"() {
        when:
        execute("1 + gq | 1")

        then:
        Throwable exception = thrown(MissingMethodException)
        exception.message.contains('java.lang.Integer.plus()')
    }
}
