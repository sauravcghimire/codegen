package com.ebpearls.codgen

import com.squareup.kotlinpoet.*
import java.io.File
import kotlin.io.path.Path

class Test {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            val fileSpecBuilder = FileSpec.builder("", "HelloWorld")
            val typeSpecBuilder =
                TypeSpec.classBuilder("Greeter").addModifiers(KModifier.DATA)
            typeSpecBuilder
                .addModifiers(KModifier.DATA)
            val constructorBuilder = FunSpec.constructorBuilder()
            for (i in 0..2) {
                constructorBuilder.addParameter("name$i", String::class)
            }
            for (i in 0..2) {
                typeSpecBuilder.addProperty(
                    PropertySpec.builder("name$i", String::class)
                        .initializer("name$i")
                        .build()
                )}

            typeSpecBuilder.primaryConstructor(constructorBuilder.build())
            fileSpecBuilder.addType(typeSpecBuilder.build())
            val file = fileSpecBuilder.build()
            val path = Path("${getGeneratedDirectory()}")
            file.writeTo(path)
        }

        private fun getGeneratedDirectory(): String {
            val directory = File("");
            return "${directory.absolutePath}/app/src/main/java/"
        }
    }


}