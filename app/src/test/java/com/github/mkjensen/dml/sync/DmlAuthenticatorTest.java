/*
 * Copyright 2016 Martin Kamp Jensen
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.mkjensen.dml.sync;

import static org.junit.Assert.assertNull;

import android.accounts.NetworkErrorException;
import android.os.Bundle;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * Unit tests for {@link DmlAuthenticator}.
 */
public class DmlAuthenticatorTest {

  @Rule
  public final ExpectedException thrown = ExpectedException.none();

  private DmlAuthenticator authenticator;

  /**
   * Creates {@link #authenticator}.
   */
  @Before
  public void before() {

    // Given
    authenticator = new DmlAuthenticator(null);
  }

  @Test
  public void editProperties_throwsUnsupportedOperationException() {

    // When/then
    thrown.expect(UnsupportedOperationException.class);
    Bundle bundle = authenticator.editProperties(null, null);
    assertNull(bundle); // Hi PMD!
  }

  @Test
  public void addAccount_throwsUnsupportedOperationException() throws NetworkErrorException {

    // When/then
    thrown.expect(UnsupportedOperationException.class);
    Bundle bundle = authenticator.addAccount(null, null, null, null, null);
    assertNull(bundle); // Hi PMD!
  }

  @Test
  public void confirmCredentials_throwsUnsupportedOperationException()
      throws NetworkErrorException {

    // When/then
    thrown.expect(UnsupportedOperationException.class);
    Bundle bundle = authenticator.confirmCredentials(null, null, null);
    assertNull(bundle); // Hi PMD!
  }

  @Test
  public void getAuthToken_throwsUnsupportedOperationException() throws NetworkErrorException {

    // When/then
    thrown.expect(UnsupportedOperationException.class);
    Bundle authToken = authenticator.getAuthToken(null, null, null, null);
    assertNull(authToken); // Hi PMD!
  }

  @Test
  public void getAuthTokenLabel_throwsUnsupportedOperationException() {

    // When/then
    thrown.expect(UnsupportedOperationException.class);
    String authTokenLabel = authenticator.getAuthTokenLabel(null);
    assertNull(authTokenLabel); // Hi PMD!
  }

  @Test
  public void updateCredentials_throwsUnsupportedOperationException() throws NetworkErrorException {

    // When/then
    thrown.expect(UnsupportedOperationException.class);
    Bundle bundle = authenticator.updateCredentials(null, null, null, null);
    assertNull(bundle); // Hi PMD!
  }

  @Test
  public void hasFeatures_throwsUnsupportedOperationException() throws NetworkErrorException {

    // When/then
    thrown.expect(UnsupportedOperationException.class);
    Bundle bundle = authenticator.hasFeatures(null, null, null);
    assertNull(bundle); // Hi PMD!
  }
}
