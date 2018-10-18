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

package net.redyeti.codereview;

import javax.swing.Icon;
import javax.swing.JComponent;

import org.jetbrains.annotations.*;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.util.IconLoader;

public class CodeReviewConfigurable implements Configurable {
  public CodeReviewConfigurable() {}

  private CodeReviewConfigForm form;

  @Nls
  public String getDisplayName() {
    return "Code Review";
  }

  public Icon getIcon() {
    return IconLoader.getIcon("/net/redyeti/codereview/resources/ConfigIcon.png");
  }

  @Nullable
  @NonNls
  public String getHelpTopic() {
    return null;
  }

  public JComponent createComponent() {
    if (form == null) {
      form = new CodeReviewConfigForm();
      CodeReviewConfig config = ServiceManager.getService(CodeReviewConfig.class);
      form.setData(config);
    }
    return form.getRootComponent();
  }

  public boolean isModified() {
    CodeReviewConfig config = ServiceManager.getService(CodeReviewConfig.class);
    return form != null && form.isModified(config);
  }

  public void apply() throws ConfigurationException {
    if (form != null) {
      CodeReviewConfig config = ServiceManager.getService(CodeReviewConfig.class);
      form.getData(config);
    }
  }

  public void reset() {
    if (form != null) {
      CodeReviewConfig config = ServiceManager.getService(CodeReviewConfig.class);
      form.setData(config);
    }
  }

  public void disposeUIResources() {
    form = null;
  }
}
