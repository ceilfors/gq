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

import static com.ceilfors.groovy.spock.FileComparisonHelper.fileContentEquals

/**
 * @author ceilfors
 */
class GqFileTest extends Specification {

    @Rule
    TemporaryFolder temporaryFolder

    def "Should recreate file when deleted"() {
        setup:
        def file = new File(temporaryFolder.newFolder().absolutePath, "gq")
        file.createNewFile()
        def gqFile = new GqFile(file)

        when:
        gqFile.println("one")

        then:
        fileContentEquals file, "one\n"

        when:
        file.delete()
        gqFile.println("two")

        then:
        fileContentEquals file, "two\n"
    }
}
