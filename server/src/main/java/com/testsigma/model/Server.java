package com.testsigma.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Entity
@Table(name = "servers")
@Getter
@Setter
@ToString
public class Server {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "server_uuid")
  private String serverUuid;

  @Column(name = "consent")
  private Boolean consent = Boolean.FALSE;

  @Column(name = "consent_request_done")
  private Boolean consentRequestDone = Boolean.FALSE;

  @Column(name = "onboarded")
  private Boolean onboarded;

  public String getServerOs() {
    return "LINUX";
  }
}
