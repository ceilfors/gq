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

import spock.lang.Specification
import spock.lang.Unroll

/**
 * @author ceilfors
 */
class AutoIndentingPrintWriterTest extends Specification {

    StringWriter writer
    AutoIndentingPrintWriter printer

    def setup() {
        writer = new StringWriter()
        printer = new AutoIndentingPrintWriter(new PrintWriter(writer))
    }

    @Unroll
    def "Should indent content correctly when the indentation is set"() {

        when:
        printer.indent = indent
        printer.indentLevel = depth
        printer.print("r")

        then:
        writer.toString() == result

        where:
        indent | depth | result
        ""     | 0     | "r"
        ""     | 999   | "r"
        " "    | 1     | " r"
        "   "  | 3     | "         r"
    }

    def "Indentation is only applied when the next character is in new line"() {
        when:
        printer.indentLevel = 2
        printer.print("foo")
        printer.println("newline")
        printer.print("bar")
        printer.print("hello")
        printer.println("world")
        printer.print("gq")


        then:
        writer.toString() ==
                """    foonewline
                  |    barhelloworld
                  |    gq""".stripMargin().denormalize()
    }

    def "Should indent new lines that are coming from the printed String"() {
        when:
        printer.indentLevel = 1
        printer.print('start')
        printer.println("foo\nbar\nboo")
        printer.print('end')

        then:
        writer.toString() ==
                """  startfoo
                  |  bar
                  |  boo
                  |  end""".stripMargin().denormalize()
    }
}
