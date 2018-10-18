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

import javax.swing.Icon;

import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.IconLoader;
import com.intellij.openapi.vcs.changes.CommitExecutor;
import com.intellij.openapi.vcs.changes.CommitSession;

/**
 * The code review plugin that is responsible for creating the CommitSession
 * object.
 */
public class CodeReviewCommitExecutor implements CommitExecutor {
  private Project project;

  public CodeReviewCommitExecutor(Project project) {
    this.project = project;
  }

  @NotNull
  public Icon getActionIcon() {
    return IconLoader.getIcon("/actions/back.png");
  }

  @Nls
  public String getActionText() {
    return "Code Review...";
  }

  @Nls
  public String getActionDescription() {
    return "Sends a code review email before code is committed to the VCS";
  }

  @NotNull
  public CommitSession createCommitSession() {
    return new CodeReviewCommitSession(project);
  }
}
