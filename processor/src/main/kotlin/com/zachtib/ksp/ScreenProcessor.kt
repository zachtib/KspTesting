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
import com.squareup.kotlinpoet.*
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
            .writeTo(codeGenerator, Dependencies(true))

        return invalid
    }

    private fun buildScreenFun(screenClass: KSClassDeclaration): FunSpec {
        val (parameters, statement) = processScreenParameter(screenClass)
        val className = screenClass.toClassName()
        return FunSpec.builder("navigateTo${className.simpleName}")
            .addParameters(parameters)
            .returns(className)
            .addCode("return %T(", className)
            .addCode(statement)
            .addCode(")")
            .build()
    }

    @OptIn(KspExperimental::class)
    private fun processScreenParameter(screenClass: KSClassDeclaration): Pair<List<ParameterSpec>, CodeBlock> {
        val parameters = mutableListOf<ParameterSpec>()
        val statements = mutableListOf<CodeBlock>()

        val constructorParameters = screenClass.primaryConstructor?.parameters

        constructorParameters?.forEach { parameter ->
            if (parameter.isAnnotationPresent(ScreenKey::class)) {
                val (params, invocation) = expandScreenKeyParameter(parameter)

                parameters.addAll(params)
                statements += invocation
            } else if (parameter.isAnnotationPresent(AcceptsScreenKey::class)) {
                val ptype = parameter.type
                val receiverClass = ptype.resolve().declaration as KSClassDeclaration
                val (receiverParameters, receiverStatement) = processScreenParameter(receiverClass)

                parameters += receiverParameters
                statements += CodeBlock.builder()
                    .add("%T(", receiverClass.toClassName())
                    .add(receiverStatement)
                    .add(")")
                    .build()
            } else {
                // Just append
                val shortName = parameter.name?.getShortName() ?: "unknown"
                parameters.add(ParameterSpec(shortName, parameter.type.toTypeName()))
                statements.add(CodeBlock.of(shortName))
            }
        }

//        val codeBlock = CodeBlock.builder()
//            .apply {
//                statements.forEach { statement ->
//                    add(statement)
//                    add(",")
//                }
//            }
//            .build()

        return parameters to statements.joinToCode()
    }

    private fun expandScreenKeyParameter(param: KSValueParameter): Pair<List<ParameterSpec>, CodeBlock> {
        val ksClass = param.type.resolve().declaration as KSClassDeclaration
        val specs = ksClass.getConstructorParameters()
        val name = ksClass.simpleName.getShortName()
        val block = CodeBlock.of("%T(${specs.joinToString { it.name }})", ksClass.toClassName())
        return specs to block
    }

    private fun KSClassDeclaration.getConstructorParameters(): List<ParameterSpec> {
        val primaryConstructor = primaryConstructor ?: return emptyList()
        return primaryConstructor.parameters.map {
            val builder = ParameterSpec.builder(it.name!!.getShortName(), it.type.toTypeName())
//            if (it.hasDefault) {
                // TODO: KSP can see that there is a default, but can't read it
                // Potentially would just need to generate manual overloads with different
                // parameters to handle defaults?
//            }
            builder.build()
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