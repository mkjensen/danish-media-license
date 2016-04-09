package com.github.mkjensen.dml;

import android.test.ApplicationTestCase;
import android.test.suitebuilder.annotation.MediumTest;

import org.junit.Rule;
import org.junit.rules.ExpectedException;

/**
 * Instrumentation tests for {@link DmlApplication}.
 */
@MediumTest
public class DmlApplicationAndroidTest extends ApplicationTestCase<DmlApplication> {

  @Rule
  public final ExpectedException thrown = ExpectedException.none();

  public DmlApplicationAndroidTest() {
    super(DmlApplication.class);
  }

  /**
   * Instrumentation test.
   */
  public void test_getInstance_whenCalledBeforeOnCreate_thenThrowsDmlException() {

    // When/then
    thrown.expect(DmlException.class);
    DmlApplication.getInstance();
  }

  /**
   * Instrumentation test.
   */
  public void test_getInstance_whenCalledAfterOnCreate_thenReturnsDmlApplication() {

    // Given
    createApplication();

    // When
    DmlApplication instance = DmlApplication.getInstance();

    // Then
    assertEquals(getApplication(), instance);
  }
}
