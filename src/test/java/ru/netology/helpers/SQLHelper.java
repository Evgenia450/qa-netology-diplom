package ru.netology.helpers;

import lombok.Getter;
import lombok.SneakyThrows;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import java.sql.DriverManager;
import java.util.Arrays;
import java.sql.*;

@Getter
public class SQLHelper {
    private SQLHelper() {
    }

    private final static String[] tableNames = {"credit_request_entity", "payment_entity", "order_entity"};
    static final String urlMySQL = "jdbc:mysql://localhost:3306/app";
    static final String urlPostgreSQL = "jdbc:postgresql://localhost:5432/app";
    static final String userDB = "app";
    static final String passwordDB = "pass";
    static QueryRunner runner = new QueryRunner();

    private static boolean tableExists(String item) {
        return Arrays.asList(tableNames).contains(item);
    }

    @SneakyThrows
    public static long getRowsAmountFrom(String tableName) {
        if (tableExists(tableName)) {
            var rowsAmountQuery = "SELECT COUNT(*) FROM " + tableName + ";";
            long rowsAmount;

            try (
                    var conn = DriverManager.getConnection(urlMySQL, userDB, passwordDB);
            ) {
                rowsAmount = runner.query(conn, rowsAmountQuery, new ScalarHandler<>());
            }
            return rowsAmount;
        } else {
            return 0;
        }

    }

    @SneakyThrows
    public static String getLastStatusFromPaymentsTable() {
        var statusQuery = "SELECT status FROM payment_entity WHERE transaction_id = (SELECT payment_id FROM order_entity ORDER BY created DESC LIMIT 1);";
        String status;

        try (
                var conn = DriverManager.getConnection(urlMySQL, userDB, passwordDB);
        ) {
            status = runner.query(conn, statusQuery, new ScalarHandler<>());
        }
        return status;

    }

    @SneakyThrows
    public static String getLastStatusFromCreditsTable() {
        var statusQuery = "SELECT status FROM credit_request_entity WHERE bank_id = (SELECT credit_id FROM order_entity ORDER BY created DESC LIMIT 1);";
        String status;

        try (
                var conn = DriverManager.getConnection(urlMySQL, userDB, passwordDB);
        ) {
            status = runner.query(conn, statusQuery, new ScalarHandler<>());
        }
        return status;
    }

    @SneakyThrows
    public static void cleanDatabase() {
        try (
                var conn = DriverManager.getConnection(urlMySQL, userDB, passwordDB);
        ) {
            for (int i = 0; i < tableNames.length; i++) {
                runner.execute(conn, "DELETE FROM " + tableNames[i] + ";");
            }
        }
    }
}
