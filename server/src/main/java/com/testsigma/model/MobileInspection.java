package com.testsigma.model;

import com.fasterxml.jackson.core.type.TypeReference;
import com.testsigma.service.ObjectMapperService;
import lombok.Data;
import lombok.extern.log4j.Log4j2;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.json.JSONObject;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "mobile_inspections")
@Data
@Log4j2
public class MobileInspection {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "platform")
  @Enumerated(value = EnumType.STRING)
  private Platform platform;

  @Column(name = "agent_device_id")
  private Long agentDeviceId;

  @Column(name = "status")
  @Enumerated(EnumType.STRING)
  private MobileInspectionStatus status;

  @Column(name = "lab_type")
  @Enumerated(EnumType.STRING)
  private TestPlanLabType labType;

  @Column(name = "platform_device_id")
  private Long platformDeviceId;

  @Column(name = "app_activity")
  private String appActivity;

  @Column(name = "bundle_id")
  private String bundleId;

  @Column(name = "app_upload_id")
  private Long appUploadId;

  @Column(name = "session_id")
  private String sessionId;

  @Column(name = "application_package")
  private String applicationPackage;

  @Column(name = "application_path_type")
  @Enumerated(EnumType.STRING)
  private AppPathType applicationPathType;

  @Column(name = "started_at")
  private Timestamp startedAt;

  @Column(name = "finished_at")
  private Timestamp finishedAt;

  @Column(name = "last_active_at")
  private Timestamp lastActiveAt;

  @Column(name = "created_date")
  @CreationTimestamp
  private Timestamp createdDate;

  @Column(name = "updated_date")
  @UpdateTimestamp
  private Timestamp updatedDate;

  @Column
  private String capabilities;

  public List<Capability> getCapabilities() {
    return new ObjectMapperService().parseJson(capabilities, new TypeReference<>() {
    });
  }

  public void setCapabilities(List<Capability> capabilities) {
    if (capabilities == null)
      this.capabilities = null;
    List<JSONObject> newList = new ArrayList<>();
    for (Capability capability : capabilities) {
      if (!capability.getName().equals(""))
        newList.add(new JSONObject(capability));
    }
    this.capabilities = newList.toString();
  }
}
