package com.ceilfors.transform.gq.ast

import com.ceilfors.transform.gq.GqUtils
import org.codehaus.groovy.ast.ASTNode
import org.codehaus.groovy.ast.AnnotatedNode
import org.codehaus.groovy.ast.MethodNode
import org.codehaus.groovy.ast.Parameter
import org.codehaus.groovy.ast.VariableScope
import org.codehaus.groovy.ast.builder.AstBuilder
import org.codehaus.groovy.ast.expr.ClosureExpression
import org.codehaus.groovy.ast.stmt.BlockStatement
import org.codehaus.groovy.ast.stmt.Statement
import org.codehaus.groovy.control.CompilePhase
import org.codehaus.groovy.control.SourceUnit
import org.codehaus.groovy.transform.AbstractASTTransformation
import org.codehaus.groovy.transform.GroovyASTTransformation

@GroovyASTTransformation(phase = CompilePhase.SEMANTIC_ANALYSIS)
public class GqTransformation extends AbstractASTTransformation {

    public static final String RESULT = "result"

    public void visit(ASTNode[] astNodes, SourceUnit sourceUnit) {
        init(astNodes, sourceUnit)

        AnnotatedNode annotatedNode = astNodes[1] as AnnotatedNode
        if (annotatedNode instanceof MethodNode) {
            transformMethodNode(annotatedNode)
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

    private void transformMethodNode(MethodNode methodNode) {
        def astBuilder = new AstBuilder()

        List<ASTNode> nodes = astBuilder.buildFromSpec {
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
                expression {
                    declaration {
                        variable "result"
                        token "="
                        methodCall {
                            expression.add(wrapInClosure(methodNode))
                            constant "call"
                            argumentList {}
                        }

                    }
                }
                expression {
                    staticMethodCall(GqUtils, "printToFile") {
                        argumentList {
                            binary {
                                constant "-> "
                                token "+"
                                variable RESULT
                            }

                        }
                    }
                }
                returnStatement { variable RESULT }
            }
        }
        methodNode.code = nodes[0] as Statement
    }
}