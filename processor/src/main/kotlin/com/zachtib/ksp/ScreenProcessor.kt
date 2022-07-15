package com.zachtib.ksp

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.*
import com.google.devtools.ksp.visitor.KSDefaultVisitor
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

        val visitor = ScreenVisitor()

        FileSpec.builder("com.zachtib.test", name)
            .addType(
                TypeSpec.classBuilder(name)
                    .applyForEach(screenClasses) { screenClass ->
                        addFunction(screenClass.accept(visitor, ""))
                    }
                    .build()
            )
            .build()
            .writeTo(codeGenerator, Dependencies(true))

        return invalid
    }


    inner class ScreenVisitor : KSDefaultVisitor<Any, FunSpec>() {
        override fun visitClassDeclaration(classDeclaration: KSClassDeclaration, data: Any): FunSpec {
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
                if (parameter.isAnnotatedWith<ScreenKey>()) {
                    val ksClass = parameter.type.resolve().declaration as KSClassDeclaration
                    val specs = ksClass.getPrimaryConstructorParameters()
                    ksClass.simpleName.getShortName()
                    val block = CodeBlock.of("%T(${specs.joinToString { it.name }})", ksClass.toClassName())

                    parameters.addAll(specs)
                    statements += block
                } else if (parameter.isAnnotatedWith<AcceptsScreenKey>()) {
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

        override fun defaultHandler(node: KSNode, data: Any): FunSpec {
            throw NotImplementedError("Was asked to visit unexpected node: $node")
        }
    }
}