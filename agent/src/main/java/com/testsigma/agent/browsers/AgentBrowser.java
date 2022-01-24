package com.testsigma.agent.browsers;

import com.testsigma.automator.entity.OsBrowserType;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

@Data
@NoArgsConstructor
public class AgentBrowser {

  private OsBrowserType name;
  private String version;
  private int arch;
  @Getter
  private int majorVersion;

  public AgentBrowser(OsBrowserType name, String version, int arch) {
    this.name = name;
    this.version = version;
    this.arch = arch;
    this.setMajorVersion();
  }

  public void setMajorVersion() {
    this.majorVersion = Integer.parseInt(StringUtils.split(this.version, ".")[0]);
  }
}
