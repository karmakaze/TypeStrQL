package org.keithkim.typestrql.annotation.processor;

import com.google.common.base.CaseFormat;
import com.squareup.javapoet.*;
import org.keithkim.moja.util.Pair;
import org.keithkim.typestrql.annotation.Column;
import org.keithkim.typestrql.annotation.Table;

import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import java.util.LinkedHashMap;
import java.util.Map;

public class TableTableGenerator {
    private final ClassName entityClassName;
    private final TypeElement typeElement;
    private final Table tableAnnotation;
    private final Map<String, Pair<VariableElement, Column>> fields = new LinkedHashMap<>();

    public TableTableGenerator(String qualifiedClassName, TypeElement typeElement, Table tableAnnotation) {
        String packageName = "";
        String simpleName = qualifiedClassName;
        int i = qualifiedClassName.lastIndexOf('.');
        if (i >= 0) {
            packageName = qualifiedClassName.substring(0, i);
            simpleName = qualifiedClassName.substring(i + 1);
        }
        this.entityClassName = ClassName.get(packageName, simpleName);
        this.typeElement = typeElement;
        this.tableAnnotation = tableAnnotation;
    }

    public ProcessorError add(VariableElement element, Column columnAnnotation) {
        fields.put(element.getSimpleName().toString(), Pair.make(element, columnAnnotation));
        return null;
    }

    public TypeSpec generateCode(Processor.Context ctx) {
        String packageName = entityClassName.packageName();
        String simpleName = entityClassName.simpleName();

        String tableName = tableAnnotation.name();
        if (tableName == null || tableName.isEmpty()) {
            tableName = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, simpleName);
        }

        ClassName safeqlTableClassName = ClassName.get("org.keithkim.typestrql.schema", "Table");

        TypeName tableEntity = ParameterizedTypeName.get(safeqlTableClassName, entityClassName);
        TypeSpec.Builder tableClassBuilder = TypeSpec.classBuilder("Table")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .superclass(tableEntity);

        tableClassBuilder.addField(FieldSpec.builder(ClassName.bestGuess("Table"), "DEFAULT",
                        Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                .initializer("new Table($S, null)", tableName).build());

        tableClassBuilder.addMethod(MethodSpec.constructorBuilder().addModifiers(Modifier.PUBLIC)
                    .addParameter(String.class, "tableExpr")
                    .addParameter(String.class, "alias")
                    .addStatement("super($L.class, tableExpr, alias)", simpleName)
                .build());

        for (Map.Entry<String, Pair<VariableElement, Column>> me : fields.entrySet()) {
            String fieldName = me.getKey();
            Pair<VariableElement, Column> elementAnnotation = me.getValue();
            String colClassName = CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_CAMEL, fieldName);
            String columnName = CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, fieldName);

            String typeString = elementAnnotation.value1().asType().toString();
            ClassName typeParam;
            if (typeString.startsWith("java.")) {
                try {
                    typeParam = ClassName.get(Class.forName(typeString));
                } catch (ClassNotFoundException e) {
                    typeParam = ClassName.get(packageName, typeString);
                }
            } else {
                typeParam = ClassName.get(packageName, typeString);
            }

            ClassName safeqlColumnClassName = ClassName.get("org.keithkim.typestrql.schema", "Table.SqlColumn");
            TypeName columnTypeName = ParameterizedTypeName.get(safeqlColumnClassName, typeParam);

            TypeSpec columnClassType = TypeSpec.classBuilder(ClassName.get(packageName, colClassName))
                    .addModifiers(Modifier.PUBLIC)
                    .superclass(columnTypeName)
                    .addMethod(MethodSpec.constructorBuilder().addModifiers(Modifier.PUBLIC)
                            .addStatement("super($S)", columnName)
                            .build())
                    .addMethod(MethodSpec.constructorBuilder().addModifiers(Modifier.PUBLIC)
                            .addParameter(String.class, "alias")
                            .addStatement("super($S, alias)", columnName)
                            .build())
                    .build();
            tableClassBuilder.addType(columnClassType);

            ClassName fieldColClassName = ClassName.get("", colClassName);
            tableClassBuilder.addField(FieldSpec.builder(fieldColClassName,
                            fieldName+"Col", Modifier.PUBLIC, Modifier.FINAL)
                    .initializer("new $T()", fieldColClassName)
                    .build());

            tableClassBuilder.addMethod(MethodSpec.methodBuilder(fieldName +"Col")
                    .addModifiers(Modifier.PUBLIC).returns(fieldColClassName)
                    .addParameter(String.class, "alias")
                    .addStatement("return new $T(alias)", fieldColClassName).build());

//            entityClassBuilder.addField(FieldSpec.builder(fieldClassName,fieldName +"Col")
//                    .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
//                    .initializer("new $T(null)", fieldClassName).build());
//

//            public Id idCol(String alias) {
//                return new Id(alias);
//            }

//        public Name nameCol(String alias) {
//            return new Name(alias);
//        }

        }

        return tableClassBuilder.build();
    }
}
