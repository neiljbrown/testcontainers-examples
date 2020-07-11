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

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;
import java.util.Optional;

import javax.sql.DataSource;

import com.google.common.base.Preconditions;
import com.neiljbrown.example.business.domain.User;

/**
 * Implementation of the Data Access Object (DAO) for users, which uses the JDBC API to store and retrieve the
 * entities to/from the service's relational database.
 */
public class JdbcUserDao {

  private final DataSource dataSource;

  /**
   * @param dataSource the {@link DataSource} to use to obtain a connection to the service's relational DB (the
   * data-store containing users).
   */
  public JdbcUserDao(DataSource dataSource) {
    Objects.requireNonNull(dataSource, "dataSource must not be null.");
    this.dataSource = dataSource;
  }

  /**
   * Finds a {@link User user} by their unique id.
   *
   * @param userId the unique ID of the user.
   * @return an Optional of {@link User user}, empty if the {code userId} doesn't identify an existing user.
   */
  public Optional<User> findUserById(long userId) {
    Preconditions.checkArgument(userId > 0, "userId must be greater than 0.");
    final String sqlQuery = "SELECT * FROM user u WHERE u.id=?";
    try {
      final PreparedStatement preparedStatement = this.dataSource.getConnection().prepareStatement(sqlQuery);
      preparedStatement.setLong(1, userId);
      final ResultSet resultSet = preparedStatement.executeQuery();
      if (resultSet.next()) {
        User user = new User(resultSet.getLong("id"), resultSet.getString("first_name"),
          resultSet.getString("last_name"));
        return Optional.of(user);
      } else {
        return Optional.empty();
      }
    } catch (SQLException sqle) {
      throw new RuntimeException("Error executing query [" + sqlQuery + "].", sqle);
    }
  }

  /**
   * @return the user ID of the last (most recently created) user.
   */
  public long findLastUserId() {
    final String sqlQuery = "SELECT MAX(id) last_user_id FROM user";
    try {
      final PreparedStatement preparedStatement = this.dataSource.getConnection().prepareStatement(sqlQuery);
      final ResultSet resultSet = preparedStatement.executeQuery();
      resultSet.next();
      return resultSet.getLong("last_user_id");
    } catch (SQLException sqle) {
      throw new RuntimeException("Error executing query [" + sqlQuery + "].", sqle);
    }
  }
}