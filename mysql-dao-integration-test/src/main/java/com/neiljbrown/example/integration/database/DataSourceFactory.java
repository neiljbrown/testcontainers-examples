/*
 * Copyright 2020 - present the original author or authors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *            http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.neiljbrown.example.integration.database;

import javax.sql.DataSource;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

/**
 * Factory for creating instances of the implementation of the JDBC {@link DataSource} that JDBC DAO can use to obtain
 * connections to the app/service's DB.
 */
public class DataSourceFactory {

  /**
   * Creates the DataSource and initialises it with the required mandatory configuration loaded from an external
   * datasource.properties file on the classpath.
   *
   * @return the created {@link DataSource}.
   */
  public static DataSource createDataSource() {
    final HikariConfig hikariConfig = new HikariConfig("/datasource.properties");
    return new HikariDataSource(hikariConfig);
  }

  /**
   * Creates the DataSource and initialises it with required mandatory configuration supplied as parameters.
   *
   * @param jdbcUrl the JDBC URL (aka connection string) to use to locate and connect to the database server, in the
   * format defined by the JDBC DriverManager.
   * @param username the username of the database user for which the connection should be created.
   * @param password the password of the database user for which the connection should be created.
   * @param driverClassName string containing fully-qualified name of the implementation of the JDBC Driver to use to
   * connect to the database server, e.g. com.mysql.jdbc.Driver. Optional. If not supplied (null) the implementation
   * of the DataSource in use will try to determine the class of Driver from the info supplied in {@code jdbcUrl}.
   * @return
   */
  public static DataSource createDataSource(String jdbcUrl, String username, String password, String driverClassName) {
    final HikariConfig hikariConfig = new HikariConfig();
    hikariConfig.setJdbcUrl(jdbcUrl);
    hikariConfig.setUsername(username);
    hikariConfig.setPassword(password);
    if (driverClassName !=null) {
      hikariConfig.setDriverClassName(driverClassName);
    }
    return new HikariDataSource(hikariConfig);
  }
}