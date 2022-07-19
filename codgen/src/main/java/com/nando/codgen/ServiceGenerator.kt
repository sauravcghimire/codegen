package com.nando.codgen

import com.google.gson.Gson
import com.nando.codgen.models.ApiMetaData
import com.nando.codgen.models.Type
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import java.io.File
import kotlin.io.path.Path

object ServiceGenerator {
        @JvmStatic
        fun main(args: Array<String>) {
                val apiMetaData = Gson().fromJson(
                        readFileDirectlyAsText(getMetaDataFilePath()),
                        ApiMetaData::class.java
                )

                /*Generate Enums*/
                apiMetaData.enums.forEach { enum ->
                        val fileSpecBuilder = FileSpec.builder("com.nando.codegen.enums", enum.name)
                        val typeSpecBuilder = TypeSpec.enumBuilder(enum.name)
                        enum.properties.forEach {
                                typeSpecBuilder.addEnumConstant(it.name)
                        }
                        fileSpecBuilder.addType(typeSpec = typeSpecBuilder.build())
                        val file = fileSpecBuilder.build()
                        val path = Path("${getGeneratedDirectory()}")
                        file.writeTo(path)
                }

                /*GenerateModel*/
                apiMetaData.models.forEach { model ->
                        val fileSpecBuilder = FileSpec.builder("com.nando.codegen.models", model.name)
                        val typeSpecBuilder =
                                TypeSpec.classBuilder(model.name).addModifiers(KModifier.DATA)
                        val constructorBuilder = FunSpec.constructorBuilder()
                        model.properties.forEach {
                                when (it.type.typeId) {
                                        MetaType.LIST -> {
                                                if (it.type.of.typeId == MetaType.MODEL) {
                                                        val of = ClassName(
                                                                "com.nando.codegen.models",
                                                                it.type.of.name
                                                        )
                                                        constructorBuilder.addParameter(
                                                                it.name,
                                                                LIST.parameterizedBy(of)
                                                        )
                                                } else {
                                                        val of = ClassName(
                                                                "",
                                                                getOfType(it.type.of).simpleName!!
                                                        )
                                                        constructorBuilder.addParameter(
                                                                it.name,
                                                                LIST.parameterizedBy(of)
                                                        )
                                                }
                                        }
                                        MetaType.MODEL -> {
                                                val className = ClassName(
                                                        "com.nando.codegen.models", it.type.name)
                                                constructorBuilder.addParameter(it.name, className)
                                        }
                                        MetaType.ENUM -> {
                                                val className = ClassName(
                                                        "com.nando.codegen.models", it.type.name)
                                                constructorBuilder.addParameter(it.name, className)
                                        }
                                        else -> {
                                                val type = getType(it.type)
                                                constructorBuilder.addParameter(
                                                        it.name, type
                                                )
                                        }
                                }

                        }
                        typeSpecBuilder.apply {
                                primaryConstructor(constructorBuilder.build())
                        }
                        fileSpecBuilder.addType(typeSpecBuilder.build())
                        val file = fileSpecBuilder.build()
                        val path = Path("${getGeneratedDirectory()}")
                        file.writeTo(path)
                }

        }

        private fun getType(type: Type) = when (type.typeId) {
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
                val directory = File("");
                return "${directory.absolutePath}/codgen/api_metadata.json"

        }

        private fun getGeneratedDirectory(): String {
                val directory = File("");
                return "${directory.absolutePath}/app/src/main/java/"
        }

        private fun readFileDirectlyAsText(fileName: String): String =
                File(fileName).readText(Charsets.UTF_8)

}
