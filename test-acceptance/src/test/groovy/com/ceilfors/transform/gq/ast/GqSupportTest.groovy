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

/**
 * @author ceilfors
 */
class GqSupportTest extends BaseSpecification {

    def "Should write variable expression statement and the evaluated expression"() {
        setup:
        def example = newExample(GqSupportExample)

        when:
        def result = example."3 plus 5"()

        then:
        result == 8
        gqFile.text == ("3 plus 5: 3 + 5=8\n".denormalize())
    }

    def "Should write method call expression statement and the evaluated expression"() {
        setup:
        def example = newExample(GqSupportExample)

        when:
        def result = example.nested1(5)

        then:
        result == 5
        gqFile.text == ("nested1: nested2(value)=5\n".denormalize())
    }

    def "Should write method call expression statement with multiple arguments in order"() {
        setup:
        def example = newExample(GqSupportExample)

        when:
        def result = example.sum(1, 2, 3)

        then:
        result == 6
        gqFile.text == ("sum: one=1, two=2, three=3\n".denormalize())
    }

    def "Should be able to be used in conjunction with CompileStatic method"() {
        when:
        def result = new GroovyClassLoader().parseClass("""
            import static com.ceilfors.transform.gq.GqSupport.gq

            class Test {

                @groovy.transform.CompileStatic
                public String compileStatic() {
                    return gq("static!")
                }
            }
        """).newInstance().compileStatic()

        then:
        result == "static!"
        gqFile.text.startsWith("compileStatic")

    }

    def "Should be able to be used in conjunction with CompileStatic class"() {
        when:
        def result = new GroovyClassLoader().parseClass("""
            import static com.ceilfors.transform.gq.GqSupport.gq

            @groovy.transform.CompileStatic
            class Test {

                public String compileStatic() {
                    return gq("static!")
                }
            }
        """).newInstance().compileStatic()

        then:
        result == "static!"
        gqFile.text.startsWith("compileStatic")
    }
}
