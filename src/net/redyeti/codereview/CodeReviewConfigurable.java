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
