package foo

// NOTE THIS FILE IS AUTO-GENERATED by the generateTestDataForReservedWords.kt. DO NOT EDIT!

interface Trait {
    fun foo(eval: String)
}

class TraitImpl : Trait {
    override fun foo(eval: String) {
    assertEquals("123", eval)
    testRenamed("eval", { eval })
}
}

class TestDelegate : Trait by TraitImpl() {
    fun test() {
        foo("123")
    }
}

fun box(): String {
    TestDelegate().test()

    return "OK"
}