package com.zachtib.ksp

import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.isAnnotationPresent
import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSValueParameter
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.ksp.toClassName
import com.squareup.kotlinpoet.ksp.toTypeName
import com.squareup.kotlinpoet.ksp.writeTo

class ScreenProcessor(
    val codeGenerator: CodeGenerator,
    val options: Map<String, String>,
) : SymbolProcessor {

    var invoked = false

    override fun process(resolver: Resolver): List<KSAnnotated> {
        if (invoked) {
            return emptyList()
        }
        invoked = true

        val symbolsWithAnnotation: Sequence<KSAnnotated> = resolver.getSymbolsWithAnnotation<Screen>()
        val (valid, invalid) = symbolsWithAnnotation.validateAll()
        val screenClasses = valid.filterIsInstance<KSClassDeclaration>()

        val name = "Screens"

        FileSpec.builder("com.zachtib.test", name)
            .addType(
                TypeSpec.classBuilder(name)
                    .addFunctions(
                        screenClasses.map { screenClass: KSClassDeclaration ->
                            buildScreenFun(screenClass)
                        }
                    )
                    .build()
            )
            .build()
            .writeTo(codeGenerator, Dependencies(false))

        return invalid
    }

    @OptIn(KspExperimental::class)
    private fun buildScreenFun(screenClass: KSClassDeclaration): FunSpec {
        val className = screenClass.toClassName()
        val constructorParameters = screenClass.primaryConstructor?.parameters
        val builder = FunSpec.builder("navigateTo${className.simpleName}")
            .returns(className)

        if (constructorParameters.isNullOrEmpty()) {
            builder.addStatement("return %T()", className)
        } else {
            val parameters = mutableListOf<ParameterSpec>()
            val statements = mutableListOf<String>()
            for (parameter: KSValueParameter in constructorParameters) {
                if (parameter.isAnnotationPresent(ScreenKey::class)
//                    && parameter.type.hasModifiers(Modifier.DATA)
                ) {
                    // Expand the parameters
                    val ptype = parameter.type
                    val pclass = ptype.resolve().declaration as KSClassDeclaration
                    val pname = pclass.simpleName.getShortName()
                    val strings = mutableListOf<String>()
                    for (pspec in pclass.getConstructorParameters()) {
                        parameters.add(pspec)
                        strings.add(pspec.name)
                    }
                    statements.add("$pname(${strings.joinToString()})")
                } else {
                    // Just append
                    val shortName = parameter.name?.getShortName() ?: "unknown"
                    parameters.add(ParameterSpec(shortName, parameter.type.toTypeName()))
                    statements.add(shortName)
                }
            }
            builder.addParameters(parameters)
                .addStatement("return %T(${statements.joinToString()})", className)
        }
        return builder.build()
    }

    private fun KSClassDeclaration.getConstructorParameters(): List<ParameterSpec> {
        val primaryConstructor = primaryConstructor ?: return emptyList()
        return primaryConstructor.parameters.map {
            ParameterSpec(it.name!!.getShortName(), it.type.toTypeName())
        }
    }

//
//    inner class ScreenVisitor : KSDefaultVisitor<Any, FunSpec>() {
//        override fun visitClassifierReference(reference: KSClassifierReference, data: Any): FunSpec {
//            return super.visitClassifierReference(reference, data)
//        }
//
//        override fun defaultHandler(node: KSNode, data: Any): FunSpec {
//            TODO("Not yet implemented")
//        }
//    }
}