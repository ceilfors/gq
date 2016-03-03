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

import com.ceilfors.transform.gq.GqFile
import com.ceilfors.transform.gq.SingletonCodeFlowManager
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification
/**
 * @author ceilfors
 */
class BaseSpecification extends Specification {

    @Rule
    TemporaryFolder temporaryFolder

    File gqFile
    private Writer writer

    def setup() {
        gqFile = new File(temporaryFolder.newFolder().absolutePath, "gq")
        writer = new FileWriter(gqFile)
        SingletonCodeFlowManager.INSTANCE.codeFlowListeners = [new GqFile(writer)]
    }

    def cleanup() {
        writer.close()
    }

    static <T> T newExample(Class<T> clasz) {
        def file = new File("src/test/groovy/${clasz.package.name.replace('.', '/')}/${clasz.simpleName}.groovy")
        assert file.exists()

        GroovyClassLoader invoker = new GroovyClassLoader()
        def clazz = invoker.parseClass(file)
        return clazz.newInstance() as T
    }
}
