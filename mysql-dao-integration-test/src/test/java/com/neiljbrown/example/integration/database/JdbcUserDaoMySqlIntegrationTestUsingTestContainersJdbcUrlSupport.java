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

import com.neiljbrown.example.business.domain.User;

/**
 * An implementation of integration tests of {@link JdbcUserDao} in conjunction with MySQL that provides an example
 * of using the Testcontainers library's JDBC URL based approach to launching (and connecting to) the MySQL container.
 *
 * <h2>Support for Launching MySQL</h2>
 * This TestCase has been enhanced to remove the limitation (in the original version) of relying on an instance MySQL
 * running locally on the default port (i.e. accessible via localhost:3306). Instead the Testcontainers library is
 * used to automate launching an instance of the required version of MySQL in a Docker container.
 *
 * <h2>TestContainers Integration Method</h2>
 * This implementation of the integration test provides an example of using the Testcontainers library's custom JDBC
 * URL based approach for launching the MySQL container. This offers a simpler albeit it less flexible approach than
 * using Testcontainers' JUnit integration. The DataSource and hence (standard) JDBC driver is configured to connect
 * to the database using a custom JDBC URL that's proxied by Testcontainers which then intercepts connect requests and
 * launches the container. This integration approach is simpler to implement but there is less ability to customise
 * the configuration/setup of the MySQL container or its lifecycle. A single instance of a container, running on a
 * random port is launched on the first request and shared by all subsequent requests. For more details of how the
 * custom JDBC URL is implemented see method {@link #buildJdbcUrlForMySqlTestContainer(Properties)}.
*/
public class JdbcUserDaoMySqlIntegrationTestUsingTestContainersJdbcUrlSupport extends AbstractJdbcUserDaoMySqlIntegrationTest {

  private static final Logger logger =
    LoggerFactory.getLogger(JdbcUserDaoMySqlIntegrationTestUsingTestContainersJdbcUrlSupport.class);

  /** Create test case. */
  JdbcUserDaoMySqlIntegrationTestUsingTestContainersJdbcUrlSupport() {
    Properties dataSourceProperties = loadDataSourceProperties();
    // Create the DataSource that the DAO under test will use to connect to the database, configuring it to use a
    // JDBC URL with a custom protocol provided by the Testcontainers library, which will result in subsequent requests
    // being routed to an instance of MySQL running in the Testcontainers provided Docker container.
    final DataSource dataSource = DataSourceFactory.createDataSource(
      buildJdbcUrlForMySqlTestContainer(dataSourceProperties),
      dataSourceProperties.getProperty("dataSource.user"),
      dataSourceProperties.getProperty("dataSource.password"),
      dataSourceProperties.getProperty("driverClassName"));
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
   * Builds a custom JDBC URL that uses the Testcontainers' library support for launching a new containerised
   * instance of the application's MySQL database server, to support integration testing the DAO.
   * <p>
   * The DataSource for the application's database is typically configured with a JDBC URL using the standard
   * connection URL syntax "jdbc:mysql://<hostname>:<port>/<database-name>". TestContainers supports reconfiguring
   * the JDBC driver to connect to a containerised instance of (a specific version of) MySQL (which it launches on
   * demand) by substituting the standard "jdbc:mysql:" protocol with the custom one "jdbc:tc:mysql:[mysql-version]".
   * The hostname and port components of the connection URL are not used and ignore if specified, since
   * Testcontainers always launches the containerised instance on random port on localhost.
   *
   * @param dataSourceProperties  properties used to configure the the application's JDBC DataSource. Used to obtain
   * the database name that's used to build the JDBC URL.
   * @return a JDBC URL for accessing the application's DB hosted by an instance of MySQL running in a container that
   * will be launched by the Testcontainers library.
   */
  private String buildJdbcUrlForMySqlTestContainer(Properties dataSourceProperties) {
    return "jdbc:tc:mysql:" + MYSQL_VERSION + ":///" + dataSourceProperties.getProperty("dataSource.databaseName");
  }
}