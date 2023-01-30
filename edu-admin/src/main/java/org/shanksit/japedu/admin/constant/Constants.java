package org.shanksit.japedu.admin.constant;


import lombok.AllArgsConstructor;
import org.shanksit.japedu.admin.enums.BasicEnum;

public class Constants {

    /**
     * 查询单条数据，SQL结尾字符
     **/
    public static final String SQL_LIMIT_ONE = "limit 1";

    /**
     * 数据状态
     */
    @AllArgsConstructor
    public enum DelFlag implements BasicEnum<Integer, String> {
        NORMAL(0, "正常"),
        DELETE(1, "删除");


        //region 枚举字段
        Integer value;
        String text;

        @Override
        public Integer value() {
            return this.value;
        }

        @Override
        public String text() {
            return this.text;
        }
        //endregion

    }

    /**
     * 用户类型
     */
    @AllArgsConstructor
    public enum UserType implements BasicEnum<Integer, String> {
        ROOT(0, "超级管理员"),
        INSTITUTION(1, "机构用户"),
        TEACHER(10, "教师用户");


        //region 枚举字段
        Integer value;
        String text;

        @Override
        public Integer value() {
            return this.value;
        }

        @Override
        public String text() {
            return this.text;
        }
        //endregion

    }

    /**
     * 有没有子类
     */
    @AllArgsConstructor
    public enum HasChildrenTagType implements BasicEnum<Integer, String> {
        NO(0, "没有子类"),
        YES(1, "有子类");



        //region 枚举字段
        Integer value;
        String text;

        @Override
        public Integer value() {
            return this.value;
        }

        @Override
        public String text() {
            return this.text;
        }
        //endregion

    }
}
