package com.ceilfors.transform.gq.ast

import com.ceilfors.transform.gq.GqUtils
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

/**
 * @author ceilfors
 */
class BaseSpecification extends Specification {

    @Rule
    TemporaryFolder temporaryFolder

    def setup() {
        System.setProperty(GqUtils.TEMP_DIR, temporaryFolder.newFolder().absolutePath)
    }

    def cleanup() {
        System.clearProperty(GqUtils.TEMP_DIR)
    }

    static <T> T newExample(Class<T> clasz) {
        def file = new File("src/test/groovy/${clasz.package.name.replace('.', '/')}/${clasz.simpleName}.groovy")
        assert file.exists()

        GroovyClassLoader invoker = new GroovyClassLoader()
        def clazz = invoker.parseClass(file)
        return clazz.newInstance() as T
    }
}
