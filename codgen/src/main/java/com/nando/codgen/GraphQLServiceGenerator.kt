package com.nando.codgen

import com.squareup.kotlinpoet.*
import java.io.File
import kotlin.io.path.Path

object GraphQLServiceGenerator {

    private const val GENERATED_FILE_PACKAGE = "com.nando.codegen.generated.graphql"
    private val path = Path(getGeneratedDirectory())

    @JvmStatic
    fun main(args: Array<String>) {
        val schema = readFileDirectlyAsText(
            getMetaDataFilePath()
        )
//        generateRepoMethodsForQuery(schema)
//        generateRepoMethodsForMutation(schema)
        generateModels(schema)

    }

    private fun generateModels(schema: String) {
        val words = schema.split("\\s+".toRegex()).map { word ->
            word.replace("""^[,\.]|[,\.]$""".toRegex(), "")
        }
        var i = 0
        val models = hashMapOf<String, MutableList<String>>()
        while (i < words.size) {
            val model = mutableListOf<String>()
            if ((words[i] == "type" || words[i] == "input") && (words[i + 1] != "Query" && words[i + 1] != "Mutation")) {
                val className = words[i + 1]
                while (words[i + 3] != "}") {
                    model.add(words[i + 3])
                    i++
                }
                models.put(className, model)
            } else {
                i++
            }
        }
        models.forEach {
            val fileSpec =
                FileSpec.builder(
                    "$GENERATED_FILE_PACKAGE.data.models",
                    it.key.trim()
                )
            val typeSpec = TypeSpec.classBuilder(it.key.trim())
                .addModifiers(KModifier.DATA)
            var i = 0
            val constructorBuilder = FunSpec.constructorBuilder()
            while (i < it.value.size - 1) {
                val parameterName = it.value[i].split(":")[0].trim()
                val parameterType = it.value[i+1].trim()
                println("$parameterName==$parameterType")
                when (val parameterType = it.value[i + 1]) {
                    "String", "String!" -> {
                        constructorBuilder.addParameter(
                            parameterName,
                            String::class.asClassName()
                                .copy(nullable = it.value[i + 1].contains("!"))
                        )
                    }
                    "Int", "Int!" -> {
                        constructorBuilder.addParameter(
                           parameterName,
                            Int::class.asClassName()
                                .copy(nullable = it.value[i + 1].contains("!"))
                        )
                    }
                    "Float", "Float!" -> {
                        constructorBuilder.addParameter(
                            parameterName,
                            Float::class.asClassName()
                                .copy(nullable = it.value[i + 1].contains("!"))
                        )
                    }
                    else -> {
                        if(it.value[i+1].contains("[")){

                        }else {
                            constructorBuilder.addParameter(
                                parameterName,
                                ClassName(
                                    "$GENERATED_FILE_PACKAGE.data.models",
                                    parameterType
                                ).copy(
                                    nullable = it.value[i + 1].contains("!")
                                )
                            )
                        }

                    }
                }
                i += 2
            }
            typeSpec.primaryConstructor(constructorBuilder.build())
            fileSpec.addType(typeSpec.build())
            fileSpec.build().writeTo(path)
        }


    }

    private fun generateRepoMethodsForQuery(schema: String) {
        var query = schema.substringAfter("type Query {")
        query = query.substringBefore("}")
        val methods = query.split("\r?\n|\r".toRegex()).toTypedArray()
        val fileSpec =
            FileSpec.builder("$GENERATED_FILE_PACKAGE.domain.repos", "Repository")
        val typeSpec = TypeSpec.interfaceBuilder("Repository")
        methods.forEachIndexed { index, method ->
            if (index != 0) {
                if (method.contains("(")) {
                    val returnType = method.substring(method.lastIndexOf(':') + 1)
                    println(returnType)
                    val methodName = method.split("(")[0]
                    val funSpec = FunSpec.builder(methodName.trim())
                        .addModifiers(KModifier.ABSTRACT)
                    when (returnType) {
                        "String", "String!" -> {
                            funSpec.returns(String::class)
                        }
                        "Int", "Int!" -> {
                            funSpec.returns(Int::class)
                        }
                        "Float", "Float!" -> {
                            funSpec.returns(Float::class)
                        }
                        else -> {
                            funSpec.returns(
                                ClassName(
                                    "$GENERATED_FILE_PACKAGE.data.models",
                                    returnType.split("!")[0].trim()
                                ).copy(nullable = returnType.contains("!"))
                            )

                        }
                    }
                    typeSpec.addFunction(funSpec.build())
                } else {
                    val methodName = method.split(":")[0]
                    if (method.isNotBlank()) {
                        val returnType = method.substring(method.lastIndexOf(':') + 1)
                        val funSpec = FunSpec.builder(methodName.trim())
                            .addModifiers(KModifier.ABSTRACT)
                        when (returnType) {
                            "String", "String!" -> {
                                funSpec.returns(String::class)
                            }
                            "Int", "Int!" -> {
                                funSpec.returns(Int::class)
                            }
                            "Float", "Float!" -> {
                                funSpec.returns(Float::class)
                            }
                            else -> {
                                funSpec.returns(
                                    ClassName(
                                        "$GENERATED_FILE_PACKAGE.data.models",
                                        returnType.split("!")[0].trim()
                                    ).copy(nullable = returnType.contains("!"))
                                )

                            }
                        }
                        typeSpec.addFunction(funSpec.build())
                    }
                }
            }
            println(method)
        }
        fileSpec.addType(typeSpec.build())
        fileSpec.build().writeTo(path)
    }

    private fun generateRepoMethodsForMutation(schema: String) {
        var mutation = schema.substringAfter("type Mutation {")
        mutation = mutation.substringBefore("}")
        val methods = mutation.split("\r?\n|\r".toRegex()).toTypedArray()
        val fileSpec =
            FileSpec.builder("$GENERATED_FILE_PACKAGE.domain.repos", "Repository")
        val typeSpec = TypeSpec.interfaceBuilder("Repository")
        methods.forEachIndexed { index, method ->
            if (index != 0) {
                if (method.contains("(")) {
                    val returnType = method.substring(method.lastIndexOf(':') + 1)
                    println(returnType)
                    val methodName = method.split("(")[0]
                    val funSpec = FunSpec.builder(methodName.trim())
                        .addModifiers(KModifier.ABSTRACT)
                    when (returnType) {
                        "String", "String!" -> {
                            funSpec.returns(String::class)
                        }
                        "Int", "Int!" -> {
                            funSpec.returns(Int::class)
                        }
                        "Float", "Float!" -> {
                            funSpec.returns(Float::class)
                        }
                        else -> {
                            funSpec.returns(
                                ClassName(
                                    "$GENERATED_FILE_PACKAGE.data.models",
                                    returnType.split("!")[0].trim()
                                )
                            )

                        }
                    }
                    typeSpec.addFunction(funSpec.build())
                } else {
                    val methodName = method.split(":")[0]
                    if (method.isNotBlank()) {
                        val returnType = method.substring(method.lastIndexOf(':') + 1)
                        val funSpec = FunSpec.builder(methodName.trim())
                            .addModifiers(KModifier.ABSTRACT)
                        when (returnType) {
                            "String", "String!" -> {
                                funSpec.returns(String::class)
                            }
                            "Int", "Int!" -> {
                                funSpec.returns(Int::class)
                            }
                            "Float", "Float!" -> {
                                funSpec.returns(Float::class)
                            }
                            else -> {
                                funSpec.returns(
                                    ClassName(
                                        "$GENERATED_FILE_PACKAGE.data.models",
                                        returnType.split("!")[0].trim()
                                    )
                                )

                            }
                        }
                        typeSpec.addFunction(funSpec.build())
                    }
                }
            }
            println(method)
        }
        fileSpec.addType(typeSpec.build())
        fileSpec.build().writeTo(path)
    }

    private fun getGeneratedDirectory(): String {
        val directory = File("")
        return "${directory.absolutePath}/app/src/main/java/"
    }

    private fun getMetaDataFilePath(): String {
        val directory = File("")
        return "${directory.absolutePath}/codgen/schema.graphql"

    }

    private fun readFileDirectlyAsText(fileName: String): String =
        File(fileName).readText(Charsets.UTF_8)
}