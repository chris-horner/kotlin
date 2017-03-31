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

import com.google.gwt.dev.js.rhino.CodePosition
import com.google.gwt.dev.js.rhino.ErrorReporter
import com.intellij.openapi.util.io.FileUtil
import org.jetbrains.kotlin.js.backend.ast.JsBlock
import org.jetbrains.kotlin.js.backend.ast.JsGlobalBlock
import org.jetbrains.kotlin.js.backend.ast.JsNode
import org.jetbrains.kotlin.js.backend.ast.JsProgram
import org.jetbrains.kotlin.js.inline.util.collectDefinedNames
import org.jetbrains.kotlin.js.inline.util.fixForwardNameReferences
import org.jetbrains.kotlin.js.parser.parse
import java.io.File

class DeadCodeElimination(val root: JsNode, val logConsumer: (String) -> Unit) {
    val moduleMapping = mutableMapOf<JsBlock, String>()
    val usedNames = mutableSetOf<String>()

    fun apply() {
        val context = Context()

        val topLevelVars = collectDefinedNames(root)
        context.addLocalVars(topLevelVars)
        for (name in topLevelVars) {
            context.nodes[name]!!.alias(context.globalScope.member(name.ident))
        }

        val analyzer = Analyzer(context)
        analyzer.moduleMapping += moduleMapping
        root.accept(analyzer)

        val usageFinder = UsageFinder(context, analyzer.analysisResult, logConsumer)
        root.accept(usageFinder)

        for (usedName in usedNames) {
            val path = usedName.split(".")
            val node = path.fold(context.globalScope) { node, part -> node.member(part) }
            usageFinder.use(node)
        }

        Eliminator(analyzer.analysisResult).accept(root)
    }

    companion object {
        fun run(fileMap: Map<String, String>, usedNames: Set<String>, logConsumer: (String) -> Unit) {
            val files = fileMap.keys.map { File(it) }
            val targetFiles = fileMap.values.map { File(it) }

            val codeList = files.map { FileUtil.loadFile(it) }
            val program = JsProgram()
            val dce = DeadCodeElimination(program.globalBlock, logConsumer)

            val blocks = files.zip(codeList).map { (file, code) ->
                val block = JsGlobalBlock()
                block.statements += parse(code, reporter, program.scope, file.path)
                dce.moduleMapping[block] = file.nameWithoutExtension
                block
            }
            program.globalBlock.statements += blocks
            program.globalBlock.fixForwardNameReferences()

            dce.usedNames += usedNames
            dce.apply()

            for ((file, block) in targetFiles.zip(blocks)) {
                FileUtil.writeToFile(file, block.toString())
            }
        }

        private val reporter = object : ErrorReporter {
            override fun warning(message: String, startPosition: CodePosition, endPosition: CodePosition) {
                println("[WARN] at ${startPosition.line}, ${startPosition.offset}: $message")
            }

            override fun error(message: String, startPosition: CodePosition, endPosition: CodePosition) {
                println("[ERRO] at ${startPosition.line}, ${startPosition.offset}: $message")
            }
        }
    }
}