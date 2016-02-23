package com.ceilfors.transform.gq.ast

import com.ceilfors.transform.gq.GqUtils
import org.codehaus.groovy.ast.*
import org.codehaus.groovy.ast.builder.AstBuilder
import org.codehaus.groovy.ast.expr.ClosureExpression
import org.codehaus.groovy.ast.expr.VariableExpression
import org.codehaus.groovy.ast.stmt.BlockStatement
import org.codehaus.groovy.ast.stmt.Statement
import org.codehaus.groovy.control.CompilePhase
import org.codehaus.groovy.control.SourceUnit
import org.codehaus.groovy.transform.AbstractASTTransformation
import org.codehaus.groovy.transform.GroovyASTTransformation

import static org.codehaus.groovy.ast.tools.GeneralUtils.returnS
import static org.codehaus.groovy.ast.tools.GeneralUtils.varX

@GroovyASTTransformation(phase = CompilePhase.SEMANTIC_ANALYSIS)
public class GqTransformation extends AbstractASTTransformation {

    public void visit(ASTNode[] astNodes, SourceUnit sourceUnit) {
        init(astNodes, sourceUnit)

        AnnotatedNode annotatedNode = astNodes[1] as AnnotatedNode
        if (annotatedNode instanceof MethodNode) {
            MethodNode methodNode = annotatedNode as MethodNode

            def result = varX("result")
            def wrappedOriginalCode = wrapInClosure(methodNode)

            BlockStatement newCode = new BlockStatement([
                    printMethodHeaderS(methodNode),
                    callClosureAndKeepResultS(wrappedOriginalCode, result),
                    printVariableS(result),
                    returnS(result)
            ], new VariableScope())

            methodNode.code = newCode
        } else {
            throw new IllegalStateException("Gq annotation is only usable in methods.")
        }
    }

    private ClosureExpression wrapInClosure(MethodNode methodNode) {
        BlockStatement originalCode = methodNode.code as BlockStatement

        final def closureVariableScope = new VariableScope(originalCode.variableScope)
        for (Parameter parameter in methodNode.parameters) {
            // Allow closure to access the original code's variable
            closureVariableScope.putReferencedLocalVariable(parameter)
            parameter.setClosureSharedVariable(true) // KLUDGE: Should we use: new VariableScopeVisitor(sourceUnit, true).visitMethod(methodNode)
        }

        def closure = new ClosureExpression(Parameter.EMPTY_ARRAY, originalCode)
        closure.variableScope = closureVariableScope

        return closure
    }

    private Statement printMethodHeaderS(MethodNode methodNode) {
        new AstBuilder().buildFromSpec {
            block {
                expression {
                    staticMethodCall(GqUtils, "printToFile") {
                        argumentList {
                            gString "${methodNode.name}(parameters)", {
                                strings {
                                    constant "${methodNode.name}(" as String
                                    for (int i = 0; i < methodNode.parameters.size(); i++) {
                                        if (i != 0) {
                                            constant ', '
                                        }
                                    }
                                    constant ')'
                                }
                                values {
                                    for (Parameter parameter in methodNode.parameters) {
                                        variable parameter.name
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }[0] as Statement
    }

    private Statement callClosureAndKeepResultS(ClosureExpression closure, VariableExpression resultVariable) {
        new AstBuilder().buildFromSpec {
            expression {
                declaration {
                    expression.add(resultVariable)
                    token "="
                    methodCall {
                        expression.add(closure)
                        constant "call"
                        argumentList {}
                    }

                }
            }
        }[0] as Statement
    }

    private Statement printVariableS(VariableExpression variableExpression) {
        new AstBuilder().buildFromSpec {
            expression {
                staticMethodCall(GqUtils, "printToFile") {
                    argumentList {
                        binary {
                            constant "-> "
                            token "+"
                            expression.add(variableExpression)
                        }

                    }
                }
            }
        }[0] as Statement
    }
}