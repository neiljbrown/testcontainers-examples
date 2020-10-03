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
import java.util.Properties;
import javax.sql.DataSource;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.neiljbrown.example.business.domain.User;

/**
 * Integration tests of {@link JdbcUserDao} in conjunction with MySQL.
 *
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
public class JdbcUserDaoMySqlIntegrationTestUsingTestContainersJUnit5Support extends AbstractJdbcUserDaoMySqlIntegrationTest {

  private static final Logger logger = LoggerFactory.getLogger(JdbcUserDaoMySqlIntegrationTestUsingTestContainersJUnit5Support.class);

  // Launch a MySQL container that's shared by all test methods. (Declaring an @Container field as static results in
  // the created container being shared by all test methods - started once before first test and stopped after last).
  @Container
  private static final MySQLContainer MY_SQL_CONTAINER = new MySQLContainer("mysql:" + MYSQL_VERSION);

  /** Create test case. */
  JdbcUserDaoMySqlIntegrationTestUsingTestContainersJUnit5Support() {
    final DataSource dataSource = this.createDataSource();
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

  /**
   * Creates the DataSource that the DAO under test will use to connect to the database. Supports overriding the
   * JDBC URL and the MySQL DB user credentials used when testing against the contained-based MySQL DB server that's
   * used by these tests.
   * <p>
   * When using the Testcontainer library's JUnit support for launching the container, the app's default configured
   * JDBC URL needs to be customised / overridden to account for the MySQL server in the container being exposed /
   * running on a random port (rather than the default 3306).
   * <p>
   * The Testcontainer library creates a MySQL DB user (and password) that can be used to connect to the launched
   * instance of the MySQL DB server. Use this in place of the MySQL DB user credentials that the app is configured to
   * use in other environments.
   *
   * @return a {@link DataSource} that's configured to connect to the MySQL DB server launched by the Testcontainer lib.
   */
  DataSource createDataSource() {
    Properties dataSourceProperties = loadDataSourceProperties();
    // Build a test specific JDBC URL to accommodate the MySQL server in the launched container running on random port
    final String jdbcUrl = buildJdbcUrlForMySqlTestContainer(MY_SQL_CONTAINER.getHost(),
      MY_SQL_CONTAINER.getFirstMappedPort(), dataSourceProperties);
    logger.debug("Configured DataSource to connect to the MySQL database using built JDBC URL {}.", jdbcUrl);
    return DataSourceFactory.createDataSource(jdbcUrl,
      dataSourceProperties.getProperty("dataSource.user"), dataSourceProperties.getProperty("dataSource.password"),
      dataSourceProperties.getProperty("driverClassName"));
  }

  /**
   * Builds the JDBC URL that the test will configure the DataSource (that's used by the DAO under test) to use to
   * connect to the MySQL server. Accommodates the fact that the MySQL server in the launched container is running on a
   * random port, which needs to be used in place of the port configured for the app in production.
   *
   * @param hostname the hostname on which the MySQL server is running, typically localhost.
   * @param port the port on which the MySQL server is running.
   * @param dataSourceProperties  properties used to configure the the application's JDBC DataSource. Used to obtain
   * the database name that's used to build the JDBC URL.
   * @return the JDBC URL for accessing the application's DB.
   */
  private String buildJdbcUrlForMySqlTestContainer(String hostname, Integer port, Properties dataSourceProperties) {
    return "jdbc:mysql://" + hostname + ":" + port + "/" +
      dataSourceProperties.getProperty("dataSource.databaseName") +
      // Disable use of SSL in dev to avoid MySQL logging warning about server's SSL certificate not being verified
      "?useSSL=false";
  }
}