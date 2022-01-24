/*
 * *****************************************************************************
 *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  All rights reserved.
 *  ****************************************************************************
 */

import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {HttpHeadersService} from "../../shared/services/http-headers.service";
import {UrlConstantsService} from "../../shared/services/url.constants.service";
import {Agent} from "../models/agent.model";
import {Page} from "../../shared/models/page";
import {Observable, throwError} from "rxjs";
import {catchError, map, timeout} from "rxjs/operators";
import {Pageable} from "app/shared/models/pageable";
import {AgentInfo} from "../models/agent-info.model";

@Injectable({
  providedIn: 'root'
})
export class AgentService {
  public localAgentUrl: String = "https://local.testsigmaagent.com:9494";
  constructor(
    private http: HttpClient,
    private httpHeaders: HttpHeadersService,
    private URLConstants: UrlConstantsService) {
  }

  public findAll(filter?: string, sortBy?: string, pageable?: Pageable): Observable<Page<Agent>> {
    return this.http.get<Page<Agent>>(this.URLConstants.agentsUrl, {
      headers: this.httpHeaders.contentTypeApplication,
      params: this.httpHeaders.serializeParams(filter, sortBy, pageable)
    }).pipe(
      timeout(60000),
      map(data => new Page<Agent>().deserialize(data, Agent)),
      catchError(() => throwError('Problem while fetching Agents not found'))
    )
  }

  public findAllPrivateAndPublic(filter?: string, sortBy?: string, pageable?: Pageable): Observable<Page<Agent>> {
    return this.http.get<Page<Agent>>(this.URLConstants.agentsUrl+"/all", {
      headers: this.httpHeaders.contentTypeApplication,
      params: this.httpHeaders.serializeParams(filter, sortBy, pageable)
    }).pipe(
      timeout(60000),
      map(data => new Page<Agent>().deserialize(data, Agent)),
      catchError(() => throwError('Problem while fetching Agents not found'))
    )
  }

  public find(agentId: Number): Observable<Agent> {
    return this.http.get<Agent>(this.URLConstants.agentsUrl + '/' + agentId, {headers: this.httpHeaders.contentTypeApplication}).pipe(
      timeout(60000),
      map(data => new Agent().deserialize(data)),
      catchError(() => throwError('Problem while fetching agent::' + agentId))
    );
  }

  public findByUuid(agentId: String): Observable<Agent> {
    return this.http.get<Agent>(this.URLConstants.agentsUrl + '/' + agentId + "/uuid", {headers: this.httpHeaders.contentTypeApplication}).pipe(
      timeout(60000),
      map(data => new Agent().deserialize(data)),
      catchError(() => throwError('Problem while checking Visibility::' + agentId))
    );
  }

  public create(agent: Agent): Observable<Agent> {
    return this.http.post<Agent>(this.URLConstants.agentsUrl + '/', agent.serialize()).pipe(
      timeout(120000),
      map((data) => new Agent().deserialize(data)),
      catchError((error, fieldErrors) => throwError((fieldErrors) ? 'duplicate' : 'Problem while saving Agent - ' + error))
    );
  }

  public update(id: Number, agent: Agent): Observable<Agent> {
    return this.http.put<Agent>(this.URLConstants.agentsUrl + '/' + agent.id, agent.serialize(), {headers: this.httpHeaders.contentTypeApplication}).pipe(
      timeout(60000),
      map(data => new Agent().deserialize(data)),
      catchError((error, fieldErrors) => throwError((fieldErrors) ? 'duplicate' : 'Problem while saving Agent - ' + error))
    );
  }

  public destroy(id: Number): Observable<void> {
    return this.http.delete<void>(this.URLConstants.agentsUrl + '/' + id).pipe(
      timeout(60000),
      catchError((err) => throwError(err))
    );
  }

  public registerAgent(agent: Agent): Observable<void> {
    return this.http.put<void>(this.localAgentUrl + "/agent/api/v1/"
      + agent.uniqueId + "/register?jwtApiKey=" + agent.jwtApiKey, null).pipe(
      timeout(60000),
      catchError((err) => throwError(err))
    );
  }

  public deregisterAgent(agent: Agent): Observable<void> {
    return this.http.delete<void>(this.localAgentUrl + "/agent/api/v1/agent/" + agent.uniqueId);

  }

  public ping(): Observable<AgentInfo> {
    return this.http.get<AgentInfo>(this.localAgentUrl + "/agent/api/v1/agent/agent_info").pipe(
      timeout(10000),
      map(data => new AgentInfo().deserialize(data)),
      catchError((err) => throwError(err))
    );
  }

  public downloadTag(): Observable<JSON> {
    return this.http.get<JSON>(this.URLConstants.agentDownloadTagUrl, {headers: this.httpHeaders.contentTypeApplication}).pipe(
      timeout(60000),
      map(data => data),
      catchError(() => throwError('Problem while fetching Agent download tag -'))
    );
  }
}

