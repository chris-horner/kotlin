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

class UsageFinder(
        private val context: Context,
        private val analysisResult: AnalysisResult,
        private val logConsumer: (String) -> Unit
) : RecursiveJsVisitor() {
    private var currentNodeWithLocation: JsNode? = null
    private val localVars = mutableSetOf<JsName>()
    private var depth = 0

    override fun visit(x: JsVars.JsVar) {
        if (analysisResult.nodeMap[x] == null && x !in analysisResult.astNodesToSkip) {
            super.visit(x)
        }
    }

    override fun visitExpressionStatement(x: JsExpressionStatement) {
        if (analysisResult.nodeMap[x] == null && x !in analysisResult.astNodesToSkip) {
            super.visitExpressionStatement(x)
        }
    }

    override fun visitReturn(x: JsReturn) {
        if (analysisResult.nodeMap[x] == null && x !in analysisResult.astNodesToSkip) {
            super.visitReturn(x)
        }
    }

    override fun visitNameRef(nameRef: JsNameRef) {
        val node = context.extractNode(nameRef)
        if (node != null) {
            if (!node.original.used && node.root().localName?.let { it !in localVars } ?: true) {
                reportAndNest("use: referenced name $node", currentNodeWithLocation) {
                    use(node)
                }
            }
        }
        else {
            super.visitNameRef(nameRef)
        }
    }

    override fun visitInvocation(invocation: JsInvocation) {
        val function = invocation.qualifier
        when {
            function is JsFunction && function in analysisResult.functionsToEnter -> {
                accept(function.body)
                for (argument in invocation.arguments.filter { it is JsFunction && it in analysisResult.functionsToEnter }) {
                    accept(argument)
                }
            }
            invocation in analysisResult.invocationsToSkip -> {}
            else -> {
                super.visitInvocation(invocation)
            }
        }
    }

    fun use(node: Node) {
        if (node.original.used) return
        node.original.used = true

        useDeclaration(node)

        for (dependency in node.original.dependencies) {
            if (!dependency.original.used) {
                reportAndNest("use: dependency $dependency", null) { use(dependency) }
            }
        }
        node.members.toList().forEach { (name, member) ->
            if (!member.original.used) {
                reportAndNest("use: member $name", null) { use(member) }
            }
        }

        for (expr in node.original.expressions.filterIsInstance<JsFunction>()) {
            reportAndNest("traverse: function", expr) {
                expr.collectLocalVariables().let {
                    context.addLocalVars(it)
                    localVars += it
                }
                expr.body.accept(this)
            }
        }
    }

    private fun useDeclaration(node: Node) {
        if (node.original.hasSideEffects && !node.original.used) {
            reportAndNest("use: because of side effect", null) {
                use(node)
            }
        }
        else if (!node.original.declarationUsed) {
            node.original.declarationUsed = true

            node.original.qualifier?.parent?.let {
                reportAndNest("use-decl: parent $it", null) {
                    useDeclaration(it)
                }
            }

            for (expr in node.original.expressions) {
                reportAndNest("traverse: value", expr) {
                    expr.accept(this)
                }
            }
        }
    }

    override fun visitPrefixOperation(x: JsPrefixOperation) {
        if (x.operator == JsUnaryOperator.TYPEOF) {
            val arg = x.arg
            if (arg is JsNameRef && arg.qualifier == null) {
                context.extractNode(arg)?.let { useDeclaration(it) }
                return
            }
        }
        super.visitPrefixOperation(x)
    }

    override fun visitElement(node: JsNode) {
        val newLocation = node.extractLocation()
        val old = currentNodeWithLocation
        if (newLocation != null) {
            currentNodeWithLocation = node
        }
        super.visitElement(node)
        currentNodeWithLocation = old
    }

    private fun report(message: String) {
        logConsumer("  ".repeat(depth) + message)
    }

    private fun reportAndNest(message: String, dueTo: JsNode?, action: () -> Unit) {
        val location = dueTo?.extractLocation()
        val fullMessage = if (location != null) "$message (due to ${location.fileName}:${location.lineNumber + 1})" else message
        report(fullMessage)
        nested(action)
    }

    private fun nested(action: () -> Unit) {
        depth++
        action()
        depth--
    }
}