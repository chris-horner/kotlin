/*
 * Copyright 2010-2017 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jetbrains.kotlin.js.dce

import org.jetbrains.kotlin.js.backend.ast.*
import org.jetbrains.kotlin.js.dce.Context.Node
import org.jetbrains.kotlin.js.inline.util.collectLocalVariables
import org.jetbrains.kotlin.js.translate.context.Namer

class Analyzer(private val context: Context) : JsVisitor() {
    private val processedFunctions = mutableSetOf<JsFunction>()
    private val postponedFunctions = mutableMapOf<JsName, JsFunction>()
    private val nodeMap = mutableMapOf<JsNode, Node>()
    private val astNodesToSkip = mutableSetOf<JsNode>()
    private val invocationsToSkip = mutableSetOf<JsInvocation>()
    val moduleMapping = mutableMapOf<JsStatement, String>()
    private val functionsToEnter = mutableSetOf<JsFunction>()
    private var returnNodes = mutableListOf<Node>()

    val analysisResult = object : AnalysisResult {
        override val nodeMap: Map<JsNode, Node> get() = this@Analyzer.nodeMap

        override val astNodesToSkip: Set<JsNode> get() = this@Analyzer.astNodesToSkip

        override val functionsToEnter: Set<JsFunction> get() = this@Analyzer.functionsToEnter

        override val invocationsToSkip: Set<JsInvocation> get() = this@Analyzer.invocationsToSkip
    }

    override fun visitVars(x: JsVars) {
        x.vars.forEach { accept(it) }
    }

    override fun visit(x: JsVars.JsVar) {
        val rhs = x.initExpression
        if (rhs != null) {
            processAssignment(x, x.name.makeRef(), rhs)?.let { nodeMap[x] = it }
        }
    }

    override fun visitExpressionStatement(x: JsExpressionStatement) {
        val expression = x.expression
        if (expression is JsBinaryOperation) {
            if (expression.operator == JsBinaryOperator.ASG) {
                processAssignment(x, expression.arg1, expression.arg2)?.let {
                    nodeMap[x] = it
                }
            }
        }
        else if (expression is JsFunction) {
            expression.name?.let { context.nodes[it]?.original }?.let {
                nodeMap[x] = it
                it.expressions += expression
            }
        }
        else if (expression is JsInvocation) {
            val function = expression.qualifier
            if (function is JsFunction) {
                enterFunction(function, expression.arguments)
                return
            }
            if (function is JsNameRef && function.qualifier == null) {
                val postponedFunction = function.name?.let { postponedFunctions[it] }
                if (postponedFunction != null) {
                    enterFunction(postponedFunction, expression.arguments)
                    invocationsToSkip += expression
                    return
                }
            }
            if (context.isObjectDefineProperty(function)) {
                handleObjectDefineProperty(x, expression.arguments.getOrNull(0), expression.arguments.getOrNull(1),
                                           expression.arguments.getOrNull(2))
            }
            else if (context.isDefineModule(function)) {
                astNodesToSkip += x
            }
            else if (context.isAmdDefine(function)) {
                handleAmdDefine(expression, expression.arguments)
            }
        }
    }

    private fun handleObjectDefineProperty(statement: JsStatement, target: JsExpression?, propertyName: JsExpression?,
                                           propertyDescriptor: JsExpression?) {
        if (target == null || propertyName !is JsStringLiteral || propertyDescriptor == null) return
        val targetNode = context.extractNode(target) ?: return

        val memberNode = targetNode.member(propertyName.value)
        nodeMap[statement] = memberNode
        memberNode.original.hasSideEffects = true

        if (propertyDescriptor is JsObjectLiteral) {
            for (initializer in propertyDescriptor.propertyInitializers) {
                processAssignment(statement, JsNameRef(propertyName.value, target), initializer.valueExpr)
            }
        }
        else if (propertyDescriptor is JsInvocation) {
            val function = propertyDescriptor.qualifier
            if (context.isObjectGetOwnPropertyDescriptor(function)) {
                val source = propertyDescriptor.arguments.getOrNull(0)
                val sourcePropertyName = propertyDescriptor.arguments.getOrNull(1)
                if (source != null && sourcePropertyName is JsStringLiteral) {
                    processAssignment(statement, JsNameRef(propertyName.value, target), JsNameRef(sourcePropertyName.value, source))
                }
            }
        }
    }

    private fun handleAmdDefine(invocation: JsInvocation, arguments: List<JsExpression>) {
        val argumentsWithoutName = when (arguments.size) {
            2 -> arguments
            3 -> arguments.drop(1)
            else -> return
        }

        val dependencies = argumentsWithoutName[0] as? JsArrayLiteral ?: return
        val functionRef = argumentsWithoutName[1]
        val function = when (functionRef) {
            is JsFunction -> functionRef
            is JsNameRef -> {
                if (functionRef.qualifier != null) return
                postponedFunctions[functionRef.name] ?: return
            }
            else -> return
        }

        val dependencyNodes = dependencies.expressions
                .map { it as? JsStringLiteral ?: return }
                .map { if (it.value == "exports") context.currentModule else context.globalScope.member(it.value) }

        enterFunctionWithGivenNodes(function, dependencyNodes)
        invocationsToSkip += invocation
    }

    override fun visitBlock(x: JsBlock) {
        val oldModule = context.currentModule
        val newModule = moduleMapping[x]
        if (newModule != null) {
            context.currentModule = context.globalScope.member(newModule)
        }
        x.statements.forEach { accept(it) }
        context.currentModule = oldModule
    }

    override fun visitIf(x: JsIf) {
        accept(x.thenStatement)
        x.elseStatement?.accept(this)
    }

    override fun visitReturn(x: JsReturn) {
        val expr = x.expression
        if (expr != null) {
            context.extractNode(expr)?.let {
                returnNodes.add(it)
                nodeMap[x] = it
            }
        }
    }

    private fun processAssignment(node: JsNode?, lhs: JsExpression, rhs: JsExpression): Node? {
        val leftNode = context.extractNode(lhs)
        val rightNode = context.extractNode(rhs)

        if (leftNode != null && rightNode != null) {
            leftNode.alias(rightNode)
            return leftNode
        }
        else if (leftNode != null) {
            if (rhs is JsInvocation) {
                val function = rhs.qualifier
                if (function is JsFunction) {
                    enterFunction(function, rhs.arguments)
                    return null
                }
                if (function is JsNameRef && function.qualifier == null) {
                    function.name?.let { postponedFunctions[it] }?.let {
                        enterFunction(it, rhs.arguments)
                        return null
                    }
                }
                if (context.isObjectFunction(function, "create")) {
                    handleObjectCreate(leftNode, rhs.arguments.getOrNull(0))
                    return leftNode
                }
                if (context.isDefineInlineFunction(function) && rhs.arguments.size == 2) {
                    leftNode.expressions += rhs.arguments[1]
                    val defineInlineFunctionNode = context.extractNode(function)
                    if (defineInlineFunctionNode != null) {
                        leftNode.dependencies += defineInlineFunctionNode
                    }
                    return leftNode
                }
            }
            else if (rhs is JsBinaryOperation) {
                if (rhs.operator == JsBinaryOperator.OR) {
                    val secondNode = context.extractNode(rhs.arg1)
                    val reassignment = rhs.arg2
                    if (reassignment is JsBinaryOperation && reassignment.operator == JsBinaryOperator.ASG) {
                        val reassignNode = context.extractNode(reassignment.arg1)
                        val reassignValue = reassignment.arg2
                        if (reassignNode == secondNode && reassignNode != null && reassignValue is JsObjectLiteral &&
                            reassignValue.propertyInitializers.isEmpty()
                        ) {
                            return processAssignment(node, lhs, rhs.arg1)
                        }
                    }
                }
                else if (rhs.operator == JsBinaryOperator.COMMA) {
                    if (rhs.arg1 is JsStringLiteral) {
                        return processAssignment(node, lhs, rhs.arg2)
                    }
                }
            }
            else if (rhs is JsFunction) {
                leftNode.expressions += rhs
                return leftNode
            }
            else if (leftNode.qualifier?.memberName == Namer.METADATA) {
                leftNode.expressions += rhs
                return leftNode
            }
            else if (rhs is JsObjectLiteral && rhs.propertyInitializers.isEmpty()) {
                return leftNode
            }

            val nodeInitializedByEmptyObject = extractVariableInitializedByEmptyObject(rhs)?.let { context.nodes[it] }
            if (nodeInitializedByEmptyObject != null) {
                leftNode.alias(nodeInitializedByEmptyObject)
                return leftNode
            }
        }
        return null
    }

    private fun handleObjectCreate(target: Node, arg: JsExpression?) {
        if (arg == null) return

        val prototypeNode = context.extractNode(arg) ?: return
        target.original.dependencies += prototypeNode.original
        target.original.expressions += arg
    }

    private fun enterFunction(function: JsFunction, arguments: List<JsExpression>) {
        functionsToEnter += function
        context.addLocalVars(function.collectLocalVariables())

        for ((param, arg) in function.parameters.zip(arguments)) {
            if (arg is JsFunction && arg.name == null && isProperFunctionalParameter(function.body, param)) {
                postponedFunctions[param.name] = arg
            }
            else {
                processAssignment(function, param.name.makeRef(), arg)
            }
        }

        processFunction(function)
    }

    private fun extractVariableInitializedByEmptyObject(expression: JsExpression): JsName? {
        if (expression !is JsConditional) return null

        val testExpr = expression.testExpression as? JsBinaryOperation ?: return null
        if (testExpr.operator != JsBinaryOperator.REF_EQ) return null

        val testExprLhs = testExpr.arg1 as? JsPrefixOperation ?: return null
        if (testExprLhs.operator != JsUnaryOperator.TYPEOF) return null
        val testExprNameRef = testExprLhs.arg as? JsNameRef ?: return null

        val testExprRhs = testExpr.arg2 as? JsStringLiteral ?: return null
        if (testExprRhs.value != "undefined") return null

        val thenExpr = expression.thenExpression as? JsObjectLiteral ?: return null
        if (thenExpr.propertyInitializers.isNotEmpty()) return null

        val elseExpr = expression.elseExpression as? JsNameRef ?: return null

        if (testExprNameRef.name != elseExpr.name) return null
        return testExprNameRef.name
    }

    private fun enterFunctionWithGivenNodes(function: JsFunction, arguments: List<Node>) {
        functionsToEnter += function
        context.addLocalVars(function.collectLocalVariables())

        for ((param, arg) in function.parameters.zip(arguments)) {
            if (arg.root() == context.globalScope.original) continue
            val paramNode = context.nodes[param.name]!!
            paramNode.alias(arg)
        }

        processFunction(function)
    }

    private fun processFunction(function: JsFunction) {
        if (processedFunctions.add(function)) {
            val oldReturnNodes = returnNodes
            val newReturnNodes = mutableListOf<Node>()
            returnNodes = newReturnNodes

            accept(function.body)

            returnNodes = oldReturnNodes
        }
    }

    private fun isProperFunctionalParameter(body: JsStatement, parameter: JsParameter): Boolean {
        var result = true
        body.accept(object : RecursiveJsVisitor() {
            override fun visitInvocation(invocation: JsInvocation) {
                val qualifier = invocation.qualifier
                if (qualifier is JsNameRef && qualifier.qualifier == null && qualifier.name == parameter.name) {
                    if (invocation.arguments.all { context.extractNode(it) != null }) {
                        return
                    }
                }
                if (context.isAmdDefine(qualifier)) return
                super.visitInvocation(invocation)
            }

            override fun visitNameRef(nameRef: JsNameRef) {
                if (nameRef.name == parameter.name) {
                    result = false
                }
                super.visitNameRef(nameRef)
            }
        })
        return result
    }
}