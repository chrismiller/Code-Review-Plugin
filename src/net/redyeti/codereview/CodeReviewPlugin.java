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

import java.security.Security;

import org.jetbrains.annotations.NotNull;

import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vcs.changes.ChangeListManager;

/**
 * An IntelliJ IDEA plugin that allows code reviews to be emailed before checkin.
 */
public class CodeReviewPlugin implements ProjectComponent {
  private Project project;

  public CodeReviewPlugin(Project project) {
    this.project = project;
  }

  public void projectOpened() {
  }

  public void projectClosed() {
    project = null;
  }

  public Project getProject() {
    return project;
  }

  @NotNull
  public String getComponentName() {
    return "Code Review";
  }

  public void initComponent() {
    Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
    ChangeListManager.getInstance(project).registerCommitExecutor(new CodeReviewCommitExecutor(project));
  }

  public void disposeComponent() {
  }
}
