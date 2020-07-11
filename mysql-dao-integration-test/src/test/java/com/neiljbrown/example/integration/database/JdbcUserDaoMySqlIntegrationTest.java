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
import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;
import javax.sql.DataSource;

import org.junit.jupiter.api.Test;

import com.neiljbrown.example.business.domain.User;

/**
 * Integration tests of {@link JdbcUserDao} in conjunction with MySQL.
 * <p>
 * Limitation - This test currently relies on instance of MySQL running locally on the default port (i.e. accessible
 * via localhost:3306).
 */
class JdbcUserDaoMySqlIntegrationTest {

  private JdbcUserDao jdbcUserDao;

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