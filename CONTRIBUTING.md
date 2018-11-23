# How to contribute to ApngDrawable

## Pull requests are always welcome
First of all, thank you so much for taking your time to contribute! We always welcome your ideas and feedback. Please feel free to make any pull requests.

* [Create a new issue](https://github.com/line/apng-drawable/issues) to ask questions, report bugs or propose new features and improvements.
* [Send a pull request](https://github.com/line/apng-drawable/pulls) to contribute your work.

## Contributor license agreement
If you are sending a pull request and it's a non-trivial change beyond fixing typos, make sure to sign [ICLA](https://cla-assistant.io/line/apng-drawable) (individual contributor license agreement). Contact us if you need CCLA (corporate contributor license agreement).

## Coding conventions
ApngDrawable follows [Kotlin coding conventions](http://kotlinlang.org/docs/reference/coding-conventions.html). Class members are declared in the following order.

* Properties (var, val)
* Initialization block (init)
* Secondary constructors (constructor)
* Functions (fun)
* Extensions of properties (val T.x or var T.x)
* Extensions of functions (fun T.foo())
* Nested or inner classes, enums, interfaces, and objects
* Companion objects (companion object)

Here is an example.

```kotlin
class Klass(val parameter: Value) {

    // Read-only properties
    val publicValue: Value = Value()
    private val value: Value = Value()

    // Read-write properties
    var publicVariable: Value = Value()
    private var variable: Value = Value()

    // Initialization block
    init {
        // ...
    }

    // Other constructors
    constructor(firstValue: Int, secondValue: Int) : this(Value())

    // Functions
    fun publicFunction() = {}

    private fun privateFunction() = {}

    // Extensions of read-only properties
    val <T> T.publicReadonly: Value get() = Value()
    private val <T> T.readonly: Value get() = Value()

    // Extensions of read-write properties
    var <T> T.publicReadWrite: Value
        get()
        set()
    private var <T> T.readWrite: Value
        get()
        set()

    // Extensions of functions
    fun <T> T.publicFunction() {}

    private fun <T>.privateFunction() {}
    // Nested or inner classes, enums, interfaces, and objects
    enum class EnumType private constructor(val enumField: Int) {
        DEFAULT(1),
        A_ENUM_TYPE(2),
    }

    private data class NestedDataClass(val int: Int)

    // Constants
    companion object {
        const val PRIMITIVE_CONSTANT = 0

        @JvmField
        val NON_PRIMITIVE_CONSTANT: OtherClass = OtherClass()
    }
}

```

When you add comments to public properties and methods, make sure to follow the Javadoc standard format.
