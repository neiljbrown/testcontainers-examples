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

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;
import javax.sql.DataSource;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.neiljbrown.example.business.domain.User;

/**
 * Integration tests of {@link JdbcUserDao} in conjunction with MySQL.
 * <p>
 * <h2>Support for Launching MySQL</h2>
 * This TestCase has been enhanced to remove the limitation (in the original version) of relying on an instance MySQL
 * running locally on the default port (i.e. accessible via localhost:3306). The Testcontainers library is used to
 * automate launching an instance of the required version of MySQL in a Docker container. The container is shared by
 * all tests, with one instance being started before the first test runs, and being stopped after the final test
 * runs and before the test case exits.
 * <p>
 * Using the Testcontainers library to automate launching the MySQL server rather than relying on an locally running
 * instance of MySQL has the following benefits -
 * <ul>
 *   <li>Fewer Env Prerequisites - Having MySQL installed in env is no longer a pre-req for the test to run.</li>
 *   <li>Increased robustness of test - Test doesn't rely on MySQL having been started and running on a specific
 *   port. As a result the tests are more reliable/robust. The database is also always in the same state.</li>
 *   <li>Consistency - The tests are now guaranteed to always run against the same version of MySQL with identical
 *   configuration across all env.</li>
 * </ul>
*/
@Testcontainers // Enable automatic start and stop of containers based on scanning for fields annotated @Container
class JdbcUserDaoMySqlIntegrationTest {

  private static final Logger logger = LoggerFactory.getLogger(JdbcUserDaoMySqlIntegrationTest.class);

  // Specific version of MySQL to use.
  private static final String MYSQL_VERSION = "5.7.31";

  // Launch a MySQL container that's shared by all test methods. (Declaring an @Container field as static results in
  // the created container being shared by all test methods - started once before first test and stopped after last).
  @Container
  private static final MySQLContainer MY_SQL_CONTAINER = new MySQLContainer("mysql:" + MYSQL_VERSION);

  // Name of host on which DB server can be accessed. (Provided by Testcontainers lib, rather than assuming localhost).
  private static String dbHost;
  // Network port on which DB server can be accessed. (Dynamically allocated by Testcontainers lib to increase
  // robustness of test by avoiding relying on a specific port being available across all environments).
  private static Integer dbPort;

  // Class under test
  private final JdbcUserDao jdbcUserDao;

  /** Initialise test case's static fixtures once before all tests run. */
  @BeforeAll
  static void beforeAll() {
    dbHost = MY_SQL_CONTAINER.getHost();
    dbPort = MY_SQL_CONTAINER.getFirstMappedPort();
    logger.debug("MySQL DB server accessible on host {} and port {}.", dbHost, dbPort);
  }

  /** Create test case. */
  JdbcUserDaoMySqlIntegrationTest() {
    final DataSource dataSource = DataSourceFactory.createDataSource();
    this.jdbcUserDao = new JdbcUserDao(dataSource);
  }

  /**
   * An integration test for {@link JdbcUserDao#findUserById(long)} in the case where the identified user does not
   * exist.
   */
  @Test
  void test_findUserById_whenUserDoesNotExist() {
    long lastUserId = this.jdbcUserDao.findLastUserId();

    final Optional<User> userById = this.jdbcUserDao.findUserById(++lastUserId);

    assertThat(userById).isEmpty();
  }
}