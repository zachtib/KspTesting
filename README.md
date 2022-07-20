# KSP Experiments

## Generate a Screen "Navigator" w/ KSP

This is a sample KSP processor that collects all classes with a `@Screen` annotation and builds a class for "navigating" (in this case, building/returning) to those screens based on their constructors.

In addition to just raw parameters, it supports using a `data class` as a parameter, and can even "explode" the fields within it, ie:

```kotlin
data class ScreenKey(val foo: String, val bar: String)

@Screen 
class MyScreen(
    @ScreenKey val key: ScreenKey,
)
```

will generate a function like

```kotlin
fun navigateToMyScreen(foo: String, bar: String)
```

Alternatively, it can also push the screen key "down" into another parameter that accepts the key in *its* constructor, like:

```kotlin
data class ScreenKey(val foo: String, val bar: String)

@Screen 
class MyOtherScreen(
    @AcceptsScreenKey val viewModel: MyOtherViewModel,
)

class MyOtherViewModel(
    @ScreenKey val key: ScreenKey,
)
```

This will generate a similar function to the previous example, but with the key being passed down into the ViewModel class instead.

The example screens live in the `sample` module under `com.zachtib.test.screens`, and you can see an example of using the generated class in `Main.kt` under `com.zachtib.test`. The Processor itself is in the `processor` module: `com.zachtib.ksp.ScreenProcessor`.

None of this should be assumed to be a good idea, this was all just to practice writing a KSP processor.