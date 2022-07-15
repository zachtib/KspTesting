package com.zachtib.ksp

import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.isAnnotationPresent
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.*
import com.google.devtools.ksp.validate
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.ksp.toTypeName

inline fun <reified T> Resolver.getSymbolsWithAnnotation() = getSymbolsWithAnnotation(T::class.qualifiedName!!)

@OptIn(KspExperimental::class)
inline fun <reified T : Annotation> KSAnnotated.isAnnotatedWith(): Boolean {
    return isAnnotationPresent(T::class)
//    return annotations.any {
//        it.shortName.getShortName() == T::class.simpleName &&
//                it.fqName == T::class.qualifiedName
//    }
}

val KSAnnotation.fqName: String?
    get() = annotationType.resolve().declaration.qualifiedName?.asString()

data class ValidationResults(
    val valid: List<KSAnnotated>,
    val invalid: List<KSAnnotated>,
)

fun KSModifierListOwner.hasModifiers(vararg modifiers: Modifier): Boolean {
    return this.modifiers.containsAll(modifiers.asList())
}

fun Sequence<KSAnnotated>.validateAll(): ValidationResults {
    val valid = mutableListOf<KSAnnotated>()
    val invalid = mutableListOf<KSAnnotated>()
    for (element in this) {
        if (element.validate()) {
            valid += element
        } else {
            invalid += element
        }
    }
    return ValidationResults(valid, invalid)
}

fun KSClassDeclaration.getPrimaryConstructorParameters(): List<ParameterSpec> {
    val primaryConstructor = primaryConstructor ?: return emptyList()
    return primaryConstructor.parameters.map {
        val builder = ParameterSpec.builder(it.name!!.getShortName(), it.type.toTypeName())
//        if (it.hasDefault) {
//         TODO: KSP can see that there is a default, but can't read it
//         Potentially would just need to generate manual overloads with different
//         parameters to handle defaults?
//        }
        builder.build()
    }
}

fun <T : Any, E : Any> T.applyForEach(
    elements: Collection<E>?,
    action: T.(E) -> Unit
): T {
    if (elements != null) {
        for (element in elements) {
            action(element)
        }
    }
    return this
}