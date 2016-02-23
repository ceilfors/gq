package com.ceilfors.transform.gq.ast

import com.ceilfors.transform.gq.GqUtils

class GqTest extends BaseSpecification {

    def "Should write the name of a method with empty parameter"() {
        setup:
        def example = newExample(GqExample)

        when:
        def result = example."return 5"()

        then:
        result == 5
        GqUtils.gqFile.readLines().first().contains("return 5()")
    }

    def "Should write the arguments of a method call"() {
        setup:
        def example = newExample(GqExample)

        when:
        def result = example.add(3, 3)

        then:
        result == 6
        GqUtils.gqFile.readLines().first().contains("add(3, 3)")
    }

    def "Should be able to write a method when its return type is void"() {
        setup:
        def example = newExample(GqExample)

        when:
        example."return void"()

        then:
        GqUtils.gqFile.text == ("return void()\n")
    }


    def "Should write the returned value of a method call"() {
        setup:
        def example = newExample(GqExample)

        when:
        def result = example."return 5"()

        then:
        result == 5
        GqUtils.gqFile.readLines().last().contains("-> 5")
    }


    // --- Technical debt
    // Groovy doc recommends CompileStatic for GqTransformation to make compilation quicker
    // Rename GqTransformation to GqASTTransformation to follow standard
    // Remove ast package as it's a useless layer.
    // Use MethodClosure syntax to have better IDE support `GqUtils.&printToFile as MethodClosure`
    // Merge GqSupport and @Gq so that it looks like Gq.gq and @Gq.T
    // gString verbatimText is not implemented. This affects decompilation but not runtime. e.g. ${methodNode.name}(parameters) instead of ${method.name)($1, $2, $3)

    // --- Feature
    // @Gq indentation for nested annotated method call
    // @Gq(vars=true) to print all variable expression
    // q.d
}
