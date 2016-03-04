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

class GqTest extends BaseSpecification {

    def "Should write the name of a method with empty parameter"() {
        setup:
        def example = newExample(GqExample)

        when:
        def result = example."return 5"()

        then:
        result == 5
        gqFile.readLines().first().contains("return 5()")
    }

    def "Should write the arguments of a method call"() {
        setup:
        def example = newExample(GqExample)

        when:
        def result = example.add(3, 3)

        then:
        result == 6
        gqFile.readLines().first().contains("add(3, 3)")
    }

    def "Should be able to write a method when its return type is void"() {
        setup:
        def example = newExample(GqExample)

        when:
        example."return void"()

        then:
        gqFile.text == ("return void()\n".denormalize())
    }


    def "Should write the returned value of a method call"() {
        setup:
        def example = newExample(GqExample)

        when:
        def result = example."return 5"()

        then:
        result == 5
        gqFile.readLines().last().contains("-> 5")
    }

    def "Should write nested method call with indentation"() {
        setup:
        def example = newExample(GqExample)

        when:
        def result = example.nested1()

        then:
        result == 15
        gqFile.text ==
                """nested1()
                  |  nested3()
                  |  -> 5
                  |-> 15
                  |""".stripMargin().denormalize()
    }

    def "Should write exception details if an exception is thrown"() {
        setup:
        def example = newExample(GqExample)

        when:
        example.throwException()

        then:
        RuntimeException e = thrown(RuntimeException)
        e.message == "Hello!"
        gqFile.text ==
                """throwException()
                  |!> RuntimeException('Hello!') at GqExample.groovy:26
                  |""".stripMargin().denormalize()
    }

    def "Should write exception details if an exception is thrown from a nested method"() {
        setup:
        def example = newExample(GqExample)

        when:
        example.nestedThrowException1()

        then:
        RuntimeException e = thrown(RuntimeException)
        e.message == "Hello!"
        gqFile.text ==
                """nestedThrowException1()
                  |  nestedThrowException2()
                  |    nestedThrowException3()
                  |    !> RuntimeException('Hello!') at GqExample.groovy:43
                  |  !> RuntimeException('Hello!') at GqExample.groovy:37
                  |!> RuntimeException('Hello!') at GqExample.groovy:31
                  |""".stripMargin().denormalize()
    }

    def "Should restore indentation when an exception is thrown"() {
        setup:
        def example = newExample(GqExample)

        when:
        example.throwException()

        then:
        thrown(RuntimeException)

        when:
        example.nested1()

        then:
        gqFile.text.endsWith(
                """nested1()
                  |  nested3()
                  |  -> 5
                  |-> 15
                  |""".stripMargin().denormalize())
    }

    // --- Technical debt
    // Rename GqTransformation to GqASTTransformation to follow standard
    // Remove ast package as it's a useless layer.
    // GqFile that's created by SingletonCodeFlowManager is never closed
    // This code in GqFile seems to be a responsibility of someone else: writer.indentLevel = methodCalls.size()

    // --- Feature
    // Adopt @zefifier groovy-decorator
    // When gq file is deleted, gq won't create the file again. This is because of the convert to Writer.
    // @Gq Exception - Print source code context e.g. source code snippets and line numbers
    // @Gq Exception - Test - nestedException1 catch exception from nestedexception2 and throw again. Indentation must stay the same.
    // GqSupport - support multiple arguments e.g. gc(3+5, 10+10, 15+15)
    // GqSupport - support multiline text e.g. gc(3+\n\n5) -> Trim the new line. See spock's SourceLookup?
    // GqSupport.gq to support void return type
    // Support @Gq and GqSupport for groovy scripts e.g. not encapsulated in class
    // @Gq(vars=true) to print all variable expression
    // q.d
}
