package org.opencb.opencga.storage.hadoop.variant.index.phoenix;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.phoenix.schema.PTable;
import org.apache.phoenix.schema.types.PArrayDataType;
import org.apache.phoenix.schema.types.PDataType;
import org.apache.phoenix.schema.types.PhoenixArray;
import org.apache.phoenix.util.ColumnInfo;
import org.apache.phoenix.util.QueryUtil;
import org.apache.phoenix.util.SchemaUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created on 11/10/16.
 *
 * @author Jacobo Coll &lt;jacobo167@gmail.com&gt;
 */
public class PhoenixHelper {

    public static final String DEFAULT_TABLE_TYPE = "TABLE";
    private final Configuration conf;
    protected static Logger logger = LoggerFactory.getLogger(VariantPhoenixHelper.class);

    public PhoenixHelper(Configuration conf) {
        this.conf = conf;
    }

    public boolean execute(Connection con, String sql) throws SQLException {
        VariantPhoenixHelper.logger.debug(sql);
        try (Statement statement = con.createStatement()) {
            return statement.execute(sql);
        }
    }

    public String explain(Connection con, String sql) throws SQLException {
        if (!sql.startsWith("explain") && !sql.startsWith("EXPLAIN")) {
            sql = "EXPLAIN " + sql;
        }
        try (Statement statement = con.createStatement()) {
            ResultSet resultSet = statement.executeQuery(sql);
            return QueryUtil.getExplainPlan(resultSet);
        }
    }

    public String buildAlterAddColumn(String tableName, String column, String type) {
        return buildAlterAddColumn(tableName, column, type, true);
    }

    public String buildAlterAddColumn(String tableName, String column, String type, boolean ifNotExists) {
        return buildAlterAddColumn(tableName, column, type, ifNotExists, DEFAULT_TABLE_TYPE);
    }

    public String buildAlterTableAddColumn(String tableName, String column, String type, boolean ifNotExists) {
        return buildAlterAddColumn(tableName, column, type, ifNotExists, "TABLE");
    }

    public String buildAlterViewAddColumn(String tableName, String column, String type, boolean ifNotExists) {
        return buildAlterAddColumn(tableName, column, type, ifNotExists, "View");
    }

    private String buildAlterAddColumn(String tableName, String column, String type, boolean ifNotExists, String table) {
        return "ALTER " + table + " \"" + tableName + "\" ADD " + (ifNotExists ? "IF NOT EXISTS " : "") + "\"" + column + "\" " + type;
    }

    public String buildAlterAddColumns(String tableName, Collection<Column> columns, boolean ifNotExists) {
        StringBuilder sb = new StringBuilder();
        sb.append("ALTER ").append(DEFAULT_TABLE_TYPE).append(" \"").append(tableName)
                .append("\" ADD ").append(ifNotExists ? "IF NOT EXISTS " : "");
        Iterator<Column> iterator = columns.iterator();
        while (iterator.hasNext()) {
            Column column = iterator.next();
            sb.append("\"").append(column.column()).append("\" ").append(column.sqlType());
            if (iterator.hasNext()) {
                sb.append(", ");
            }
        }
        return sb.toString();
    }

    public void addMissingColumns(Connection con, String tableName, Collection<Column> annotColumns, boolean oneCall)
            throws SQLException {
        Set<String> columns = getColumns(con, tableName).stream().map(Column::column).collect(Collectors.toSet());
        List<Column> missingColumns = annotColumns.stream()
                .filter(column -> !columns.contains(column.column()))
                .collect(Collectors.toList());
        if (!missingColumns.isEmpty()) {
            logger.info("Adding missing columns: " + missingColumns);
            if (oneCall) {
                String sql = buildAlterAddColumns(tableName, missingColumns, true);
                VariantPhoenixHelper.logger.info(sql);
                execute(con, sql);
            } else {
                for (Column column : missingColumns) {
                    String sql = buildAlterAddColumn(tableName, column.column(), column.sqlType(), true);
                    VariantPhoenixHelper.logger.info(sql);
                    execute(con, sql);
                }
            }
        }
    }

    public Connection newJdbcConnection() throws SQLException, ClassNotFoundException {
        return newJdbcConnection(conf);
    }

    public Connection newJdbcConnection(Configuration conf) throws SQLException, ClassNotFoundException {
        return QueryUtil.getConnection(conf);
    }

    public static byte[] toBytes(Collection collection, PArrayDataType arrayType) {
        PDataType pDataType = PDataType.arrayBaseType(arrayType);
        Object[] elements = collection.toArray();
        PhoenixArray phoenixArray = new PhoenixArray(pDataType, elements);
        return arrayType.toBytes(phoenixArray);
    }

    public void createIndexes(Connection con, String tableName, List<PhoenixHelper.Index> indices, boolean async) throws SQLException {
        for (PhoenixHelper.Index index : indices) {
            String sql = createIndexSql(tableName, index, async);
            execute(con, sql);
        }
    }

    public void createLocalIndex(Connection con, String tableName, String indexName, List<String> columns, List<String> include)
            throws SQLException {
        String sql = PhoenixHelper.createIndexSql(PTable.IndexType.LOCAL, tableName, indexName, columns, include, false);
        execute(con, sql);
    }

    public void createGlobalIndex(Connection con, String indexName, String tableName, List<String> columns, List<String> include)
            throws SQLException {
        String sql = PhoenixHelper.createIndexSql(PTable.IndexType.GLOBAL, tableName, indexName, columns, include, false);
        execute(con, sql);
    }

    public static String createIndexSql(String tableName, Index index, boolean async) {
        return createIndexSql(index.indexType, tableName, index.indexName, index.columns, index.include, async);
    }

    public static String createIndexSql(PTable.IndexType type, String tableName, String indexName,
                                        List<String> columns, List<String> include, boolean async) {
        Objects.requireNonNull(indexName);
        Objects.requireNonNull(tableName);
        if (columns == null || columns.isEmpty()) {
            throw new IllegalArgumentException("Columns can not be empty");
        }
        StringBuilder sb = new StringBuilder();
        sb.append("CREATE");
        if (type.equals(PTable.IndexType.LOCAL)) {
            sb.append(" LOCAL");
        }
        sb.append(" INDEX IF NOT EXISTS ");

        sb.append(SchemaUtil.getEscapedArgument(indexName))
                .append(" ON ").append(SchemaUtil.getEscapedFullTableName(tableName)).append(" ( ");
        for (Iterator<String> iterator = columns.iterator(); iterator.hasNext();) {
            String column = iterator.next();
            sb.append(SchemaUtil.getEscapedFullColumnName(column));
            if (iterator.hasNext()) {
                sb.append(", ");
            }
        }
        sb.append(" )");
        if (include != null && !include.isEmpty()) {
            sb.append(" INCLUDE(");
            for (Iterator<String> iterator = include.iterator(); iterator.hasNext();) {
                String column = iterator.next();
                sb.append(SchemaUtil.getEscapedFullColumnName(column));
                if (iterator.hasNext()) {
                    sb.append(", ");
                }
            }
            sb.append(" )");
        }
        if (async) {
            sb.append(" ASYNC");
        }
        String sql = sb.toString();
        logger.info("Creating index: {}", sql);
        return sql;
    }

    public List<Column> getColumns(Connection con, String table) throws SQLException {
        String sql = "SELECT * FROM \"" + table + "\" LIMIT 0";
        VariantPhoenixHelper.logger.debug(sql);

        try (Statement statement = con.createStatement()) {
            ResultSet resultSet = statement.executeQuery(sql);
            ResultSetMetaData metaData = resultSet.getMetaData();
            List<Column> columns = new ArrayList<>(metaData.getColumnCount());
            // 1-based
            for (int i = 1; i <= metaData.getColumnCount(); i++) {
                columns.add(Column.build(metaData.getColumnName(i), PDataType.fromSqlTypeName(metaData.getColumnTypeName(i))));
            }
            return columns;
        }
    }


    public interface Column {
        String column();

        byte[] bytes();

        PDataType getPDataType();

        String sqlType();

        boolean nullable();

        static Column build(String column, PDataType pDataType) {
            return new ColumnImpl(column, pDataType, false);
        }

        static Column build(String column, PDataType pDataType, boolean nullable) {
            return new ColumnImpl(column, pDataType, nullable);
        }

        default ColumnInfo toColumnInfo() {
            return new ColumnInfo(column(), getPDataType().getSqlType());
        }
    }

    private static class ColumnImpl implements Column {

        private final String column;
        private final PDataType pDataType;
        private boolean nullable;

        ColumnImpl(String column, PDataType pDataType, boolean nullable) {
            this.bytes = Bytes.toBytes(column);
            this.column = column;
            this.pDataType = pDataType;
            this.nullable = nullable;
        }

        private byte[] bytes;

        @Override
        public String column() {
            return column;
        }

        @Override
        public byte[] bytes() {
            return bytes;
        }

        @Override
        public PDataType getPDataType() {
            return pDataType;
        }

        @Override
        public String sqlType() {
            return pDataType.getSqlTypeName();
        }

        @Override
        public boolean nullable() {
            return nullable;
        }

        @Override
        public String toString() {
            return column;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof ColumnImpl)) {
                return false;
            }

            ColumnImpl column1 = (ColumnImpl) o;

            if (column != null ? !column.equals(column1.column) : column1.column != null) {
                return false;
            }
            return pDataType != null ? pDataType.equals(column1.pDataType) : column1.pDataType == null;

        }

        @Override
        public int hashCode() {
            int result = column != null ? column.hashCode() : 0;
            result = 31 * result + (pDataType != null ? pDataType.hashCode() : 0);
            return result;
        }
    }

    public static class Index {
        private final String indexName;
        private final PTable.IndexType indexType;
        private final List<String> columns;
        private final List<String> include;

        public Index(String indexName, PTable.IndexType indexType, Column ... columns) {
            this.indexName = indexName;
            this.indexType = indexType;
            this.columns = Arrays.stream(columns).map(Column::column).collect(Collectors.toList());
            this.include = null;
        }

        public Index(TableName table, PTable.IndexType indexType, List<?> columns, List<?> include) {
            this(table.getNameAsString().replaceAll("[:\\\\.]", "_").toUpperCase() + "_" + columns
                            .stream()
                            .map(Object::toString)
                            .collect(Collectors.joining("_"))
                            .replaceAll("[\"\\[\\]]", "") + "_IDX",
                    indexType, columns, include);
        }

        public Index(String indexName, PTable.IndexType indexType, List<?> columns, List<?> include) {
            this.indexName = indexName;
            this.indexType = indexType;

            this.columns = columns.stream()
                    .map(o -> o instanceof Column ? ((Column) o).column() : o.toString())
                    .collect(Collectors.toList());
            this.include = include.stream()
                    .map(o -> o instanceof Column ? ((Column) o).column() : o.toString())
                    .collect(Collectors.toList());
        }
    }
}