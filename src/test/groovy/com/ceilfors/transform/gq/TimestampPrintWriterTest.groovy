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

import groovy.transform.NotYetImplemented
import spock.lang.Specification
/**
 * @author ceilfors
 */
class TimestampPrintWriterTest extends Specification {

    StringWriter writer
    TimestampPrintWriter printer

    def setup() {
        writer = new StringWriter()
        printer = new TimestampPrintWriter(writer)
    }

    @NotYetImplemented
    def "Should add a timestamp to every newline"() {
        when:
        printer.println("one")

        then:
        writer.toString() == " 0.0s one\n".denormalize()

        when:
        Thread.sleep(200)
        printer.println("two")

        then:
        writer.toString() == " 0.0s one\n 0.2s two\n".denormalize()
    }

    def "Should not add a timestamp if there is no newline"() {
        when:
        printer.print('foo')

        then:
        writer.toString() == ' 0.0s foo'

        when:
        printer.print('bar')

        then:
        writer.toString() == ' 0.0s foobar'
    }
}
