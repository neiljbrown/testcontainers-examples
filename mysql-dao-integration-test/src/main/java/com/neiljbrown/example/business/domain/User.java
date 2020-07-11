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
package com.neiljbrown.example.business.domain;

import com.google.common.base.MoreObjects;

/**
 * A registered user of the application, and user related business logic.
 */
public class User {

  private final long id;
  private final String firstName;
  private final String lastName;

  /**
   * @param id the id of the user.
   * @param firstName the first name of the user.
   * @param lastName the last name of the user.
   */
  public User(long id, String firstName, String lastName) {
    this.id = id;
    this.firstName = firstName;
    this.lastName = lastName;
  }

  public long getId() {
    return this.id;
  }

  public String getFirstName() {
    return this.firstName;
  }

  public String getLastName() {
    return this.lastName;
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
      .add("id", id)
      .add("firstName", firstName)
      .add("lastName", lastName)
      .toString();
  }
}