package com.nando.codgen

import com.google.gson.Gson
import com.nando.codgen.models.ApiMetaData
import com.nando.codgen.models.Type
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import java.io.File
import kotlin.io.path.Path

object ServiceGenerator {

    private const val GENERATED_FILE_PACKAGE = "com.nando.codegen.generated"

    @JvmStatic
    fun main(args: Array<String>) {

        val apiMetaData = Gson().fromJson(
            readFileDirectlyAsText(getMetaDataFilePath()),
            ApiMetaData::class.java
        )

        /*Generate Repositories*/

        apiMetaData.repos.forEach { repo ->
            val fileSpecBuilder = FileSpec.builder("$GENERATED_FILE_PACKAGE.repos", repo.name)
            val typeSpecBuilder = TypeSpec.interfaceBuilder(repo.name)
            repo.methods.forEach { method ->
                val className =
                    ClassName("$GENERATED_FILE_PACKAGE.models", method.returnType.typeName)
                val funSecBuilder = FunSpec.builder(method.name)
                    .addModifiers(KModifier.ABSTRACT)
                    .returns(
                        ClassName("kotlinx.coroutines.flow", "Flow").parameterizedBy(className)
                    )
                method.parameter.forEach {
                    funSecBuilder.addParameter(
                        it.name,
                        when (it.type.typeId) {
                            MetaType.BOOLEAN -> {
                                Boolean::class
                            }
                            else -> {
                                String::class
                            }
                        }

                    )
                }
                val funSpec = funSecBuilder.build()
                typeSpecBuilder.addFunction(funSpec)
            }
            fileSpecBuilder.addType(typeSpecBuilder.build())
            val file = fileSpecBuilder.build()
            val path = Path(getGeneratedDirectory())
            file.writeTo(path)
        }

        /*Generate Enums*/
        apiMetaData.enums.forEach { enum ->
            val fileSpecBuilder = FileSpec.builder("$GENERATED_FILE_PACKAGE.enums", enum.name)
            val typeSpecBuilder = TypeSpec.enumBuilder(enum.name)
            enum.properties.forEach {
                typeSpecBuilder.addEnumConstant(it.name)
            }
            fileSpecBuilder.addType(typeSpec = typeSpecBuilder.build())
            val file = fileSpecBuilder.build()
            val path = Path(getGeneratedDirectory())
            file.writeTo(path)
        }

        /*GenerateModel*/
        apiMetaData.models.forEach { model ->
            val fileSpecBuilder = FileSpec.builder("$GENERATED_FILE_PACKAGE.models", model.name)
            val typeSpecBuilder =
                TypeSpec.classBuilder(model.name).addModifiers(KModifier.DATA)
            val constructorBuilder = FunSpec.constructorBuilder()
            model.properties.forEach {
                when (it.type.typeId) {
                    MetaType.LIST -> {
                        if (it.type.of!!.typeId == MetaType.MODEL) {
                            val of = ClassName(
                                "$GENERATED_FILE_PACKAGE.models",
                                it.type.of.name
                            )
                            constructorBuilder.addParameter(
                                it.name,
                                LIST.parameterizedBy(of).copy(nullable = it.type.nullable)
                            )
                        } else {
                            val of = ClassName(
                                "",
                                getOfType(it.type.of).simpleName!!
                            )
                            constructorBuilder.addParameter(
                                it.name,
                                LIST.parameterizedBy(of).copy(it.type.nullable)
                            )
                        }
                    }
                    MetaType.MODEL -> {
                        val className = ClassName(
                            "$GENERATED_FILE_PACKAGE.models", it.type.name
                        )
                        constructorBuilder.addParameter(
                            it.name,
                            className.copy(nullable = it.type.nullable)
                        )
                    }
                    MetaType.ENUM -> {
                        val className = ClassName(
                            "$GENERATED_FILE_PACKAGE.enums", it.type.name
                        )
                        constructorBuilder.addParameter(
                            it.name,
                            className.copy(nullable = it.type.nullable)
                        )
                    }
                    MetaType.STRING -> {
                        constructorBuilder.addParameter(
                            it.name,
                            String::class.asClassName().copy(nullable = it.type.nullable)
                        )
                    }
                    MetaType.BOOLEAN -> {
                        constructorBuilder.addParameter(
                            it.name,
                            Boolean::class.asClassName().copy(nullable = it.type.nullable)
                        )
                    }
                    MetaType.NUMBER -> {
                        when (it.type.formatId) {
                            MetaNumberFormat.DOUBLE -> {
                                constructorBuilder.addParameter(
                                    it.name,
                                    Double::class.asClassName().copy(nullable = it.type.nullable)
                                )
                            }
                            MetaNumberFormat.LONG -> {
                                constructorBuilder.addParameter(
                                    it.name,
                                    Long::class.asClassName().copy(nullable = it.type.nullable)
                                )
                            }
                            MetaNumberFormat.FLOAT -> {
                                constructorBuilder.addParameter(
                                    it.name,
                                    Float::class.asClassName().copy(nullable = it.type.nullable)
                                )
                            }
                            else -> constructorBuilder.addParameter(
                                it.name,
                                Int::class.asClassName().copy(nullable = it.type.nullable)
                            )
                        }
                    }
                    else -> constructorBuilder.addParameter(
                        it.name,
                        Any::class.asClassName().copy(nullable = it.type.nullable)
                    )
                }

            }
            model.properties.forEach {
                when (it.type.typeId) {
                    MetaType.LIST -> {
                        if (it.type.of!!.typeId == MetaType.MODEL) {
                            val of = ClassName(
                                "$GENERATED_FILE_PACKAGE.models",
                                it.type.of.name
                            )
                            typeSpecBuilder.addProperty(
                                PropertySpec.builder(
                                    it.name,
                                    LIST.parameterizedBy(of).copy(nullable = it.type.nullable)
                                )
                                    .initializer(it.name)
                                    .build()
                            )
                        } else {
                            val of = ClassName(
                                "",
                                getOfType(it.type.of).simpleName!!
                            )
                            typeSpecBuilder.addProperty(
                                PropertySpec.builder(
                                    it.name,
                                    LIST.parameterizedBy(of).copy(it.type.nullable)
                                )
                                    .initializer(it.name)
                                    .build()
                            )
                        }
                    }
                    MetaType.MODEL -> {
                        val className = ClassName(
                            "$GENERATED_FILE_PACKAGE.models", it.type.name
                        )
                        typeSpecBuilder.addProperty(
                            PropertySpec.builder(it.name, className.copy(it.type.nullable))
                                .initializer(it.name)
                                .build()
                        )
                    }
                    MetaType.ENUM -> {
                        val className = ClassName(
                            "$GENERATED_FILE_PACKAGE.enums", it.type.name
                        )
                        typeSpecBuilder.addProperty(
                            PropertySpec.builder(
                                it.name,
                                className.copy(nullable = it.type.nullable)
                            )
                                .initializer(it.name)
                                .build()
                        )
                    }
                    MetaType.STRING -> {
                        typeSpecBuilder.addProperty(
                            PropertySpec.builder(
                                it.name,
                                String::class.asClassName().copy(nullable = it.type.nullable)
                            )
                                .initializer(it.name)
                                .build()
                        )
                    }
                    MetaType.BOOLEAN -> {
                        typeSpecBuilder.addProperty(
                            PropertySpec.builder(
                                it.name,
                                Boolean::class.asClassName().copy(nullable = it.type.nullable)
                            )
                                .initializer(it.name)
                                .build()
                        )
                    }
                    MetaType.NUMBER -> {
                        when (it.type.formatId) {
                            MetaNumberFormat.DOUBLE -> {
                                typeSpecBuilder.addProperty(
                                    PropertySpec.builder(
                                        it.name,
                                        Double::class.asClassName()
                                            .copy(nullable = it.type.nullable)
                                    ).initializer(it.name).build()
                                )
                            }
                            MetaNumberFormat.LONG -> {
                                typeSpecBuilder.addProperty(
                                    PropertySpec.builder(
                                        it.name,
                                        Long::class.asClassName().copy(nullable = it.type.nullable)
                                    ).initializer(it.name).build()
                                )
                            }
                            MetaNumberFormat.FLOAT -> {
                                typeSpecBuilder.addProperty(
                                    PropertySpec.builder(
                                        it.name,
                                        Float::class.asClassName().copy(nullable = it.type.nullable)
                                    ).initializer(it.name).build()
                                )
                            }
                            else -> {
                                typeSpecBuilder.addProperty(
                                    PropertySpec.builder(
                                        it.name,
                                        Int::class.asClassName().copy(nullable = it.type.nullable)
                                    ).initializer(it.name).build()
                                )
                            }
                        }
                    }
                    else -> {
                        typeSpecBuilder.addProperty(
                            PropertySpec.builder(
                                it.name,
                                Any::class.asClassName().copy(nullable = it.type.nullable)
                            )
                                .initializer(it.name)
                                .build()
                        )
                    }
                }

            }
            typeSpecBuilder.primaryConstructor(constructorBuilder.build())
            fileSpecBuilder.addType(typeSpecBuilder.build())
            fileSpecBuilder.suppressWarningTypes("RedundantVisibilityModifier")
            val file = fileSpecBuilder.build()
            val path = Path(getGeneratedDirectory())
            file.writeTo(path)
        }

    }


    private fun getOfType(type: Type) = when (type.typeId) {
        MetaType.STRING -> {
            String::class
        }
        MetaType.BOOLEAN -> {
            Boolean::class
        }
        MetaType.NUMBER -> {
            when (type.formatId) {
                MetaNumberFormat.DOUBLE -> Double::class
                MetaNumberFormat.LONG -> Long::class
                MetaNumberFormat.FLOAT -> Float::class
                else -> Int::class
            }
        }
        else -> {
            Any::class
        }

    }

    private fun getMetaDataFilePath(): String {
        val directory = File("")
        return "${directory.absolutePath}/codgen/api_metadata.json"

    }

    private fun getGeneratedDirectory(): String {
        val directory = File("")
        return "${directory.absolutePath}/app/src/main/java/"
    }

    private fun readFileDirectlyAsText(fileName: String): String =
        File(fileName).readText(Charsets.UTF_8)

    internal fun FileSpec.Builder.suppressWarningTypes(vararg types: String) {
        if (types.isEmpty()) {
            return
        }

        val format = "%S,".repeat(types.count()).trimEnd(',')
        addAnnotation(
            AnnotationSpec.builder(ClassName("", "Suppress"))
                .addMember(format, *types)
                .build()
        )
    }

}
