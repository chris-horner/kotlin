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

package org.jetbrains.kotlin.android.intention

import com.android.SdkConstants
import com.intellij.codeInsight.intention.IntentionAction
import com.intellij.openapi.util.io.FileUtil
import com.intellij.util.PathUtil
import org.jetbrains.kotlin.android.KotlinAndroidTestCase
import org.jetbrains.kotlin.idea.test.ConfigLibraryUtil
import org.jetbrains.kotlin.idea.test.PluginTestCaseBase
import org.jetbrains.kotlin.test.InTextDirectivesUtils
import org.jetbrains.kotlin.test.KotlinTestUtils
import java.io.File


open class AbstractAndroidIntentionTest : KotlinAndroidTestCase() {
    fun doTest(path: String) {
        val testFile = File(path)
        val testFileText = FileUtil.loadFile(testFile)
        val intentionClassName = InTextDirectivesUtils.findStringWithPrefixes(testFileText, "// INTENTION_CLASS: ")
                                 ?: error("Intention class not found!")

        val notAvailable = InTextDirectivesUtils.isDirectiveDefined(testFileText, "// NOT_AVAILABLE")
        val withRuntime = InTextDirectivesUtils.isDirectiveDefined(testFileText, "// WITH_RUNTIME")

        try {
            if (withRuntime) {
                ConfigLibraryUtil.configureKotlinRuntime(myFixture.module)
            }

            val sourceFile = myFixture.copyFileToProject(path, "src/${PathUtil.getFileName(path)}")
            myFixture.configureFromExistingVirtualFile(sourceFile)

            val intention = Class.forName(intentionClassName).newInstance() as? IntentionAction ?: error("Failed to create intention!")
            if (!intention.isAvailable(myFixture.project, myFixture.editor, myFixture.file)) {
                if (notAvailable) {
                    return
                }

                error("Intention is not available!")
            }

            myFixture.launchAction(intention)
            myFixture.checkResultByFile("$path.expected")
        }
        finally {
            if (withRuntime) {
                ConfigLibraryUtil.unConfigureKotlinRuntime(myFixture.module)
            }
        }
    }

    override fun createManifest() {
        myFixture.copyFileToProject("idea/testData/android/AndroidManifest.xml", SdkConstants.FN_ANDROID_MANIFEST_XML)
    }

    override fun getTestDataPath() = KotlinTestUtils.getHomeDirectory()
}
