package com.nando.codgen

object Constant {
    const val SAMPLE = "{\n" +
            "  \"baseUrl\": \"http://www.baseurl.com/api/v1/\",\n" +
            "  \"repos\": [\n" +
            "    {\n" +
            "      \"name\": \"UserRepository\",\n" +
            "      \"methods\": [\n" +
            "        {\n" +
            "          \"name\": \"AddUser\",\n" +
            "          \"authorized\": false,\n" +
            "          \"httpMethod\": 0,\n" +
            "          \"path\": \"/user/add\",\n" +
            "          \"parameter\": [\n" +
            "            {\n" +
            "              \"name\": \"user\",\n" +
            "              \"type\": {\n" +
            "                \"typeId\": 0,\n" +
            "                \"typeName\": \"AddUserRequestModel\",\n" +
            "                \"nullable\": false\n" +
            "              }\n" +
            "            }\n" +
            "          ],\n" +
            "          \"returnType\": {\n" +
            "            \"typeId\": 0,\n" +
            "            \"typeName\": \"AddUserResponseModel\"\n" +
            "          }\n" +
            "        }\n" +
            "      ]\n" +
            "    }\n" +
            "  ],\n" +
            "  \"models\": [\n" +
            "    {\n" +
            "      \"name\": \"AddUserRequestModel\",\n" +
            "      \"properties\": [\n" +
            "        {\n" +
            "          \"name\": \"fullName\",\n" +
            "          \"type\": {\n" +
            "            \"typeId\": 3,\n" +
            "            \"nullable\": false\n" +
            "          }\n" +
            "        },\n" +
            "        {\n" +
            "          \"name\": \"email\",\n" +
            "          \"type\": {\n" +
            "            \"typeId\": 3,\n" +
            "            \"nullable\": false\n" +
            "          }\n" +
            "        },\n" +
            "        {\n" +
            "          \"name\": \"contact\",\n" +
            "          \"type\": {\n" +
            "            \"typeId\": 1,\n" +
            "            \"of\": {\n" +
            "              \"typeId\": 0,\n" +
            "              \"name\": \"ContactNumber\"\n" +
            "            },\n" +
            "            \"nullable\": false\n" +
            "          }\n" +
            "        },\n" +
            "        {\n" +
            "          \"name\": \"age\",\n" +
            "          \"type\": {\n" +
            "            \"typeId\": 3,\n" +
            "            \"formatId\": 0,\n" +
            "            \"nullable\": true\n" +
            "          }\n" +
            "        },\n" +
            "        {\n" +
            "          \"name\": \"address\",\n" +
            "          \"type\": {\n" +
            "            \"typeId\": 0,\n" +
            "            \"name\": \"Address\",\n" +
            "            \"nullable\": false\n" +
            "          }\n" +
            "        },\n" +
            "        {\n" +
            "          \"name\": \"isSuperUser\",\n" +
            "          \"type\": {\n" +
            "            \"typeId\": 4,\n" +
            "            \"nullable\": false\n" +
            "          }\n" +
            "        },\n" +
            "        {\n" +
            "          \"name\": \"excellencyLevel\",\n" +
            "          \"type\": {\n" +
            "            \"typeId\": 1,\n" +
            "            \"name\": \"ExcellencyLevel\",\n" +
            "            \"nullable\": false\n" +
            "          }\n" +
            "        },\n" +
            "        {\n" +
            "          \"name\": \"interests\",\n" +
            "          \"type\": {\n" +
            "            \"typeId\": 2,\n" +
            "            \"of\": {\n" +
            "              \"typeId\": 3\n" +
            "            },\n" +
            "            \"nullable\": false\n" +
            "          }\n" +
            "        }\n" +
            "      ]\n" +
            "    },\n" +
            "    {\n" +
            "      \"name\": \"ContactNumber\",\n" +
            "      \"properties\": [\n" +
            "        {\n" +
            "          \"name\": \"type\",\n" +
            "          \"type\": {\n" +
            "            \"typeId\": 3,\n" +
            "            \"nullable\": false\n" +
            "          }\n" +
            "        },\n" +
            "        {\n" +
            "          \"name\": \"number\",\n" +
            "          \"type\": {\n" +
            "            \"typeId\": 3,\n" +
            "            \"nullable\": false\n" +
            "          }\n" +
            "        }\n" +
            "      ]\n" +
            "    },\n" +
            "    {\n" +
            "      \"name\": \"Address\",\n" +
            "      \"properties\": [\n" +
            "        {\n" +
            "          \"name\": \"displayName\",\n" +
            "          \"type\": {\n" +
            "            \"typeId\": 3,\n" +
            "            \"nullable\": false\n" +
            "          }\n" +
            "        },\n" +
            "        {\n" +
            "          \"name\": \"latitude\",\n" +
            "          \"type\": {\n" +
            "            \"typeId\": 5,\n" +
            "            \"formatId\": 3,\n" +
            "            \"nullable\": false\n" +
            "          }\n" +
            "        },\n" +
            "        {\n" +
            "          \"name\": \"longitude\",\n" +
            "          \"type\": {\n" +
            "            \"typeId\": 5,\n" +
            "            \"formatId\": 3,\n" +
            "            \"nullable\": false\n" +
            "          }\n" +
            "        }\n" +
            "      ]\n" +
            "    }\n" +
            "  ],\n" +
            "  \"enums\": [\n" +
            "    {\n" +
            "      \"name\": \"ExcellencyLevel\",\n" +
            "      \"properties\": [\n" +
            "        {\n" +
            "          \"name\": \"Amateur\",\n" +
            "          \"value\": 0\n" +
            "        },\n" +
            "        {\n" +
            "          \"name\": \"Intermediate\",\n" +
            "          \"value\": 1\n" +
            "        },\n" +
            "        {\n" +
            "          \"name\": \"Excellent\",\n" +
            "          \"value\": 2\n" +
            "        }\n" +
            "      ]\n" +
            "    }\n" +
            "  ]\n" +
            "}"
}