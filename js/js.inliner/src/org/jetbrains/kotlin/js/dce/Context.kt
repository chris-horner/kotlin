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
import org.jetbrains.kotlin.js.translate.utils.jsAstUtils.array
import org.jetbrains.kotlin.js.translate.utils.jsAstUtils.index

class Context {
    val globalScope = Node()
    var currentModule = globalScope
    val nodes = mutableMapOf<JsName, Node>()
    var thisNode: Node? = globalScope

    fun addLocalVars(names: Collection<JsName>) {
        nodes += names.filter { it !in nodes }.associate { it to Node(it) }
    }

    fun extractNode(expression: JsExpression): Node? {
        val node = extractNodeImpl(expression)
        return if (node != null && node.root() == globalScope.original && node.pathFromRoot() == listOf("module", "exports")) {
            currentModule
        }
        else {
            node
        }
    }

    private fun extractNodeImpl(expression: JsExpression): Node? {
        return when (expression) {
            is JsNameRef -> {
                val qualifier = expression.qualifier
                if (qualifier == null) {
                    expression.name?.let { nodes[it]?.original } ?: globalScope.original.member(expression.ident)
                }
                else {
                    extractNode(qualifier)?.member(expression.ident)?.original
                }
            }
            is JsArrayAccess -> {
                val index = expression.index
                if (index is JsStringLiteral) extractNode(expression.array)?.member(index.value)?.original else null
            }
            is JsLiteral.JsThisRef -> {
                thisNode
            }
            is JsInvocation -> {
                val qualifier = expression.qualifier
                if (qualifier is JsNameRef && qualifier.qualifier == null && qualifier.ident == "require" &&
                    qualifier.name !in nodes && expression.arguments.size == 1
                        ) {
                    val argument = expression.arguments[0]
                    if (argument is JsStringLiteral) {
                        return globalScope.member(argument.value)
                    }
                }
                null
            }
            else -> {
                null
            }
        }
    }


    class Node private constructor(val localName: JsName?, qualifier: Qualifier?) {
        val dependencies = mutableSetOf<Node>()
        val expressions = mutableSetOf<JsExpression>()
        var hasSideEffects = false
        var used = false
        var declarationUsed = false
        private val membersImpl = mutableMapOf<String, Node>()
        private var rank = 0

        var qualifier: Qualifier? = qualifier
            get
            private set

        constructor(localName: JsName? = null) : this(localName, null)

        var original: Node = this
            get() {
                if (field != this) {
                    field = field.original
                }
                return field
            }
            private set

        val members: Map<String, Node> get() = original.membersImpl

        fun member(name: String): Node = original.membersImpl.getOrPut(name) { Node(null, Qualifier(this, name)) }.original

        fun alias(other: Node) {
            val a = original
            val b = other.original
            if (a == b) return

            if (a.qualifier == null && b.qualifier == null) {
                a.merge(b)
            }
            else if (a.qualifier == null) {
                if (b.root() == a) a.makeDependencies(b) else b.evacuateFrom(a)
            }
            else if (b.qualifier == null) {
                if (a.root() == b) a.makeDependencies(b) else a.evacuateFrom(b)
            }
            else {
                a.makeDependencies(b)
            }
        }

        private fun makeDependencies(other: Node) {
            dependencies += other
            other.dependencies += this
        }

        private fun evacuateFrom(other: Node) {
            val (existingMembers, newMembers) = other.members.toList().partition { (name, _) -> name in membersImpl }
            other.original = this

            for ((name, member) in newMembers) {
                membersImpl[name] = member
                member.original.qualifier = Qualifier(this, member.original.qualifier!!.memberName)
            }
            for ((name, member) in existingMembers) {
                membersImpl[name]!!.original.merge(member.original)
                membersImpl[name] = member.original
                member.original.qualifier = Qualifier(this, member.original.qualifier!!.memberName)
            }
            other.membersImpl.clear()
            hasSideEffects = hasSideEffects || other.hasSideEffects
            expressions += other.expressions
            dependencies += other.dependencies
            other.expressions.clear()
            other.dependencies.clear()
        }

        private fun merge(other: Node) {
            if (this == other) return

            if (rank < other.rank) {
                other.evacuateFrom(this)
            }
            else {
                evacuateFrom(other)
            }

            if (rank == other.rank) {
                rank++
            }
        }

        fun root(): Node = generateSequence(original) { it.qualifier?.parent?.original }.last()

        fun pathFromRoot(): List<String> =
                generateSequence(original) { it.qualifier?.parent?.original }.mapNotNull { it.qualifier?.memberName }
                        .toList().asReversed()

        override fun toString(): String = (root().localName?.ident ?: "<unknown>") + pathFromRoot().joinToString("") { ".$it" }
    }

    class Qualifier(val parent: Node, val memberName: String)
}