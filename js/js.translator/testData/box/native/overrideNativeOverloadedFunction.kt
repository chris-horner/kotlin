external open class A {
    open fun f(x: Int) = "number"

    open fun f(x: String) = "string"
}

class B : A() {
    fun g(x: Int) = "[${f(x)}]"

    fun g(x: String) = "[${f(x)}]"
}

interface I {
    fun f(x: Int): String
}

class C : A(), I

external interface J {
    fun f(x: Int): String
}

class D : A(), J

fun box(): String {
    var result = B().g(23) + B().g("foo")
    if (result != "[number][string]") return "fail1: $result"

    result = C().f(42)
    if (result != "number") return "fail2: $result"

    result = D().f("bar")
    if (result != "string") return "fail3: $result"

    return "OK"
}