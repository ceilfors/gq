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

import groovy.transform.NotYetImplemented

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

    @NotYetImplemented
    def "Should write method call expression statement with multiple arguments in order"() {
        setup:
        def instance = toInstance(wrapMethodInClass("""
            int sum(one, two, three) {
                gq(one, two, three)
                return one + two + three
            }
        """))

        when:
        def result = instance.sum(1, 2, 3)

        then:
        result == 6
        fileContentEquals gqFile, "sum: one=1, two=2, three=3\n"
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

    @NotYetImplemented
    def "Should be able to use OR operator"() {
        setup:
        def instance = toInstance(insertPackageAndImport("gq | 'text'"))

        when:
        instance.main()

        then:
        fileContentEquals gqFile, "run: 'text'='text'\n"
    }

    @NotYetImplemented
    def "Should be able to use DIV operator"() {
        setup:
        def instance = toInstance(insertPackageAndImport("gq / 'text'"))

        when:
        instance.main()

        then:
        fileContentEquals gqFile, "run: 'text'='text'\n"
    }
}
