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
    private val path = Path(getGeneratedDirectory())

    private val inject = ClassName("javax.inject", "Inject")
    private val installInClassName: ClassName = ClassName("dagger.hilt", "InstallIn")
    private val singletonComponent = ClassName("dagger.hilt.components", "SingletonComponent")
    private val provides = ClassName("dagger", "Provides")
    private val singleton = ClassName("javax.inject", "Singleton")

    private val retrofit = ClassName("retrofit2", "Retrofit")
    private val gsonConverterFactory = ClassName("retrofit2.converter.gson", "GsonConverterFactory")
    private val post = ClassName("retrofit2.http", "POST")
    private val `get` = ClassName("retrofit2.http", "GET")
    private val put = ClassName("retrofit2.http", "PUT")
    private val delete = ClassName("retrofit2.http", "DELETE")
    private val head = ClassName("retrofit2.http", "HEAD")
    private val options = ClassName("retrofit2.http", "OPTIONS")

    val flow = ClassName("kotlinx.coroutines.flow", "flow")

    val api = ClassName("$GENERATED_FILE_PACKAGE.data", "Api")
    private val apiProviderFunction = FunSpec.builder("provideApi")


    @JvmStatic
    fun main(args: Array<String>) {

        val apiMetaData = Gson().fromJson(
            readFileDirectlyAsText(getMetaDataFilePath()),
            ApiMetaData::class.java
        )


        /*Generate DI Module*/
        val diFileSpecBuilder =
            FileSpec.builder("$GENERATED_FILE_PACKAGE.di", "AppModule")
        val diTypeSpecBuilder = TypeSpec.objectBuilder("AppModule")

        val moduleClassName = ClassName("dagger", "Module")
        diTypeSpecBuilder.addAnnotation(moduleClassName)

        /*Install and singleton in Annotation*/
        val installInAnnotationSpec = AnnotationSpec.builder(installInClassName)
            .addMember("%T::class", singletonComponent)
            .build()
        diTypeSpecBuilder.addAnnotation(installInAnnotationSpec)


        apiProviderFunction.returns(api)
        apiProviderFunction.addStatement(
            "return %T.Builder()\n" +
                    ".baseUrl(\"${apiMetaData.baseUrl}\")\n" +
                    ".addConverterFactory(%T.create())\n" +
                    ".build()\n" +
                    ".create(Api::class.java)\n",
            retrofit, gsonConverterFactory
        )
        apiProviderFunction.addAnnotation(provides)
        apiProviderFunction.addAnnotation(singleton)
        diTypeSpecBuilder.addFunction(apiProviderFunction.build())


        apiMetaData.repos.forEach {
            val funSpec = FunSpec.builder("provide${it.name}")
            val returnClassName = ClassName("$GENERATED_FILE_PACKAGE.domain.repos", it.name)
            val repoImpl = ClassName("$GENERATED_FILE_PACKAGE.data.repos", "${it.name}Impl")
            funSpec.addParameter(
                ParameterSpec("api", api)
            )
            funSpec.returns(returnClassName)
            funSpec.addAnnotation(provides)
            funSpec.addAnnotation(singleton)
            funSpec.addStatement("return %T(api)", repoImpl)
            diTypeSpecBuilder.addFunction(funSpec.build())
        }

        diFileSpecBuilder.addType(diTypeSpecBuilder.build())
        val diFile = diFileSpecBuilder.build()
        diFile.writeTo(path)

        /*Generate Retrofit Interface*/
        val fileSpecBuilder =
            FileSpec.builder("$GENERATED_FILE_PACKAGE.data", "Api")
        val typeSpecBuilder = TypeSpec.interfaceBuilder("Api")

        apiMetaData.repos.forEach { repo ->
            repo.methods.forEach { method ->
                val annotationClassName = when (method.httpMethod) {
                    MetaHttpMethod.POST -> {
                        post
                    }
                    MetaHttpMethod.GET -> {
                        get
                    }
                    MetaHttpMethod.PUT -> {
                        put
                    }
                    MetaHttpMethod.DELETE -> {
                        delete
                    }
                    MetaHttpMethod.HEAD -> {
                        head
                    }
                    else -> {
                        options
                    }
                }
                val annotationSpec = AnnotationSpec.builder(annotationClassName)
                    .addMember("\"${method.path}\"")
                    .build()
                val returnClassName =
                    ClassName("$GENERATED_FILE_PACKAGE.data.models", method.returnType.name)
                val funSpecBuilder = FunSpec.builder(method.name)
                    .addModifiers(KModifier.SUSPEND)
                    .addModifiers(KModifier.ABSTRACT)
                    .addAnnotation(annotationSpec)
                    .returns(returnClassName)
                method.parameter.forEach { parameter ->
                    val typeName = getTypeName(parameter.type)
                    funSpecBuilder.addParameter(
                        parameter.name, typeName
                    )
                }

                typeSpecBuilder.addFunction(funSpecBuilder.build())
            }
        }

        val file = fileSpecBuilder.addType(typeSpecBuilder.build()).build()
        file.writeTo(path)


        /*Generate Repositories*/

        apiMetaData.repos.forEach { repo ->
            val fileSpecBuilder =
                FileSpec.builder("$GENERATED_FILE_PACKAGE.domain.repos", repo.name)
            val typeSpecBuilder = TypeSpec.interfaceBuilder(repo.name)
            repo.methods.forEach { method ->
                val className =
                    ClassName("$GENERATED_FILE_PACKAGE.data.models", method.returnType.name)
                val funSecBuilder = FunSpec.builder(method.name)
                    .addModifiers(KModifier.ABSTRACT)
                    .addModifiers(KModifier.SUSPEND)
                    .returns(
                        className
                    )
                method.parameter.forEach {
                    val typeName = getTypeName(it.type)
                    funSecBuilder.addParameter(
                        it.name, typeName
                    )
                }
                val funSpec = funSecBuilder.build()
                typeSpecBuilder.addFunction(funSpec)
            }
            fileSpecBuilder.addType(typeSpecBuilder.build())
            val file = fileSpecBuilder.build()
            file.writeTo(path)
        }

        /*Generate Repositories Implementation*/

        apiMetaData.repos.forEach { repo ->
            val fileSpecBuilder =
                FileSpec.builder("$GENERATED_FILE_PACKAGE.data.repos", "${repo.name}Impl")
            val typeSpecBuilder = TypeSpec.classBuilder("${repo.name}Impl")
            typeSpecBuilder.addSuperinterface(
                ClassName(
                    "$GENERATED_FILE_PACKAGE.domain.repos",
                    repo.name
                )
            )
            val constructorBuilder = FunSpec.constructorBuilder()
                .addParameter(ParameterSpec("api", api))
                .addAnnotation(inject)
            typeSpecBuilder.primaryConstructor(constructorBuilder.build())
            typeSpecBuilder.addProperty(
                PropertySpec.builder(
                    "api", api
                ).initializer("api").build()
            )
            repo.methods.forEach { method ->
                val className =
                    ClassName("$GENERATED_FILE_PACKAGE.data.models", method.returnType.name)
                var parameterInString = ""
                method.parameter.forEach {
                    parameterInString = parameterInString + it.name + ","
                }
                val funSecBuilder = FunSpec.builder(method.name)
                    .returns(className)
                    .addModifiers(KModifier.SUSPEND)
                    .addStatement(
                        "return api.${method.name}($parameterInString)"
                    )
                    .addModifiers(KModifier.OVERRIDE)
                method.parameter.forEach {
                    val typeName = getTypeName(it.type)
                    funSecBuilder.addParameter(it.name, typeName)
                }
                val funSpec = funSecBuilder.build()
                typeSpecBuilder.addFunction(funSpec)
            }

            fileSpecBuilder.addType(typeSpecBuilder.build())
            val file = fileSpecBuilder.build()
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
            file.writeTo(path)
        }

        /*Generate Models*/
        apiMetaData.models.forEach { model ->
            val fileSpecBuilder =
                FileSpec.builder("$GENERATED_FILE_PACKAGE.data.models", model.name)
            val typeSpecBuilder =
                TypeSpec.classBuilder(model.name).addModifiers(KModifier.DATA)
            val constructorBuilder = FunSpec.constructorBuilder()
            model.properties.forEach {
                val typeName = getTypeName(it.type)
                constructorBuilder.addParameter(it.name, typeName)
            }
            model.properties.forEach {
                val typeName = getTypeName(it.type)
                typeSpecBuilder.addProperty(
                    PropertySpec.builder(
                        it.name, typeName
                    ).initializer(it.name).build()
                )

            }
            typeSpecBuilder.primaryConstructor(constructorBuilder.build())
            fileSpecBuilder.addType(typeSpecBuilder.build())
            fileSpecBuilder.suppressWarningTypes("RedundantVisibilityModifier")
            val file = fileSpecBuilder.build()
            file.writeTo(path)
        }

    }

    private fun getTypeName(type: Type): TypeName {
        return when (type.typeId) {
            MetaType.LIST -> {

                if (type.of!!.typeId == MetaType.MODEL) {
                    val of = ClassName(
                        "$GENERATED_FILE_PACKAGE.data.models",
                        type.of.name
                    )
                    LIST.parameterizedBy(of).copy(nullable = type.nullable)
                } else {
                    val of = ClassName(
                        "",
                        getOfType(type.of).simpleName
                    )
                    LIST.parameterizedBy(of).copy(nullable = type.nullable)

                }
            }
            MetaType.MODEL -> {
                val className = ClassName(
                    "$GENERATED_FILE_PACKAGE.data.models", type.name
                )
                className.copy(nullable = type.nullable)

            }
            MetaType.ENUM -> {
                val className = ClassName(
                    "$GENERATED_FILE_PACKAGE.enums", type.name
                )
                className.copy(nullable = type.nullable)

            }
            MetaType.STRING -> {
                String::class.asClassName().copy(nullable = type.nullable)

            }
            MetaType.BOOLEAN -> {
                Boolean::class.asClassName().copy(nullable = type.nullable)
            }
            MetaType.NUMBER -> {
                when (type.formatId) {
                    MetaNumberFormat.DOUBLE -> {
                        Double::class.asClassName()
                            .copy(nullable = type.nullable)

                    }
                    MetaNumberFormat.LONG -> {
                        Long::class.asClassName().copy(nullable = type.nullable)

                    }
                    MetaNumberFormat.FLOAT -> {
                        Float::class.asClassName().copy(nullable = type.nullable)
                    }
                    else ->
                        Int::class.asClassName().copy(nullable = type.nullable)

                }
            }
            else ->
                Any::class.asClassName().copy(nullable = type.nullable)

        }

    }


    private fun getOfType(type: Type): ClassName = when (type.typeId) {
        MetaType.LIST -> {
            getOfType(type)
        }
        MetaType.MODEL -> {
            ClassName(
                "$GENERATED_FILE_PACKAGE.data.models",
                type.name
            )
        }
        MetaType.STRING -> {
            String::class.asClassName()
        }
        MetaType.BOOLEAN -> {
            Boolean::class.asClassName()
        }
        MetaType.NUMBER -> {
            when (type.formatId) {
                MetaNumberFormat.DOUBLE -> Double::class.asClassName()
                MetaNumberFormat.LONG -> Long::class.asClassName()
                MetaNumberFormat.FLOAT -> Float::class.asClassName()
                else -> Int::class.asClassName()
            }
        }
        else -> {
            Any::class.asClassName()
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
