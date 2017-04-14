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

package org.jetbrains.kotlin.android.intention;

import com.intellij.testFramework.TestDataPath;
import org.jetbrains.kotlin.test.JUnit3RunnerWithInners;
import org.jetbrains.kotlin.test.KotlinTestUtils;
import org.jetbrains.kotlin.test.TargetBackend;
import org.jetbrains.kotlin.test.TestMetadata;
import org.junit.runner.RunWith;

import java.io.File;
import java.util.regex.Pattern;

/** This class is generated by {@link org.jetbrains.kotlin.generators.tests.TestsPackage}. DO NOT MODIFY MANUALLY */
@SuppressWarnings("all")
@TestMetadata("idea/testData/android/intention")
@TestDataPath("$PROJECT_ROOT")
@RunWith(JUnit3RunnerWithInners.class)
public class AndroidIntentionTestGenerated extends AbstractAndroidIntentionTest {
    public void testAllFilesPresentInIntention() throws Exception {
        KotlinTestUtils.assertAllTestsPresentByMetadata(this.getClass(), new File("idea/testData/android/intention"), Pattern.compile("^([\\w\\-_]+)\\.kt$"), TargetBackend.ANY, true);
    }

    @TestMetadata("idea/testData/android/intention/addActivityToManifest")
    @TestDataPath("$PROJECT_ROOT")
    @RunWith(JUnit3RunnerWithInners.class)
    public static class AddActivityToManifest extends AbstractAndroidIntentionTest {
        public void testAllFilesPresentInAddActivityToManifest() throws Exception {
            KotlinTestUtils.assertAllTestsPresentByMetadata(this.getClass(), new File("idea/testData/android/intention/addActivityToManifest"), Pattern.compile("^([\\w\\-_]+)\\.kt$"), TargetBackend.ANY, true);
        }

        @TestMetadata("alreadyExists.kt")
        public void testAlreadyExists() throws Exception {
            String fileName = KotlinTestUtils.navigationMetadata("idea/testData/android/intention/addActivityToManifest/alreadyExists.kt");
            doTest(fileName);
        }

        @TestMetadata("inner.kt")
        public void testInner() throws Exception {
            String fileName = KotlinTestUtils.navigationMetadata("idea/testData/android/intention/addActivityToManifest/inner.kt");
            doTest(fileName);
        }

        @TestMetadata("insideBody.kt")
        public void testInsideBody() throws Exception {
            String fileName = KotlinTestUtils.navigationMetadata("idea/testData/android/intention/addActivityToManifest/insideBody.kt");
            doTest(fileName);
        }

        @TestMetadata("nested.kt")
        public void testNested() throws Exception {
            String fileName = KotlinTestUtils.navigationMetadata("idea/testData/android/intention/addActivityToManifest/nested.kt");
            doTest(fileName);
        }

        @TestMetadata("notActivity.kt")
        public void testNotActivity() throws Exception {
            String fileName = KotlinTestUtils.navigationMetadata("idea/testData/android/intention/addActivityToManifest/notActivity.kt");
            doTest(fileName);
        }

        @TestMetadata("private.kt")
        public void testPrivate() throws Exception {
            String fileName = KotlinTestUtils.navigationMetadata("idea/testData/android/intention/addActivityToManifest/private.kt");
            doTest(fileName);
        }

        @TestMetadata("protected.kt")
        public void testProtected() throws Exception {
            String fileName = KotlinTestUtils.navigationMetadata("idea/testData/android/intention/addActivityToManifest/protected.kt");
            doTest(fileName);
        }

        @TestMetadata("simple.kt")
        public void testSimple() throws Exception {
            String fileName = KotlinTestUtils.navigationMetadata("idea/testData/android/intention/addActivityToManifest/simple.kt");
            doTest(fileName);
        }
    }

    @TestMetadata("idea/testData/android/intention/addBroadcastReceiverToManifest")
    @TestDataPath("$PROJECT_ROOT")
    @RunWith(JUnit3RunnerWithInners.class)
    public static class AddBroadcastReceiverToManifest extends AbstractAndroidIntentionTest {
        public void testAllFilesPresentInAddBroadcastReceiverToManifest() throws Exception {
            KotlinTestUtils.assertAllTestsPresentByMetadata(this.getClass(), new File("idea/testData/android/intention/addBroadcastReceiverToManifest"), Pattern.compile("^([\\w\\-_]+)\\.kt$"), TargetBackend.ANY, true);
        }

        @TestMetadata("alreadyExists.kt")
        public void testAlreadyExists() throws Exception {
            String fileName = KotlinTestUtils.navigationMetadata("idea/testData/android/intention/addBroadcastReceiverToManifest/alreadyExists.kt");
            doTest(fileName);
        }

        @TestMetadata("inner.kt")
        public void testInner() throws Exception {
            String fileName = KotlinTestUtils.navigationMetadata("idea/testData/android/intention/addBroadcastReceiverToManifest/inner.kt");
            doTest(fileName);
        }

        @TestMetadata("insideBody.kt")
        public void testInsideBody() throws Exception {
            String fileName = KotlinTestUtils.navigationMetadata("idea/testData/android/intention/addBroadcastReceiverToManifest/insideBody.kt");
            doTest(fileName);
        }

        @TestMetadata("nested.kt")
        public void testNested() throws Exception {
            String fileName = KotlinTestUtils.navigationMetadata("idea/testData/android/intention/addBroadcastReceiverToManifest/nested.kt");
            doTest(fileName);
        }

        @TestMetadata("notReceiver.kt")
        public void testNotReceiver() throws Exception {
            String fileName = KotlinTestUtils.navigationMetadata("idea/testData/android/intention/addBroadcastReceiverToManifest/notReceiver.kt");
            doTest(fileName);
        }

        @TestMetadata("private.kt")
        public void testPrivate() throws Exception {
            String fileName = KotlinTestUtils.navigationMetadata("idea/testData/android/intention/addBroadcastReceiverToManifest/private.kt");
            doTest(fileName);
        }

        @TestMetadata("protected.kt")
        public void testProtected() throws Exception {
            String fileName = KotlinTestUtils.navigationMetadata("idea/testData/android/intention/addBroadcastReceiverToManifest/protected.kt");
            doTest(fileName);
        }

        @TestMetadata("simple.kt")
        public void testSimple() throws Exception {
            String fileName = KotlinTestUtils.navigationMetadata("idea/testData/android/intention/addBroadcastReceiverToManifest/simple.kt");
            doTest(fileName);
        }
    }

    @TestMetadata("idea/testData/android/intention/addServiceToManifest")
    @TestDataPath("$PROJECT_ROOT")
    @RunWith(JUnit3RunnerWithInners.class)
    public static class AddServiceToManifest extends AbstractAndroidIntentionTest {
        public void testAllFilesPresentInAddServiceToManifest() throws Exception {
            KotlinTestUtils.assertAllTestsPresentByMetadata(this.getClass(), new File("idea/testData/android/intention/addServiceToManifest"), Pattern.compile("^([\\w\\-_]+)\\.kt$"), TargetBackend.ANY, true);
        }

        @TestMetadata("alreadyExists.kt")
        public void testAlreadyExists() throws Exception {
            String fileName = KotlinTestUtils.navigationMetadata("idea/testData/android/intention/addServiceToManifest/alreadyExists.kt");
            doTest(fileName);
        }

        @TestMetadata("inner.kt")
        public void testInner() throws Exception {
            String fileName = KotlinTestUtils.navigationMetadata("idea/testData/android/intention/addServiceToManifest/inner.kt");
            doTest(fileName);
        }

        @TestMetadata("insideBody.kt")
        public void testInsideBody() throws Exception {
            String fileName = KotlinTestUtils.navigationMetadata("idea/testData/android/intention/addServiceToManifest/insideBody.kt");
            doTest(fileName);
        }

        @TestMetadata("nested.kt")
        public void testNested() throws Exception {
            String fileName = KotlinTestUtils.navigationMetadata("idea/testData/android/intention/addServiceToManifest/nested.kt");
            doTest(fileName);
        }

        @TestMetadata("notService.kt")
        public void testNotService() throws Exception {
            String fileName = KotlinTestUtils.navigationMetadata("idea/testData/android/intention/addServiceToManifest/notService.kt");
            doTest(fileName);
        }

        @TestMetadata("private.kt")
        public void testPrivate() throws Exception {
            String fileName = KotlinTestUtils.navigationMetadata("idea/testData/android/intention/addServiceToManifest/private.kt");
            doTest(fileName);
        }

        @TestMetadata("protected.kt")
        public void testProtected() throws Exception {
            String fileName = KotlinTestUtils.navigationMetadata("idea/testData/android/intention/addServiceToManifest/protected.kt");
            doTest(fileName);
        }

        @TestMetadata("simple.kt")
        public void testSimple() throws Exception {
            String fileName = KotlinTestUtils.navigationMetadata("idea/testData/android/intention/addServiceToManifest/simple.kt");
            doTest(fileName);
        }
    }

    @TestMetadata("idea/testData/android/intention/implementParcelable")
    @TestDataPath("$PROJECT_ROOT")
    @RunWith(JUnit3RunnerWithInners.class)
    public static class ImplementParcelable extends AbstractAndroidIntentionTest {
        public void testAllFilesPresentInImplementParcelable() throws Exception {
            KotlinTestUtils.assertAllTestsPresentByMetadata(this.getClass(), new File("idea/testData/android/intention/implementParcelable"), Pattern.compile("^([\\w\\-_]+)\\.kt$"), TargetBackend.ANY, true);
        }

        @TestMetadata("allNullableTypes.kt")
        public void testAllNullableTypes() throws Exception {
            String fileName = KotlinTestUtils.navigationMetadata("idea/testData/android/intention/implementParcelable/allNullableTypes.kt");
            doTest(fileName);
        }

        @TestMetadata("allTypes.kt")
        public void testAllTypes() throws Exception {
            String fileName = KotlinTestUtils.navigationMetadata("idea/testData/android/intention/implementParcelable/allTypes.kt");
            doTest(fileName);
        }

        @TestMetadata("alreadyImplemented.kt")
        public void testAlreadyImplemented() throws Exception {
            String fileName = KotlinTestUtils.navigationMetadata("idea/testData/android/intention/implementParcelable/alreadyImplemented.kt");
            doTest(fileName);
        }

        @TestMetadata("constructorProperties.kt")
        public void testConstructorProperties() throws Exception {
            String fileName = KotlinTestUtils.navigationMetadata("idea/testData/android/intention/implementParcelable/constructorProperties.kt");
            doTest(fileName);
        }

        @TestMetadata("constructorPropertiesWithParameters.kt")
        public void testConstructorPropertiesWithParameters() throws Exception {
            String fileName = KotlinTestUtils.navigationMetadata("idea/testData/android/intention/implementParcelable/constructorPropertiesWithParameters.kt");
            doTest(fileName);
        }

        @TestMetadata("missingConstructor.kt")
        public void testMissingConstructor() throws Exception {
            String fileName = KotlinTestUtils.navigationMetadata("idea/testData/android/intention/implementParcelable/missingConstructor.kt");
            doTest(fileName);
        }

        @TestMetadata("missingDescribeContents.kt")
        public void testMissingDescribeContents() throws Exception {
            String fileName = KotlinTestUtils.navigationMetadata("idea/testData/android/intention/implementParcelable/missingDescribeContents.kt");
            doTest(fileName);
        }

        @TestMetadata("missingWriteToParcel.kt")
        public void testMissingWriteToParcel() throws Exception {
            String fileName = KotlinTestUtils.navigationMetadata("idea/testData/android/intention/implementParcelable/missingWriteToParcel.kt");
            doTest(fileName);
        }

        @TestMetadata("simple.kt")
        public void testSimple() throws Exception {
            String fileName = KotlinTestUtils.navigationMetadata("idea/testData/android/intention/implementParcelable/simple.kt");
            doTest(fileName);
        }

        @TestMetadata("withTransient.kt")
        public void testWithTransient() throws Exception {
            String fileName = KotlinTestUtils.navigationMetadata("idea/testData/android/intention/implementParcelable/withTransient.kt");
            doTest(fileName);
        }
    }

    @TestMetadata("idea/testData/android/intention/redoParcelable")
    @TestDataPath("$PROJECT_ROOT")
    @RunWith(JUnit3RunnerWithInners.class)
    public static class RedoParcelable extends AbstractAndroidIntentionTest {
        public void testAllFilesPresentInRedoParcelable() throws Exception {
            KotlinTestUtils.assertAllTestsPresentByMetadata(this.getClass(), new File("idea/testData/android/intention/redoParcelable"), Pattern.compile("^([\\w\\-_]+)\\.kt$"), TargetBackend.ANY, true);
        }

        @TestMetadata("inderectParcelable.kt")
        public void testInderectParcelable() throws Exception {
            String fileName = KotlinTestUtils.navigationMetadata("idea/testData/android/intention/redoParcelable/inderectParcelable.kt");
            doTest(fileName);
        }

        @TestMetadata("init.kt")
        public void testInit() throws Exception {
            String fileName = KotlinTestUtils.navigationMetadata("idea/testData/android/intention/redoParcelable/init.kt");
            doTest(fileName);
        }

        @TestMetadata("initWithExtraStatements.kt")
        public void testInitWithExtraStatements() throws Exception {
            String fileName = KotlinTestUtils.navigationMetadata("idea/testData/android/intention/redoParcelable/initWithExtraStatements.kt");
            doTest(fileName);
        }

        @TestMetadata("noParcelable.kt")
        public void testNoParcelable() throws Exception {
            String fileName = KotlinTestUtils.navigationMetadata("idea/testData/android/intention/redoParcelable/noParcelable.kt");
            doTest(fileName);
        }

        @TestMetadata("oldField.kt")
        public void testOldField() throws Exception {
            String fileName = KotlinTestUtils.navigationMetadata("idea/testData/android/intention/redoParcelable/oldField.kt");
            doTest(fileName);
        }

        @TestMetadata("oldFieldSecondary.kt")
        public void testOldFieldSecondary() throws Exception {
            String fileName = KotlinTestUtils.navigationMetadata("idea/testData/android/intention/redoParcelable/oldFieldSecondary.kt");
            doTest(fileName);
        }

        @TestMetadata("simple.kt")
        public void testSimple() throws Exception {
            String fileName = KotlinTestUtils.navigationMetadata("idea/testData/android/intention/redoParcelable/simple.kt");
            doTest(fileName);
        }
    }

    @TestMetadata("idea/testData/android/intention/removeParcelable")
    @TestDataPath("$PROJECT_ROOT")
    @RunWith(JUnit3RunnerWithInners.class)
    public static class RemoveParcelable extends AbstractAndroidIntentionTest {
        public void testAllFilesPresentInRemoveParcelable() throws Exception {
            KotlinTestUtils.assertAllTestsPresentByMetadata(this.getClass(), new File("idea/testData/android/intention/removeParcelable"), Pattern.compile("^([\\w\\-_]+)\\.kt$"), TargetBackend.ANY, true);
        }

        @TestMetadata("inderectParcelable.kt")
        public void testInderectParcelable() throws Exception {
            String fileName = KotlinTestUtils.navigationMetadata("idea/testData/android/intention/removeParcelable/inderectParcelable.kt");
            doTest(fileName);
        }

        @TestMetadata("init.kt")
        public void testInit() throws Exception {
            String fileName = KotlinTestUtils.navigationMetadata("idea/testData/android/intention/removeParcelable/init.kt");
            doTest(fileName);
        }

        @TestMetadata("initWithExtraStatements.kt")
        public void testInitWithExtraStatements() throws Exception {
            String fileName = KotlinTestUtils.navigationMetadata("idea/testData/android/intention/removeParcelable/initWithExtraStatements.kt");
            doTest(fileName);
        }

        @TestMetadata("noParcelable.kt")
        public void testNoParcelable() throws Exception {
            String fileName = KotlinTestUtils.navigationMetadata("idea/testData/android/intention/removeParcelable/noParcelable.kt");
            doTest(fileName);
        }

        @TestMetadata("simple.kt")
        public void testSimple() throws Exception {
            String fileName = KotlinTestUtils.navigationMetadata("idea/testData/android/intention/removeParcelable/simple.kt");
            doTest(fileName);
        }
    }
}
