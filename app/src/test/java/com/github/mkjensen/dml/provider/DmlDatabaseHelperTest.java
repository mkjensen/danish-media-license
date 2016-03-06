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

package com.github.mkjensen.dml.provider;

import static org.mockito.Matchers.startsWith;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.inOrder;
import static org.powermock.api.mockito.PowerMockito.mock;

import android.database.sqlite.SQLiteDatabase;

import com.github.mkjensen.dml.PowerMockParameterizedRobolectricTest;
import com.github.mkjensen.dml.RobolectricTest;

import org.junit.Test;
import org.mockito.InOrder;
import org.robolectric.ParameterizedRobolectricTestRunner.Parameters;

import java.util.Arrays;
import java.util.Collection;

/**
 * Unit tests for {@link DmlDatabaseHelper}.
 */
public class DmlDatabaseHelperTest extends PowerMockParameterizedRobolectricTest {

  /**
   * Values for {@link #oldVersion} and {@link #newVersion}.
   */
  @Parameters(name = "oldVersion={0}, newVersion={1}")
  public static Collection<Integer[]> data() {
    return Arrays.asList(new Integer[][] {
        {0, 0}, {0, 1}, {1, 0}
    });
  }

  private final int oldVersion;

  private final int newVersion;

  public DmlDatabaseHelperTest(int oldVersion, int newVersion) {
    this.oldVersion = oldVersion;
    this.newVersion = newVersion;
  }

  @Test
  public void onUpgrade_whenCalled_thenTablesAreDroppedAndCreated() {

    // Given
    DmlDatabaseHelper databaseHelper = new DmlDatabaseHelper(null);
    SQLiteDatabase databaseMock = mock(SQLiteDatabase.class);

    // When
    databaseHelper.onUpgrade(databaseMock, oldVersion, newVersion);

    // Then
    InOrder order = inOrder(databaseMock);
    order.verify(databaseMock, atLeastOnce()).execSQL(startsWith("DROP TABLE"));
    order.verify(databaseMock, atLeastOnce()).execSQL(startsWith("CREATE TABLE"));
  }
}
