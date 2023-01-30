package org.shanksit.japedu.admin;

import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.rules.DateType;

public class AutoGenerater {

    public static void main(String[] args) {
        FastAutoGenerator.create("jdbc:mysql://localhost:3306/japanese_education?useUnicode=true&characterEncoding=utf-8&serverTimezone=Asia/Shanghai", "root", "root")
                .globalConfig(builder -> {
                    builder.author("Kylin") // 设置作者
                            .enableSwagger() // 开启 swagger 模式
                            .dateType(DateType.ONLY_DATE)
                            .outputDir("H:\\sanks\\japanese-edu\\edu-admin\\src\\main\\java\\org\\shanksit\\japedu\\admin"); // 指定输出目录
                })
                .packageConfig(builder -> {
                    builder.parent("org.shanksit.japedu") // 设置父包名
                            .moduleName("admin") // 设置父包模块名
                            .mapper("dao.mapper")
                            .service("dao.repository")
                            .serviceImpl("dao.repository.impl")
                            .controller("rest")
                            .entity("entity")
                            .xml("dao.mapper");//设置xml文件的目录

                })
                .strategyConfig(builder -> {
                    builder.addInclude("t_user_download_history") // 设置需要生成的表名
                            .enableCapitalMode()
                            .addTablePrefix("t_"); // 设置过滤表前缀
                })
                //.templateEngine(new FreemarkerTemplateEngine()) // 使用Freemarker引擎模板，默认的是Velocity引擎模板
                .execute();

    }



//    public static void testGenerator() {
//        //1、全局配置
//        GlobalConfig config = globalConfig().build();
//        config.setActiveRecord(true);//开启AR模式
//        config.setAuthor("kylin");//设置作者
//        config.setDateType(DateType.ONLY_DATE);
//        // 获取当前项目根路径
//        String projectPath = System.getProperty("user.dir")+"\\src\\main\\java";
//        System.out.println(projectPath);
//        config.setOutputDir(projectPath);
//        config.setFileOverride(true); //是否覆盖之前生成的文件
//        config.setIdType(IdType.AUTO);//主键策略
//        config.setSwagger2(true);
//        config.setServiceName("I%sRepository");//生成的service接口名字首字母是否为I，这样设置就没有I
//        config.setServiceImplName("%sRepository");
//        config.setBaseResultMap(true);//生成resultMap
//        config.setBaseColumnList(true);//在xml中生成基础列
//        config.setSwagger2(true); //启用Swagger2
//
//        //2、数据源配置
//        DataSourceConfig dataSourceConfig = DATA_SOURCE_CONFIG.build();
//        //3、策略配置
//        StrategyConfig strategyConfig = new StrategyConfig.Builder()
//                .enableCapitalMode()//开启全局大写命名
//
//                .setLogicDeleteFieldName("del_flag")//设置逻辑删除字段,要和数据库中表对应
//                //.setDbColumnUnderline(true)//表名字段名使用下划线
//                .setNaming(NamingStrategy.underline_to_camel)//类型下划线转驼峰
//                .setColumnNaming(NamingStrategy.underline_to_camel)//字段名不改变
//                .setEntityTableFieldAnnotationEnable(true)
//                //.setSuperEntityColumns("id","created_time","updated_time","created_by","updated_by","useable","del_flag")
//                //.setSuperEntityClass(BaseEntity.class)
//                .setEntityLombokModel(true)//使用lombok
//                .setTablePrefix("t_")//表名前缀
//                //可以用同配符号:表示生成t_开头的对应库下所有表
//                .setExclude("t_test");
////                .setInclude("t_data_enhance")
////                .setInclude("t_dataset")
////                .setInclude("t_task_instance")
////                .setInclude("t_task_node")
////                .setInclude("t_mode")
////                .setInclude("t_mode_data")
//        ;
//        //逆向工程使用的表
//        //设置创建时间和更新时间自动填充策略
//        ArrayList<TableFill> tableFills = new ArrayList<TableFill>();
//        tableFills.add(new TableFill("created_by", FieldFill.INSERT));
//        tableFills.add(new TableFill("updated_by", FieldFill.INSERT_UPDATE));
//        tableFills.add(new TableFill("created_time", FieldFill.INSERT));
//        tableFills.add(new TableFill("updated_time", FieldFill.INSERT_UPDATE));
//        strategyConfig.setTableFillList(tableFills);
//        // 乐观锁策略
//        strategyConfig.setVersionFieldName("version");
//        strategyConfig.setRestControllerStyle(true);//采用restful 风格的api
//        strategyConfig.setControllerMappingHyphenStyle(true); // controller 请求地址采用下划线代替驼峰
//        //4、包名策略配置
//        PackageConfig packageConfig = new PackageConfig();
//        packageConfig.setParent("org.shanksit.japaneseedu")//设置包名的parent
//                .setMapper("dao.mapper")
//                .setService("dao.repository")
//                .setServiceImpl("dao.repository.impl")
//                .setController("rest")
//                .setEntity("entity")
//                .setXml("dao/mapper/xml");//设置xml文件的目录
//        //5、整合配置
//        AutoGenerator autoGenerator = new AutoGenerator();
//        autoGenerator.setGlobalConfig(config)
//                .setDataSource(dataSourceConfig)
//                .setStrategy(strategyConfig)
//                .setPackageInfo(packageConfig);
//        //6、执行
//        autoGenerator.execute();
//    }


}
