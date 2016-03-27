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

import static com.ceilfors.groovy.spock.FileComparisonHelper.fileContentEquals

class GqTraceSpec extends BaseSpecification {

    def "Should write the name of the method"() {
        setup:
        def instance = toInstance(wrapMethodInClass("""
            @q
            int myMethod() {
                5
            }
        """))

        when:
        instance.myMethod()

        then:
        gqFile.readLines().first().contains("myMethod()")
    }

    def "Should write the returned value of a method call"() {
        setup:
        def instance = toInstance(wrapMethodInClass("""
            @q
            int "return 5"() {
                5
            }
        """))

        when:
        instance."return 5"()

        then:
        gqFile.readLines().last().contains("-> 5")
    }

    def "Should return the value of the wrapped method"() {
        setup:
        def instance = toInstance(wrapMethodInClass("""
            @q
            int myMethod() {
                5 + 10
            }
        """))

        when:
        def result = instance.myMethod()

        then:
        result == 15
    }

    def "Should write the arguments of a method call"() {
        setup:
        def instance = toInstance(wrapMethodInClass("""
            @q
            int add(int x, int y) {
                return x + y
            }
        """))

        when:
        instance.add(3, 3)

        then:
        gqFile.readLines().first().contains("add(3, 3)")
    }

    def "Should be able to write a method when its return type is void"() {
        setup:
        def instance = toInstance(wrapMethodInClass("""
            @q
            void "return void"() {}
        """))

        when:
        instance."return void"()

        then:
        fileContentEquals gqFile, "return void()\n"
    }

    def "Should write nested method call with indentation"() {
        setup:
        def instance = toInstance(wrapMethodInClass("""
            @q int nested()          { nested2() + 5 }
               private int nested2() { nested3() + 5 }
            @q private int nested3() { 5 }
        """))

        when:
        def result = instance.nested()

        then:
        result == 15
        fileContentEquals gqFile,
                """nested()
                  |  nested3()
                  |  -> 5
                  |-> 15
                  |""".stripMargin()
    }

    def "Should write exception details if an exception is thrown"() {
        setup:
        def example = newExample(GqExample)

        when:
        example.throwException()

        then:
        RuntimeException e = thrown(RuntimeException)
        e.message == "Hello!"
        fileContentEquals gqFile,
                """throwException()
                  |!> RuntimeException('Hello!') at GqExample.groovy:26
                  |""".stripMargin()
    }

    def "Should write exception details if an exception is thrown from a nested method"() {
        setup:
        def example = newExample(GqExample)

        when:
        example.nestedThrowException1()

        then:
        RuntimeException e = thrown(RuntimeException)
        e.message == "Hello!"
        fileContentEquals gqFile,
                """nestedThrowException1()
                  |  nestedThrowException2()
                  |    nestedThrowException3()
                  |    !> RuntimeException('Hello!') at GqExample.groovy:43
                  |  !> RuntimeException('Hello!') at GqExample.groovy:37
                  |!> RuntimeException('Hello!') at GqExample.groovy:31
                  |""".stripMargin()
    }

    def "Should restore indentation when an exception is thrown"() {
        setup:
        def example = newExample(GqExample)

        when:
        example.throwException()

        then:
        thrown(RuntimeException)
        gqFile.delete()

        when:
        example.throwException()

        then:
        thrown(RuntimeException)
        fileContentEquals gqFile,
                """throwException()
                  |!> RuntimeException('Hello!') at GqExample.groovy:26
                  |""".stripMargin()
    }

    def "Should be able to be used in standalone Groovy script"() {
        setup:
        def instance = toInstance(insertPackageAndImport("""
            @q
            def simplyReturn(arg) { arg }

            simplyReturn(5)
        """))

        when:
        instance.main()

        then:
        fileContentEquals gqFile,
                """simplyReturn(5)
                  |-> 5
                  |""".stripMargin()
    }

    def "Should shorten long expression and save the original expression to a temporary file"() {
        setup:
        def instance = toInstance(wrapMethodInClass("""
            @q
            String simplyReturn(arg) {
                return arg
            }
        """))
        String args = "0" * 100

        when:
        instance.simplyReturn(args)

        then: "Method arguments must be handled"
        def lines = gqFile.text.readLines()
        def methodLine = lines[0] =~ "simplyReturn\\('(0+\\.\\.0+)' \\(file://(.*)\\)\\)"
        methodLine.forceMatches()
        methodLine.group(1).length() < 100
        new File(methodLine.group(2)).text == "'$args'".toString()

        then: "Return expression value must be handled"
        def returnLine = lines[1] =~ "-> '(0+\\.\\.0+)' \\(file://(.*)\\)"
        returnLine.forceMatches()
        returnLine.group(1).length() < 100
        new File(returnLine.group(2)).text == "'$args'".toString()
    }
}
