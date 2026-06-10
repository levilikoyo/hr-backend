package com.hr.backend;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import javax.sql.DataSource;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class OrganizationScopedUniqueIndexCleanup implements ApplicationRunner {

    private static final Map<String, Set<String>> GLOBAL_CODE_INDEXES = Map.ofEntries(
            Map.entry("funds", Set.of("fund_code")),
            Map.entry("customers", Set.of("customer_code")),
            Map.entry("vendors", Set.of("vendor_code")),
            Map.entry("banks", Set.of("bank_code")),
            Map.entry("fixed_assets", Set.of("asset_code")),
            Map.entry("currencies", Set.of("curency_code")),
            Map.entry("gl_accounts", Set.of("gl_code")),
            Map.entry("accounting_frameworks", Set.of("framework_code")),
            Map.entry("dimension_setups", Set.of("dimension_code")),
            Map.entry("dimension_values", Set.of("value_code")),
            Map.entry("asset_book_categories", Set.of("book_code")),
            Map.entry("departments", Set.of("department_code"))
    );

    private final DataSource dataSource;

    public OrganizationScopedUniqueIndexCleanup(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void run(ApplicationArguments args) {
        try (Connection connection = dataSource.getConnection()) {
            DatabaseMetaData metaData = connection.getMetaData();
            String product = metaData.getDatabaseProductName().toLowerCase(Locale.ROOT);
            String schema = currentSchema(connection);

            for (Map.Entry<String, Set<String>> tableEntry : GLOBAL_CODE_INDEXES.entrySet()) {
                for (String indexName : findSingleColumnUniqueIndexes(
                        metaData,
                        connection.getCatalog(),
                        schema,
                        tableEntry.getKey(),
                        tableEntry.getValue()
                )) {
                    dropIndex(connection, product, tableEntry.getKey(), indexName);
                }
            }
        } catch (Exception e) {
            System.out.println("Organization-scoped unique index cleanup skipped: " + e.getMessage());
        }
    }

    private List<String> findSingleColumnUniqueIndexes(
            DatabaseMetaData metaData,
            String catalog,
            String schema,
            String table,
            Set<String> targetColumns
    ) throws Exception {
        Map<String, Map<Short, String>> indexes = new LinkedHashMap<>();

        try (ResultSet rs = metaData.getIndexInfo(catalog, schema, table, true, false)) {
            while (rs.next()) {
                String indexName = rs.getString("INDEX_NAME");
                String columnName = rs.getString("COLUMN_NAME");
                boolean nonUnique = rs.getBoolean("NON_UNIQUE");

                if (nonUnique || indexName == null || columnName == null || "PRIMARY".equalsIgnoreCase(indexName)) {
                    continue;
                }

                short position = rs.getShort("ORDINAL_POSITION");
                indexes.computeIfAbsent(indexName, key -> new TreeMap<>())
                        .put(position, columnName.toLowerCase(Locale.ROOT));
            }
        }

        List<String> result = new ArrayList<>();
        for (Map.Entry<String, Map<Short, String>> index : indexes.entrySet()) {
            List<String> columns = new ArrayList<>(index.getValue().values());
            if (columns.size() == 1 && targetColumns.contains(columns.get(0))) {
                result.add(index.getKey());
            }
        }
        return result;
    }

    private String currentSchema(Connection connection) {
        try {
            return connection.getSchema();
        } catch (Exception e) {
            return null;
        }
    }

    private void dropIndex(Connection connection, String product, String table, String indexName) throws Exception {
        String sql;
        if (product.contains("mysql") || product.contains("mariadb")) {
            sql = "ALTER TABLE `" + table + "` DROP INDEX `" + indexName + "`";
        } else if (product.contains("postgres")) {
            sql = "DROP INDEX IF EXISTS \"" + indexName + "\"";
        } else if (product.contains("sql server")) {
            sql = "DROP INDEX " + indexName + " ON " + table;
        } else {
            sql = "DROP INDEX IF EXISTS " + indexName;
        }

        try (Statement statement = connection.createStatement()) {
            statement.execute(sql);
            System.out.println("Removed old global unique index " + indexName + " on " + table);
        }
    }
}
