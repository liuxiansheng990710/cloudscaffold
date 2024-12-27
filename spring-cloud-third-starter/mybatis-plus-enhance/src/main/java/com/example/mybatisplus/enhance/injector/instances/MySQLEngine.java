package com.example.mybatisplus.enhance.injector.instances;

/**
 * <p>
 * mysql数据库检测引擎实现类
 * <p>
 *
 * @author : 21
 * @since : 2024/2/4 11:51
 */

public class MySQLEngine implements DataBaseEngine {

    public static MySQLEngine instance = new MySQLEngine();

    private String sql = "SELECT %s FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = ?";

    private String keyWords = "COLUMN_NAME";

    public void updateSQL(String sql) {
        this.sql = sql;
    }

    public void updateKeyWords(String keyWords) {
        this.keyWords = keyWords;
    }

    @Override
    public String getDataBaseColumnSQL() {
        return String.format(this.sql, this.keyWords);
    }

    @Override
    public String getSQLKeyWords() {
        return this.keyWords;
    }

    @Override
    public String getColumnMark() {
        return "`";
    }

}
