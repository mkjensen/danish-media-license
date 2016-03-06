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

package com.github.mkjensen.dml;

import org.junit.Rule;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.modules.junit4.rule.PowerMockRule;

/**
 * Base class for running Powermock-enabled Robolectric unit tests.
 */
@PowerMockIgnore( {"android.*", "org.mockito.*", "org.robolectric.*"})
public abstract class PowerMockRobolectricTest extends RobolectricTest {

  @Rule
  public final PowerMockRule powerMockRule = new PowerMockRule();
}
