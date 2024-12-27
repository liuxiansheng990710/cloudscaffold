package com.example.mybatisplus.enhance.injector.instances;

/**
 * <p>
 * PGSQL数据库检测实现引擎
 * <p>
 *
 * @author : 21
 * @since : 2024/2/4 14:30
 */

public class PostgreSQLEngine implements DataBaseEngine {

    public static PostgreSQLEngine instance = new PostgreSQLEngine();

    private String sql = "SELECT A.ATTNAME AS %s FROM PG_CLASS AS C,PG_ATTRIBUTE AS A WHERE A.ATTRELID=?::REGCLASS AND A.ATTRELID= C.OID AND A.ATTNUM> 0 AND NOT A.ATTISDROPPED";

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
        return "\"";
    }

}
