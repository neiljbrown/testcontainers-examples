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

import java.io.IOException;
import java.util.Properties;

/**
 * Abstract super-class for implementations of the MySQL DB integration tests of the {@link JdbcUserDao}.
 */
abstract class AbstractJdbcUserDaoMySqlIntegrationTest {

  // Specific version of MySQL to use. (package-private)
  static final String MYSQL_VERSION = "5.7.31";

  // Class under test (package-private)
  JdbcUserDao jdbcUserDao;

  Properties loadDataSourceProperties() {
    Properties dataSourceProperties = new Properties();
    try {
      dataSourceProperties.load(this.getClass().getResourceAsStream("/datasource.properties"));
    } catch (IOException e) {
      throw new RuntimeException("Failed to load datasource.properties.", e);
    }
    return dataSourceProperties;
  }
}