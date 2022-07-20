@file:OptIn(KspExperimental::class)

package com.zachtib.ksp

import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.isAnnotationPresent
import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSNode
import com.google.devtools.ksp.visitor.KSDefaultVisitor
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ksp.toClassName
import com.squareup.kotlinpoet.ksp.toTypeName
import com.squareup.kotlinpoet.ksp.writeTo
import com.zachtib.util.applyForEach

class ScreenProcessor(
    private val codeGenerator: CodeGenerator,
    private val options: Map<String, String>,
) : SymbolProcessor {

    var invoked = false

    override fun process(resolver: Resolver): List<KSAnnotated> {
        val screenAnnotationName = requireNotNull(Screen::class.qualifiedName)
        val symbolsWithAnnotation = resolver.getSymbolsWithAnnotation(screenAnnotationName)
        val (valid, invalid) = symbolsWithAnnotation.validateAll()
        val screenClasses = valid.filterIsInstance<KSClassDeclaration>()

        if (valid.isEmpty()) {
            return invalid
        } else if (invoked) {
            throw IllegalArgumentException(symbolsWithAnnotation.joinToString { it.toString() })
        }
        invoked = true

        val packageName = options["package"] ?: "com.zachtib.test"
        val name = options["class"] ?: "Screens"

        val visitor = ScreenVisitor()

        FileSpec.builder(packageName, name)
            .addType(
                TypeSpec.classBuilder(name)
                    .applyForEach(screenClasses) { screenClass ->
                        addFunction(screenClass.accept(visitor, null))
                    }
                    .build()
            )
            .build()
            .writeTo(codeGenerator, Dependencies(true))

        return invalid
    }


    inner class ScreenVisitor : KSDefaultVisitor<Any?, FunSpec>() {
        override fun visitClassDeclaration(classDeclaration: KSClassDeclaration, data: Any?): FunSpec {
            val (parameters, statement) = processScreenParameters(classDeclaration)
            val className = classDeclaration.toClassName()
            return FunSpec.builder("navigateTo${className.simpleName}")
                .addParameters(parameters)
                .returns(className)
                .addCode("return %T(", className)
                .addCode(statement)
                .addCode(")")
                .build()
        }

        private fun processScreenParameters(screenClass: KSClassDeclaration): Pair<List<ParameterSpec>, CodeBlock> {
            val parameters = mutableListOf<ParameterSpec>()
            val statements = mutableListOf<CodeBlock>()

            val constructorParameters = screenClass.primaryConstructor?.parameters

            constructorParameters?.forEach { parameter ->
                if (parameter.isAnnotationPresent(ScreenKey::class)) {
                    val ksClass = parameter.type.resolve().declaration as KSClassDeclaration
                    val specs = ksClass.getPrimaryConstructorParameters()
                    ksClass.simpleName.getShortName()
                    val block = CodeBlock.of("%T(${specs.joinToString { it.name }})", ksClass.toClassName())

                    parameters.addAll(specs)
                    statements += block
                } else if (parameter.isAnnotationPresent(AcceptsScreenKey::class)) {
                    val ptype = parameter.type
                    val receiverClass = ptype.resolve().declaration as KSClassDeclaration
                    val (receiverParameters, receiverStatement) = processScreenParameters(receiverClass)

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

            return parameters to statements.joinToCode()
        }

        override fun defaultHandler(node: KSNode, data: Any?): FunSpec {
            throw NotImplementedError("Was asked to visit unexpected node: $node")
        }
    }
}
