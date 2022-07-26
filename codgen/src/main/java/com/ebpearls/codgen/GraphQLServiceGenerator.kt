package com.ebpearls.codgen

import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import java.io.File
import java.io.IOException
import java.nio.file.Files
import kotlin.io.path.Path

object GraphQLServiceGenerator {

    private const val GENERATED_FILE_PACKAGE = "com.nando.codegen.generated.graphql"
    private val graphqlPath = Path(getGraphQlDirectory())
    val flow = ClassName("kotlinx.coroutines.flow", "Flow")
    val smallFlow = ClassName("kotlinx.coroutines.flow", "flow")
    val coroutineDispatcher = ClassName("kotlinx.coroutines", "CoroutineDispatcher")
    val module = ClassName("dagger", "Module")


    private val path = Path(getGeneratedDirectory())

    @JvmStatic
    fun main(args: Array<String>) {
        val schema = readFileDirectlyAsText(
            getMetaDataFilePath()
        )
        generateDIModule()
        generateGraphQLForQuery(schema)
        generateGraphQLForMutation(schema)
        generateRepoMethodsForQuery(schema)
        generateRepoMethodsForMutation(schema)
        generateRepoMethodsForQueryImpl(schema)


    }

    private fun generateDIModule() {
        /*Generate DI Module*/
        val diFileSpecBuilder =
            FileSpec.builder("${GENERATED_FILE_PACKAGE}.di", "AppModule")
        val diTypeSpecBuilder = TypeSpec.objectBuilder("AppModule")

        diTypeSpecBuilder.addAnnotation(module)

        /*Install and singleton in Annotation*/
        val installInAnnotationSpec = AnnotationSpec.builder(RetrofitServiceGenerator.installInClassName)
            .addMember("%T::class", RetrofitServiceGenerator.singletonComponent)
            .build()
        diTypeSpecBuilder.addAnnotation(installInAnnotationSpec)

        val apiProviderFunction = FunSpec.builder("provideApi")
        apiProviderFunction.returns(RetrofitServiceGenerator.api)
        apiProviderFunction.addStatement(
            "return %T.Builder()\n" +
                    ".baseUrl(\"${apiMetaData.baseUrl}\")\n" +
                    ".addConverterFactory(%T.create())\n" +
                    ".build()\n" +
                    ".create(Api::class.java)\n",
            RetrofitServiceGenerator.retrofit, RetrofitServiceGenerator.gsonConverterFactory
        )
        apiProviderFunction.addAnnotation(RetrofitServiceGenerator.provides)
        apiProviderFunction.addAnnotation(RetrofitServiceGenerator.singleton)
        diTypeSpecBuilder.addFunction(RetrofitServiceGenerator.apiProviderFunction.build())


        apiMetaData.repos.forEach {
            val funSpec = FunSpec.builder("provide${it.name}")
            val returnClassName = ClassName("${RetrofitServiceGenerator.GENERATED_FILE_PACKAGE}.domain.repos", it.name)
            val repoImpl = ClassName("${RetrofitServiceGenerator.GENERATED_FILE_PACKAGE}.data.repos", "${it.name}Impl")
            funSpec.addParameter(
                ParameterSpec("api", RetrofitServiceGenerator.api)
            )
            funSpec.returns(returnClassName)
            funSpec.addAnnotation(RetrofitServiceGenerator.provides)
            funSpec.addAnnotation(RetrofitServiceGenerator.singleton)
            funSpec.addStatement("return %T(api)", repoImpl)
            diTypeSpecBuilder.addFunction(funSpec.build())
        }

        diFileSpecBuilder.addType(diTypeSpecBuilder.build())
        val diFile = diFileSpecBuilder.build()
        diFile.writeTo(RetrofitServiceGenerator.path)
    }

    private fun generateGraphQLForQuery(schema: String) {
        generateGraphQL(schema, "Query")
    }

    private fun generateGraphQLForMutation(schema: String) {
        generateGraphQL(schema, "Mutation")
    }

    private fun generateGraphQL(schema: String, type: String) {
        var query = schema.substringAfter("type $type {")
        query = query.substringBefore("}")
        val methods = query.split("\r?\n|\r".toRegex()).toTypedArray()
        methods.forEachIndexed { index, method ->
            if (method.isNotBlank()) {
                println(method)
                if (method.contains("(")) {
                    val returnType = method.substring(method.lastIndexOf(':') + 1).trim()
                    val returnString = getReturnString(schema, returnType)
                    val methodName = method.split("(")[0].trim()
                    val fileName = methodName.replaceFirstChar { it.uppercase() }
                    val parameters =
                        method.substringAfter("(").substringBefore(")").split("'")[0].split(",")
                    var parentParameterString = ""
                    if (parameters.isNotEmpty()) {
                        parentParameterString = "("
                        parameters.forEach {
                            val key = it.split(":")[0].trim()
                            val type = it.split(":")[1].trim()
                            parentParameterString += "\$${key}:$type,"
                        }
                        parentParameterString = "$parentParameterString)"
                    }
                    var childParameterString = ""
                    if (parameters.isNotEmpty()) {
                        childParameterString = "("
                        parameters.forEach {
                            val key = it.split(":")[0].trim()
                            childParameterString += "$key:$$key,"
                        }
                        childParameterString = "$childParameterString)"
                    }
                    val fileSpec =
                        FileSpec.scriptBuilder("${fileName}.graphql", "")
                    fileSpec.addCode(
                        "${type.lowercase()} $fileName$parentParameterString {\n" +
                                "$methodName$childParameterString\n" +
                                "${returnString}\n" +
                                "}"
                    )
                    fileSpec.build().writeTo(Path(getGraphQlDirectory()))
                } else {
                    val returnType = method.substring(method.lastIndexOf(':') + 1).trim()
                    val returnString = getReturnString(schema, returnType)
                    val methodName = method.split(":")[0].trim()
                    val fileName = methodName.replaceFirstChar { it.uppercase() }
                    val fileSpec =
                        FileSpec.scriptBuilder("${fileName}.graphql", "")
                    fileSpec.addCode(
                        "${type.lowercase()} $fileName {\n" +
                                "$methodName\n" +
                                "${returnString}\n" +
                                "}"
                    )
                    fileSpec.build().writeTo(Path(getGraphQlDirectory()))
                }
            }
        }
        convertFileExtension()
    }

    private fun getReturnString(schema: String, returnType: String): String {
        var returnString = "{\n"
        val searchQuery =
            "type ${returnType.replace("!", "").substringAfter("[").substringBefore("]")} {"
        var model = schema.substringAfter(searchQuery)
        model = model.substringBefore("}")
        val parameters = model.split("\r?\n|\r".toRegex()).toTypedArray()
        parameters.forEach {
            if (it.isNotBlank()) {
                val paraMeter = it.trim()
                val key = paraMeter.split(":")[0].trim()
                val type = paraMeter.split(":")[1].trim()
                println("::::$type::::")
                if (type.isPrimptive() || type.isListOfPrimptive()) {
                    returnString += "$key\n"
                } else {
                    returnString += "$key${getReturnString(schema, type)}"
                }
            }
        }
        returnString = "$returnString}\n"
        return returnString
    }

    private fun convertFileExtension() {
        Files.walk(Path(getGraphQlDirectory())).use { paths ->
            paths.filter {
                Files.isRegularFile(it)
            }.forEach {
                try {
                    val src = File(it.toUri())
                    if (src.name.endsWith(".kts")) {
                        var dest = File(
                            getGraphQlDirectory(),
                            src.name.substring(0, src.name.lastIndexOf("."))
                        )
                        if (dest.exists()) {
                            dest.delete()
                            dest = File(
                                getGraphQlDirectory(),
                                src.name.substring(0, src.name.lastIndexOf("."))
                            )
                        }
                        val success = src.renameTo(dest)
                        if (success) {
                            println("Renaming succeeded")
                        }
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
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
            FileSpec.builder("$GENERATED_FILE_PACKAGE.domain.repos.query", "Repository")
        val typeSpec = TypeSpec.interfaceBuilder("Repository")
        methods.forEachIndexed { index, method ->
            if (index != 0) {
                if (method.contains("(")) {
                    val returnType = method.substring(method.lastIndexOf(':') + 1)
                    println(returnType)
                    val methodName = method.split("(")[0].trim()
                    val funSpec = FunSpec.builder(methodName)
                        .addModifiers(KModifier.ABSTRACT)
                    val parameters =
                        method.substringAfter("(").substringBefore(")").split("'")[0].split(",")
                    if (parameters.isNotEmpty()) {
                        parameters.forEach {
                            val key = it.split(":")[0].trim()
                            when (val type = it.split(":")[1].trim()) {
                                "String", "String!" -> {
                                    val parameterSpec = ParameterSpec(
                                        key,
                                        String::class.asClassName()
                                            .copy(nullable = type.endsWith("!"))
                                    )
                                    funSpec.addParameter(parameterSpec)
                                }
                                "Int", "Int!" -> {
                                    val parameterSpec = ParameterSpec(
                                        key,
                                        Int::class.asClassName()
                                            .copy(nullable = type.endsWith("!"))
                                    )
                                    funSpec.addParameter(parameterSpec)
                                }
                                "Float", "Float!" -> {
                                    val parameterSpec = ParameterSpec(
                                        key,
                                        Float::class.asClassName()
                                            .copy(nullable = type.endsWith("!"))
                                    )
                                    funSpec.addParameter(parameterSpec)
                                }
                                "Double", "Double!" -> {
                                    val parameterSpec = ParameterSpec(
                                        key,
                                        Double::class.asClassName()
                                            .copy(nullable = type.endsWith("!"))
                                    )
                                    funSpec.addParameter(parameterSpec)
                                }
                                "Long", "Long!" -> {
                                    val parameterSpec = ParameterSpec(
                                        key,
                                        Long::class.asClassName()
                                            .copy(nullable = type.endsWith("!"))
                                    )
                                    funSpec.addParameter(parameterSpec)
                                }
                                "Boolean", "Boolean!" -> {
                                    val parameterSpec = ParameterSpec(
                                        key,
                                        Boolean::class.asClassName()
                                            .copy(nullable = type.endsWith("!"))
                                    )
                                    funSpec.addParameter(parameterSpec)
                                }
                                else -> {
                                    val parameterClass = ClassName("type", type.replace("!", ""))
                                        .copy(nullable = type.endsWith("!"))
                                    val parameterSpec = ParameterSpec(key, parameterClass)
                                    funSpec.addParameter(parameterSpec)
                                }
                            }
                        }
                    }
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
                        "Double", "Double!" -> {
                            funSpec.returns(String::class)
                        }
                        "Long", "Long!" -> {
                            funSpec.returns(Int::class)
                        }
                        "Boolean", "Boolean!" -> {
                            funSpec.returns(Float::class)
                        }
                        else -> {
                            funSpec.returns(
                                flow.parameterizedBy(
                                    ClassName(
                                        "",
                                        "${methodName.replaceFirstChar { it.uppercaseChar() }}Query.Data"
                                    )
                                )
                            )
                        }
                    }
                    typeSpec.addFunction(funSpec.build())
                } else {
                    val methodName = method.split(":")[0].trim()
                    if (method.isNotBlank()) {
                        val returnType = method.substring(method.lastIndexOf(':') + 1)
                        val funSpec = FunSpec.builder(methodName)
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
                                    flow.parameterizedBy(
                                        ClassName(
                                            "",
                                            "${methodName.replaceFirstChar { it.uppercaseChar() }}Query.Data"
                                        )
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

    private fun generateRepoMethodsForQueryImpl(schema: String) {
        var query = schema.substringAfter("type Query {")
        query = query.substringBefore("}")
        val methods = query.split("\r?\n|\r".toRegex()).toTypedArray()
        val fileSpec =
            FileSpec.builder("$GENERATED_FILE_PACKAGE.data.repos.query", "RepositoryImpl")
        val typeSpec = TypeSpec.classBuilder("RepositoryImpl")
        typeSpec.addSuperinterface(
            ClassName(
                "$GENERATED_FILE_PACKAGE.domain.repos.query",
                "Repository"
            )
        )
        val constructorBuilder = FunSpec.constructorBuilder()
            .addParameter(ParameterSpec("ioDispatcher", coroutineDispatcher))
        typeSpec.primaryConstructor(constructorBuilder.build())
        typeSpec.addProperty(
            PropertySpec.builder("ioDispatcher", coroutineDispatcher).initializer("ioDispatcher")
                .build()
        )
        methods.forEachIndexed { index, method ->
            if (index != 0) {
                if (method.contains("(")) {
                    val returnType = method.substring(method.lastIndexOf(':') + 1)
                    println(returnType)
                    val methodName = method.split("(")[0].trim()
                    val funSpec = FunSpec.builder(methodName)
                        .addModifiers(KModifier.OVERRIDE)
                    val parameters =
                        method.substringAfter("(").substringBefore(")").split("'")[0].split(",")
                    if (parameters.isNotEmpty()) {
                        parameters.forEach {
                            val key = it.split(":")[0].trim()
                            when (val type = it.split(":")[1].trim()) {
                                "String", "String!" -> {
                                    val parameterSpec = ParameterSpec(
                                        key,
                                        String::class.asClassName()
                                            .copy(nullable = type.endsWith("!"))
                                    )
                                    funSpec.addParameter(parameterSpec)
                                }
                                "Int", "Int!" -> {
                                    val parameterSpec = ParameterSpec(
                                        key,
                                        Int::class.asClassName()
                                            .copy(nullable = type.endsWith("!"))
                                    )
                                    funSpec.addParameter(parameterSpec)
                                }
                                "Float", "Float!" -> {
                                    val parameterSpec = ParameterSpec(
                                        key,
                                        Float::class.asClassName()
                                            .copy(nullable = type.endsWith("!"))
                                    )
                                    funSpec.addParameter(parameterSpec)
                                }
                                "Double", "Double!" -> {
                                    val parameterSpec = ParameterSpec(
                                        key,
                                        Double::class.asClassName()
                                            .copy(nullable = type.endsWith("!"))
                                    )
                                    funSpec.addParameter(parameterSpec)
                                }
                                "Long", "Long!" -> {
                                    val parameterSpec = ParameterSpec(
                                        key,
                                        Long::class.asClassName()
                                            .copy(nullable = type.endsWith("!"))
                                    )
                                    funSpec.addParameter(parameterSpec)
                                }
                                "Boolean", "Boolean!" -> {
                                    val parameterSpec = ParameterSpec(
                                        key,
                                        Boolean::class.asClassName()
                                            .copy(nullable = type.endsWith("!"))
                                    )
                                    funSpec.addParameter(parameterSpec)
                                }
                                else -> {
                                    val parameterClass = ClassName("type", type.replace("!", ""))
                                        .copy(nullable = type.endsWith("!"))
                                    val parameterSpec = ParameterSpec(key, parameterClass)
                                    funSpec.addParameter(parameterSpec)
                                }
                            }
                        }
                    }
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
                        "Double", "Double!" -> {
                            funSpec.returns(String::class)
                        }
                        "Long", "Long!" -> {
                            funSpec.returns(Int::class)
                        }
                        "Boolean", "Boolean!" -> {
                            funSpec.returns(Float::class)
                        }
                        else -> {
                            funSpec.returns(
                                flow.parameterizedBy(
                                    ClassName(
                                        "",
                                        "${methodName.replaceFirstChar { it.uppercaseChar() }}Query.Data"
                                    )
                                )
                            )
                        }
                    }
                    funSpec.addStatement(
                        "return %T{\n" +
                                "}.flowOn(ioDispatcher)", smallFlow
                    )
                    typeSpec.addFunction(funSpec.build())
                } else {
                    val methodName = method.split(":")[0].trim()
                    if (method.isNotBlank()) {
                        val returnType = method.substring(method.lastIndexOf(':') + 1)
                        val funSpec = FunSpec.builder(methodName)
                            .addModifiers(KModifier.OVERRIDE)
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
                                    flow.parameterizedBy(
                                        ClassName(
                                            "",
                                            "${methodName.replaceFirstChar { it.uppercaseChar() }}Query.Data"
                                        )
                                    )
                                )

                            }
                        }
                        funSpec.addStatement(
                            "return %T{\n" +
                                    "}.flowOn(ioDispatcher)", smallFlow
                        )
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
            FileSpec.builder("$GENERATED_FILE_PACKAGE.domain.repos.mutation", "Repository")
        val typeSpec = TypeSpec.interfaceBuilder("Repository")
        methods.forEachIndexed { index, method ->
            if (index != 0) {
                if (method.contains("(")) {
                    val returnType = method.substring(method.lastIndexOf(':') + 1)
                    println(returnType)
                    val methodName = method.split("(")[0].trim()
                    val funSpec = FunSpec.builder(methodName)
                        .addModifiers(KModifier.ABSTRACT)
                    val parameters =
                        method.substringAfter("(").substringBefore(")").split("'")[0].split(",")
                    if (parameters.isNotEmpty()) {
                        parameters.forEach {
                            val key = it.split(":")[0].trim()
                            when (val type = it.split(":")[1].trim()) {
                                "String", "String!" -> {
                                    val parameterSpec = ParameterSpec(
                                        key,
                                        String::class.asClassName()
                                            .copy(nullable = type.endsWith("!"))
                                    )
                                    funSpec.addParameter(parameterSpec)
                                }
                                "Int", "Int!" -> {
                                    val parameterSpec = ParameterSpec(
                                        key,
                                        Int::class.asClassName()
                                            .copy(nullable = type.endsWith("!"))
                                    )
                                    funSpec.addParameter(parameterSpec)
                                }
                                "Float", "Float!" -> {
                                    val parameterSpec = ParameterSpec(
                                        key,
                                        Float::class.asClassName()
                                            .copy(nullable = type.endsWith("!"))
                                    )
                                    funSpec.addParameter(parameterSpec)
                                }
                                "Double", "Double!" -> {
                                    val parameterSpec = ParameterSpec(
                                        key,
                                        Double::class.asClassName()
                                            .copy(nullable = type.endsWith("!"))
                                    )
                                    funSpec.addParameter(parameterSpec)
                                }
                                "Long", "Long!" -> {
                                    val parameterSpec = ParameterSpec(
                                        key,
                                        Long::class.asClassName()
                                            .copy(nullable = type.endsWith("!"))
                                    )
                                    funSpec.addParameter(parameterSpec)
                                }
                                "Boolean", "Boolean!" -> {
                                    val parameterSpec = ParameterSpec(
                                        key,
                                        Boolean::class.asClassName()
                                            .copy(nullable = type.endsWith("!"))
                                    )
                                    funSpec.addParameter(parameterSpec)
                                }
                                else -> {
                                    val parameterClass = ClassName("type", type.replace("!", ""))
                                        .copy(nullable = type.endsWith("!"))
                                    val parameterSpec = ParameterSpec(key, parameterClass)
                                    funSpec.addParameter(parameterSpec)
                                }
                            }

                        }
                    }
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
                                flow.parameterizedBy(
                                    ClassName(
                                        "",
                                        "${methodName.replaceFirstChar { it.uppercaseChar() }}Mutation.Data"
                                    )
                                )
                            )

                        }
                    }
                    typeSpec.addFunction(funSpec.build())
                } else {
                    val methodName = method.split(":")[0].trim().split(":")[0].trim()
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
                                    flow.parameterizedBy(
                                        ClassName(
                                            "",
                                            "${methodName.replaceFirstChar { it.uppercaseChar() }}Mutation.Data"
                                        )
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

    private fun getGraphQlDirectory(): String {
        val directory = File("")
        return "${directory.absolutePath}/app/src/main/graphql/"
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

private fun String.isListOfPrimptive() = this.contains("[String]") ||
        this.contains("[Boolean]") ||
        this.contains("[Double]") ||
        this.contains("[Int]") ||
        this.contains("[Long]") ||
        this.contains("[Float]") ||
        this.contains("[String!]") ||
        this.contains("[Boolean!]") ||
        this.contains("[Double!]") ||
        this.contains("[Int!]") ||
        this.contains("[Long!]") ||
        this.contains("[Float!]") ||
        this.contains("[DateTime!]") ||
        this.contains("[DateTime]")

private fun String.isPrimptive() = this == ("String") ||
        this == "Boolean" ||
        this == "Double" ||
        this == "Int" ||
        this == ("Long") ||
        this == ("Float") ||
        this == ("String!") ||
        this == ("Boolean!") ||
        this == ("Double!") ||
        this == ("Int!") ||
        this == ("Long!") ||
        this == ("Float!") ||
        this == ("DateTime!") ||
        this == ("DateTime")