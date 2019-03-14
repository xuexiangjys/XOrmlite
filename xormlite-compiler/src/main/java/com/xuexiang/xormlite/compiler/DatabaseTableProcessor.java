package com.xuexiang.xormlite.compiler;

import com.google.auto.service.AutoService;
import com.j256.ormlite.table.DatabaseTable;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;
import com.xuexiang.xormlite.util.Logger;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

/**
 * 数据库表自动生成器
 *
 * @author xuexiang
 * @since 2019/3/14 下午10:37
 */
@AutoService(Processor.class)
public class DatabaseTableProcessor extends AbstractProcessor {

    /**
     * 文件相关的辅助类
     */
    private Filer mFiler;
    private Types mTypes;
    private Elements mElements;
    /**
     * 日志相关的辅助类
     */
    private Logger mLogger;
    /**
     * Module name, maybe its 'app' or others
     */
    private String moduleName = null;

    /**
     * 页面配置所在的包名
     */
    private static final String PACKAGE_NAME = "com.xuexiang.xormlite";

    /**
     * 数据库仓库的类名
     */
    private static final String DATABASE_TABLE_CLASS_NAME = "DataBaseTable";

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        mFiler = processingEnv.getFiler();
        mTypes = processingEnv.getTypeUtils();
        mElements = processingEnv.getElementUtils();
        mLogger = new Logger(processingEnv.getMessager());

        if (StringUtils.isNotEmpty(moduleName)) {
            moduleName = moduleName.replaceAll("[^0-9a-zA-Z_]+", "");

            mLogger.info("The user has configuration the module name, it was [" + moduleName + "]");
        } else {
            mLogger.info("These no module name, at 'build.gradle', like :\n" +
                    "javaCompileOptions {\n" +
                    "    annotationProcessorOptions {\n" +
                    "        arguments = [ moduleName : project.getName() ]\n" +
                    "    }\n" +
                    "}\n");
            //默认是app
            moduleName = "app";
        }

        mLogger.info(">>> DatabaseTableProcessor init. <<<");
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnvironment) {
        if (CollectionUtils.isNotEmpty(annotations)) {
            try {
                Set<? extends Element> databaseTableElements = roundEnvironment.getElementsAnnotatedWith(DatabaseTable.class);
                mLogger.info(">>> Found DatabaseTables, start... <<<");
                parseDatabaseTables(databaseTableElements);

            } catch (Exception e) {
                mLogger.error(e);
            }
            return true;
        }
        return false;
    }

    private void parseDatabaseTables(Set<? extends Element> databaseTableElements) throws IOException {
        if (CollectionUtils.isNotEmpty(databaseTableElements)) {
            mLogger.info(">>> Found DatabaseTables, size is " + databaseTableElements.size() + " <<<");

            ClassName databaseTableClassName = ClassName.get(PACKAGE_NAME, upperFirstLetter(moduleName) + DATABASE_TABLE_CLASS_NAME);
            TypeSpec.Builder databaseTableBuilder = TypeSpec.classBuilder(databaseTableClassName);

            /*
               private static DatabaseTable sInstance;
             */
            FieldSpec instanceField = FieldSpec.builder(databaseTableClassName, "sInstance")
                    .addModifiers(Modifier.PRIVATE)
                    .addModifiers(Modifier.STATIC)
                    .build();
             /*

              ``List<String>```
             */
            ParameterizedTypeName inputListTypeOfString = ParameterizedTypeName.get(
                    ClassName.get(List.class),
                    ClassName.get(String.class)
            );

             /*
               private List<String> mTables = new ArrayList<>();
             */
            FieldSpec tablesField = FieldSpec.builder(inputListTypeOfString, "mTables")
                    .addModifiers(Modifier.PRIVATE)
                    .build();

            //构造函数
            MethodSpec.Builder constructorBuilder = MethodSpec.constructorBuilder()
                    .addModifiers(Modifier.PRIVATE)
                    .addStatement("mTables = new $T<>()", ClassName.get(ArrayList.class));

            TypeMirror tm;
            for (Element element : databaseTableElements) {
                tm = element.asType();

                mLogger.info(">>> Found DatabaseTable: " + tm.toString() + " <<<");

                constructorBuilder.addStatement("mTables.add($S)",
                        tm.toString());
            }

            MethodSpec constructorMethod = constructorBuilder.build();

            MethodSpec instanceMethod = MethodSpec.methodBuilder("getInstance")
                    .addModifiers(Modifier.PUBLIC)
                    .addModifiers(Modifier.STATIC)
                    .returns(databaseTableClassName)
                    .addCode("if (sInstance == null) {\n" +
                            "    synchronized ($T.class) {\n" +
                            "        if (sInstance == null) {\n" +
                            "            sInstance = new $T();\n" +
                            "        }\n" +
                            "    }\n" +
                            "}\n", databaseTableClassName, databaseTableClassName)
                    .addStatement("return sInstance")
                    .build();

            MethodSpec getPagesMethod = MethodSpec.methodBuilder("getTables")
                    .addModifiers(Modifier.PUBLIC)
                    .addModifiers(Modifier.STATIC)
                    .returns(inputListTypeOfString)
                    .addStatement("return getInstance().mTables")
                    .build();

            CodeBlock javaDoc = CodeBlock.builder()
                    .add("<p>这是DatabaseTableProcessor自动生成的类，用以管理数据库表。</p>\n")
                    .add("<p><a href=\"mailto:xuexiangjys@163.com\">Contact me.</a></p>\n")
                    .add("\n")
                    .add("@author xuexiang \n")
                    .add("@date ").add(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())).add("\n")
                    .build();

            databaseTableBuilder
                    .addJavadoc(javaDoc)
                    .addModifiers(Modifier.PUBLIC)
                    .addField(tablesField)
                    .addMethod(constructorMethod)
                    .addField(instanceField)
                    .addMethod(instanceMethod)
                    .addMethod(getPagesMethod);
            JavaFile.builder(PACKAGE_NAME, databaseTableBuilder.build()).build().writeTo(mFiler);
        }
    }


    /**
     * @return 指定哪些注解应该被注解处理器注册
     */
    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> types = new LinkedHashSet<>();
        types.add(DatabaseTable.class.getCanonicalName());
        return types;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }


    /**
     * 首字母大写
     *
     * @param s 待转字符串
     * @return 首字母大写字符串
     */
    static String upperFirstLetter(final String s) {
        if (StringUtils.isEmpty(s) || !Character.isLowerCase(s.charAt(0))) {
            return s;
        }
        return String.valueOf((char) (s.charAt(0) - 32)) + s.substring(1);
    }
}
