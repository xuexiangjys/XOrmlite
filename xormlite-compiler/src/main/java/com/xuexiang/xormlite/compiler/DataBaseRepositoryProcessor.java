/*
 * Copyright (C) 2018 xuexiangjys(xuexiangjys@163.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.xuexiang.xormlite.compiler;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeVariableName;
import com.xuexiang.xormlite.annotation.DataBase;
import com.xuexiang.xormlite.enums.DataBaseType;
import com.xuexiang.xormlite.util.Consts;
import com.xuexiang.xormlite.util.Logger;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
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
 * 数据库仓库自动生成器
 *
 * @author xuexiang
 */
@AutoService(Processor.class)
public class DataBaseRepositoryProcessor extends AbstractProcessor {

    private static final ClassName DBServiceClassName = ClassName.get("com.xuexiang.xormlite.db", "DBService");
    private static final ClassName ContextClassName = ClassName.get("android.content", "Context");
    private static final ClassName IDatabaseClassName = ClassName.get("com.xuexiang.xormlite.db", "IDatabase");
    private static final ClassName IExternalDataBaseClassName = ClassName.get("com.xuexiang.xormlite.db", "IExternalDataBase");
    private static final ClassName DBLogClassName = ClassName.get("com.xuexiang.xormlite.logs", "DBLog");
    private static final TypeVariableName T = TypeVariableName.get("T");

    private Filer mFiler; //文件相关的辅助类
    private Types mTypes;
    private Elements mElements;
    private Logger mLogger; //日志相关的辅助类

    /**
     * 页面配置所在的包名
     */
    private static final String PACKAGE_NAME = "com.xuexiang.xormlite";

    /**
     * 数据库仓库的类名
     */
    private static final String DATABASE_REPOSITORY_CLASS_NAME = "DataBaseRepository";

    private TypeMirror mApplication = null;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        mFiler = processingEnv.getFiler();              // Generate class.
        mTypes = processingEnv.getTypeUtils();            // Get type utils.
        mElements = processingEnv.getElementUtils();      // Get class meta.
        mLogger = new Logger(processingEnv.getMessager());


        mApplication = mElements.getTypeElement(Consts.Application).asType();

        mLogger.info(">>> DataBaseRepositoryProcessor init. <<<");
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnvironment) {
        if (CollectionUtils.isNotEmpty(annotations)) {
            Set<? extends Element> dataBaseElements = roundEnvironment.getElementsAnnotatedWith(DataBase.class);
            try {
                mLogger.info(">>> Found DataBases, start... <<<");
                parseDataBases(dataBaseElements);

            } catch (Exception e) {
                mLogger.error(e);
            }
            return true;
        }
        return false;
    }

    /**
     * 解析数据库标注
     *
     * @param dataBaseElements
     */
    private void parseDataBases(Set<? extends Element> dataBaseElements) throws IOException {
        if (CollectionUtils.isNotEmpty(dataBaseElements)) {
            mLogger.info(">>> Found DataBases, size is " + dataBaseElements.size() + " <<<");

            //注释
            CodeBlock javaDoc = CodeBlock.builder()
                    .add("<p>这是DataBaseRepositoryProcessor自动生成的类，用以管理应用SqlLite数据库。</p>\n")
                    .add("<p><a href=\"mailto:xuexiangjys@163.com\">Contact me.</a></p>\n")
                    .add("\n")
                    .add("@author xuexiang \n")
                    .add("@date ").add(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())).add("\n")
                    .build();

             /*
               private Context mContext;
             */
            FieldSpec contextField = FieldSpec.builder(ContextClassName, "mContext")
                    .addModifiers(Modifier.PRIVATE)
                    .build();

             /*
               public void init(final Context context) {
                    mContext = context;
               }
             */
            MethodSpec initMethod = MethodSpec.methodBuilder("init")
                    .addModifiers(Modifier.PUBLIC)
                    .returns(void.class)
                    .addParameter(ContextClassName, "context", Modifier.FINAL)
                    .addStatement("mContext = context.getApplicationContext()")
                    .build();

            /*
              ``Map<String, DBService>```
             */
            ParameterizedTypeName DBPoolType = ParameterizedTypeName.get(
                    ClassName.get(Map.class),
                    ClassName.get(String.class),
                    DBServiceClassName
            );

             /*
              private Map<String, DBService> mDBPool = new HashMap<>();
             */
            FieldSpec DBPoolField = FieldSpec.builder(DBPoolType, "mDBPool")
                    .addModifiers(Modifier.PRIVATE)
                    .initializer("new $T<>()", HashMap.class)
                    .build();

            /*
              构造函数(保证单例）
              private DataBaseRepository() {}
             */
            MethodSpec constructorMethod = MethodSpec.constructorBuilder()
                    .addModifiers(Modifier.PRIVATE)
                    .build();

            TypeMirror tm;
            for (Element element : dataBaseElements) {
                tm = element.asType();
                if (mTypes.isSubtype(tm, mApplication)) {
                    mLogger.info(">>> Found Application DataBase: " + tm.toString() + " <<<");

                    DataBase dataBase = element.getAnnotation(DataBase.class);

                    ClassName dataBaseRepositoryClassName = ClassName.get(PACKAGE_NAME, upperFirstLetter(dataBase.name()) + DATABASE_REPOSITORY_CLASS_NAME);
                     /*
                       private static DataBaseRepository sInstance;
                     */
                    FieldSpec instanceField = FieldSpec.builder(dataBaseRepositoryClassName, "sInstance")
                            .addModifiers(Modifier.PRIVATE)
                            .addModifiers(Modifier.STATIC)
                            .build();

                    FieldSpec dbNameField = FieldSpec.builder(String.class, "DATABASE_NAME")
                            .addModifiers(Modifier.PUBLIC)
                            .addModifiers(Modifier.STATIC)
                            .addModifiers(Modifier.FINAL)
                            .initializer("$S", dataBase.name() + ".db")
                            .build();

                    FieldSpec versionField = FieldSpec.builder(int.class, "DATABASE_VERSION")
                            .addModifiers(Modifier.PUBLIC)
                            .addModifiers(Modifier.STATIC)
                            .addModifiers(Modifier.FINAL)
                            .initializer("$L", dataBase.version())
                            .build();

                    /*
                        private IDatabase mIDatabase;
                     */
                    FieldSpec iDatabaseField = FieldSpec.builder(dataBase.type().equals(DataBaseType.INTERNAL) ? IDatabaseClassName : IExternalDataBaseClassName, "mIDatabase")
                            .addModifiers(Modifier.PRIVATE)
                            .build();
                    /*
                        public DataBaseRepository setIDatabase(final IDatabase iDatabase) {
                            mIDatabase = context;
                        }
                     */
                    MethodSpec setIDatabaseMethod = MethodSpec.methodBuilder("setIDatabase")
                            .addModifiers(Modifier.PUBLIC)
                            .returns(dataBaseRepositoryClassName)
                            .addParameter(dataBase.type().equals(DataBaseType.INTERNAL) ? IDatabaseClassName : IExternalDataBaseClassName, "iDatabase", Modifier.FINAL)
                            .addStatement("mIDatabase = iDatabase")
                            .addStatement("return this")
                            .build();

                    MethodSpec getInstanceMethod = MethodSpec.methodBuilder("getInstance")
                            .addModifiers(Modifier.PUBLIC)
                            .addModifiers(Modifier.STATIC)
                            .returns(dataBaseRepositoryClassName)
                            .addCode("if (sInstance == null) {\n" +
                                    "    synchronized ($T.class) {\n" +
                                    "        if (sInstance == null) {\n" +
                                    "            sInstance = new $T();\n" +
                                    "        }\n" +
                                    "    }\n" +
                                    "}\n", dataBaseRepositoryClassName, dataBaseRepositoryClassName)
                            .addStatement("return sInstance")
                            .build();

                    MethodSpec getDataBaseMethod = MethodSpec.methodBuilder("getDataBase")
                            .addModifiers(Modifier.PUBLIC)
                            .addTypeVariable(T)
                            .returns(ParameterizedTypeName.get(DBServiceClassName, T))
                            .addParameter(ParameterizedTypeName.get(ClassName.get(Class.class), T), "clazz", Modifier.FINAL)
                            .addCode(
                                    "$T dbService = null;\n" +
                                    "if (mDBPool.containsKey(clazz.getCanonicalName())) {\n" +
                                    "   dbService = mDBPool.get(clazz.getCanonicalName());\n" +
                                    "} else {\n" +
                                    "   try {\n", ParameterizedTypeName.get(DBServiceClassName, T))
                            .addCode(
                                    getNewDBServiceCode(dataBase), ParameterizedTypeName.get(DBServiceClassName, T))
                            .addCode(
                                    "   } catch ($T e) {\n" +
                                    "       $T.e(e);\n" +
                                    "   }\n" +
                                    "   mDBPool.put(clazz.getCanonicalName(), dbService);\n" +
                                    "}\n", SQLException.class, DBLogClassName)
                            .addStatement("return dbService")
                            .build();

                    TypeSpec.Builder dataBaseRepositoryBuilder = TypeSpec.classBuilder(dataBaseRepositoryClassName)
                            .addJavadoc(javaDoc)
                            .addModifiers(Modifier.PUBLIC)
                            .addModifiers(Modifier.FINAL)
                            .addMethod(constructorMethod)
                            .addField(contextField)
                            .addMethod(initMethod)
                            .addField(instanceField)
                            .addMethod(getInstanceMethod)
                            .addField(dbNameField)
                            .addField(versionField)
                            .addField(iDatabaseField)
                            .addMethod(setIDatabaseMethod)
                            .addField(DBPoolField)
                            .addMethod(getDataBaseMethod);
                    if (dataBase.type().equals(DataBaseType.EXTERNAL)) {
                        FieldSpec dbPathField = FieldSpec.builder(String.class, "DATABASE_PATH")
                                .addModifiers(Modifier.PUBLIC)
                                .addModifiers(Modifier.STATIC)
                                .addModifiers(Modifier.FINAL)
                                .initializer("$S", !StringUtils.isEmpty(dataBase.path()) ? getDirPath(dataBase.path()) : "/storage/emulated/0/Android/xormlite/databases/")
                                .build();
                        dataBaseRepositoryBuilder.addField(dbPathField);
                    }
                    JavaFile.builder(PACKAGE_NAME, dataBaseRepositoryBuilder.build()).build().writeTo(mFiler);
                }
            }
        }
    }

    private String getNewDBServiceCode(DataBase dataBase) {
        if (dataBase.type().equals(DataBaseType.INTERNAL)) {
            return "       dbService = new $T(mContext, clazz, DATABASE_NAME, DATABASE_VERSION, mIDatabase);\n";
        } else {
            return "       dbService = new $T(mContext, clazz, DATABASE_PATH, DATABASE_NAME, DATABASE_VERSION, mIDatabase);\n";
        }
    }

    /**
     * 获取文件目录的路径，自动补齐"/"
     *
     * @param dirPath 目录路径
     * @return 自动补齐"/"的目录路径
     */
    private String getDirPath(String dirPath) {
        if (StringUtils.isEmpty(dirPath)) return "";

        if (!dirPath.trim().endsWith(File.separator)) {
            dirPath = dirPath.trim() + File.separator;
        }
        return dirPath;
    }


    /**
     * @return 指定哪些注解应该被注解处理器注册
     */
    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> types = new LinkedHashSet<>();
        types.add(DataBase.class.getCanonicalName());
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
        if (StringUtils.isEmpty(s) || !Character.isLowerCase(s.charAt(0))) return s;
        return String.valueOf((char) (s.charAt(0) - 32)) + s.substring(1);
    }
}
