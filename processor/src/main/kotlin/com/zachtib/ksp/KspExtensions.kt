package com.zachtib.ksp

import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSModifierListOwner
import com.google.devtools.ksp.symbol.Modifier
import com.google.devtools.ksp.validate

inline fun <reified T> Resolver.getSymbolsWithAnnotation() = getSymbolsWithAnnotation(T::class.qualifiedName!!)

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