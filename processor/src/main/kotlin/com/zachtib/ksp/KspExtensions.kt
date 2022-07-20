package com.zachtib.ksp

import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.validate
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.ksp.toTypeName

data class ValidationResults(
    val valid: List<KSAnnotated>,
    val invalid: List<KSAnnotated>,
)

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
