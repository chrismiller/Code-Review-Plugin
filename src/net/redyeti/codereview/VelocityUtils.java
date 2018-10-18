/*
 * Copyright 2018 Chris Miller
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

/**
 * $Source:$
 * $Id:$
 */
package net.redyeti.codereview;

import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.log.SimpleLog4JLogSystem;

/**
 * Velocity helper methods
 */
public class VelocityUtils {
  private VelocityUtils() {}

  public static VelocityEngine newVeloictyEngine() throws Exception {
    VelocityEngine engine = new VelocityEngine();
    engine.setProperty(VelocityEngine.RUNTIME_LOG_LOGSYSTEM_CLASS, SimpleLog4JLogSystem.class.getName());
    engine.setProperty("runtime.log.logsystem.log4j.category", "CodeReviewPlugin");
    engine.init();
    return engine;
  }
}
