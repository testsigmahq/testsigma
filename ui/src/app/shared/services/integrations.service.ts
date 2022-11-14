import {Injectable} from '@angular/core';
import {HttpHeadersService} from "./http-headers.service";
import {UrlConstantsService} from "./url.constants.service";
import {Pageable} from "../models/pageable";
import {HttpClient, HttpParams} from '@angular/common/http';
import {Observable, throwError} from 'rxjs';
import {catchError, map} from 'rxjs/operators';
import {Integrations} from "../models/integrations.model";
import {JiraProject} from "../../models/jira-project.model";
import {FreshReleaseProject} from "../../models/fresh-release-project.model";
import {FreshReleaseIssueType} from "../../models/fresh-release-issue-type.model";
import {AzureIssueType} from "../../models/azure-issue-type.model";
import {AzureProject} from "../../models/azure-project.model";
import {YoutrackProject} from "../../models/youtrack-project.model";
import {YoutrackIssue} from "../../models/youtrack-issue.model";

@Injectable({
  providedIn: 'root'
})
export class IntegrationsService {

  constructor(
    private http: HttpClient,
    private httpHeaders: HttpHeadersService,
    private URLConstants: UrlConstantsService) {
  }

  public create(plug: Integrations): Observable<Integrations> {
    console.log(plug.serialize());
    return this.http.post<Integrations>(this.URLConstants.integrationsUrl, plug.serialize()).pipe(
      map((data) => new Integrations().deserialize(data)),
      catchError(() => throwError('Problem while inserting Plugin'))
    );
  }

  public find(addonId: number): Observable<Integrations> {
    return this.http.get(`${this.URLConstants.integrationsUrl}/${addonId}`).pipe(
      map((data) => new Integrations().deserialize(data)),
      catchError(() => throwError('Problem while fetching Plugin'))
    );
  }

  public delete(addonId): Observable<any> {
    return this.http.delete(`${this.URLConstants.integrationsUrl}/${addonId}`)
  }

  public findAll(filter?: string, sortBy?: string, pageable?: Pageable): Observable<Integrations[]> {
    return this.http.get<Integrations[]>(this.URLConstants.integrationsUrl, {
      headers: this.httpHeaders.contentTypeApplication,
      params: this.httpHeaders.serializeParams(filter, sortBy, pageable)
    }).pipe(
      map(data => {
        let list: Integrations[] = [];
        data.forEach(config => list.push(new Integrations().deserialize(config)))
        return list;
      }),
      catchError(() => throwError('Problem while fetching ExternalApplicationConfigs'))
    )
  }

  public getJiraFields(id: Number): Observable<JiraProject[]> {
    return this.http.get<JiraProject[]>(this.URLConstants.integrationsUrl + "/" + id + "/jira_projects", {
      headers: this.httpHeaders.contentTypeApplication
    }).pipe(
      map(data => {
        let list: JiraProject[] = [];
        data.forEach(config => list.push(new JiraProject().deserialize(config)))
        return list;
      }),
      catchError(() => throwError('Problem while fetching Integrations jira_fields'))
    )
  }

  public searchJiraIssues(id: Number, project: String, issueType: number, term?: string): Observable<JSON> {
    let params: HttpParams;
    params = new HttpParams().append("project", project.toString()).append("issueType", issueType.toString());
    if (term)
      params = params.append("summary", term.toString());
    return this.http.get<JSON>(this.URLConstants.integrationsUrl + "/" + id + "/search_jira_issues", {
      headers: this.httpHeaders.contentTypeApplication,
      params: params
    }).pipe(
      map(data => data),
      catchError(() => throwError('Problem while fetching Integrations jira_issues'))
    )
  }

  public getFRProjects(id: Number): Observable<FreshReleaseProject[]> {
    return this.http.get<FreshReleaseProject[]>(this.URLConstants.integrationsUrl + "/" + id + "/freshrelease_projects", {
      headers: this.httpHeaders.contentTypeApplication
    }).pipe(
      map(data => {
        let list: FreshReleaseProject[] = [];
        data['projects'].forEach(config => list.push(new FreshReleaseProject().deserialize(config)))
        return list;
      }),
      catchError((err) => {
        return throwError(err)
      })
    )
  }

  public getFRIssueTypes(id: Number, project: String): Observable<FreshReleaseIssueType[]> {
    let params: HttpParams;
    params = new HttpParams().append("project", project.toString());
    return this.http.get<FreshReleaseIssueType[]>(this.URLConstants.integrationsUrl + "/" + id + "/freshrelease_issue_types", {
      headers: this.httpHeaders.contentTypeApplication,
      params: params
    }).pipe(
      map(data => {
        let list: FreshReleaseIssueType[] = [];
        data['issue_types'].forEach(config => list.push(new FreshReleaseIssueType().deserialize(config)))
        return list;
      }),
      catchError((err) => {
        return throwError(err)
      })
    )
  }

  public searchFreshReleaseIssues(id: Number, project: String, term?: String): Observable<JSON> {
    let params: HttpParams;
    params = new HttpParams().append("project", project.toString());
    if (term)
      params = params.append("title", term.toString());
    return this.http.get<JSON>(this.URLConstants.integrationsUrl + "/" + id + "/search_freshrelease_issues", {
      headers: this.httpHeaders.contentTypeApplication,
      params: params
    }).pipe(
      map(data => data),
      catchError((err) => {
        return throwError(err)
      })
    )
  }

  public getMantisProjects(id: Number): Observable<JSON> {
    return this.http.get<JSON>(this.URLConstants.integrationsUrl + '/' + id + '/mantis_projects').pipe(
      map(data => data),
      catchError((err) => {
        return throwError(err);
      })
    );
  }

  public getMantisIssue(id: Number, issueId: String): Observable<JSON> {
    return this.http.get<JSON>(this.URLConstants.integrationsUrl + '/' + id + '/search_mantis_issue/' + issueId, {
      headers: this.httpHeaders.contentTypeApplication,
    }).pipe(
      map(data => data),
      catchError((err) => {
        return throwError(err);
      })
    );
  }

  public searchMantisIssues(id: Number, project: String): Observable<JSON> {
    let params: HttpParams;
    params = new HttpParams().append('project', project.toString());
    return this.http.get<JSON>(this.URLConstants.integrationsUrl + '/' + id + '/search_mantis_issues', {
      headers: this.httpHeaders.contentTypeApplication,
      params: params
    }).pipe(
      map(data => data),
      catchError((err) => {
        return throwError(err)
      })
    );
  }


  public getAzureProjects(id: Number): Observable<AzureProject[]> {
    return this.http.get<AzureProject[]>(this.URLConstants.integrationsUrl + "/" + id + "/azure_projects", {
      headers: this.httpHeaders.contentTypeApplication
    }).pipe(
      map(data => {
        let list: AzureProject[] = [];
        data['value'].forEach(config => list.push(new AzureProject().deserialize(config)))
        return list;
      }),
      catchError((err) => {
        return throwError(err)
      })
    )
  }

  public getAzureIssueTypes(id: Number, project: String): Observable<AzureIssueType[]> {
    let params: HttpParams;
    params = new HttpParams().append("project", project.toString());
    return this.http.get<AzureIssueType[]>(this.URLConstants.integrationsUrl + "/" + id + "/azure_issue_types", {
      headers: this.httpHeaders.contentTypeApplication,
      params: params
    }).pipe(
      map(data => {
        let list: AzureIssueType[] = [];
        data['value'].forEach(config =>
          list.push(new AzureIssueType().deserialize(config)));
        return list;
      }),
      catchError((err) => {
        return throwError(err)
      })
    )
  }

  public searchAzureIssues(id: Number, project: String, issueType: String, term?: String): Observable<JSON> {
    let params: HttpParams;
    params = new HttpParams().append("project", project.toString());
    params = params.append("issueType", issueType.toString());
    if (term)
      params = params.append("title", term.toString());
    return this.http.get<JSON>(this.URLConstants.integrationsUrl + "/" + id + "/search_azure_issues", {
      headers: this.httpHeaders.contentTypeApplication,
      params: params
    }).pipe(
      map(data => data),
      catchError((err) => {
        return throwError(err)
      })
    )
  }

  public getAzureIssues(id: Number, issueIds: String): Observable<JSON> {
    let params: HttpParams;
    params = new HttpParams().append("ids", issueIds.toString());
    return this.http.get<JSON>(this.URLConstants.integrationsUrl + "/" + id + "/get_azure_issues", {
      headers: this.httpHeaders.contentTypeApplication,
      params: params
    }).pipe(
      map(data => data),
      catchError((err) => {
        return throwError(err)
      })
    )
  }


  public getBackLogPriorities(id: Number): Observable<JSON> {
    return this.http.get<JSON>(this.URLConstants.integrationsUrl + '/' + id + '/search_backlog_priorities').pipe(
      map(data => data),
      catchError((err) => {
        return throwError(err);
      })
    );
  }

  public getBackLogProjects(id: Number): Observable<JSON> {
    return this.http.get<JSON>(this.URLConstants.integrationsUrl + '/' + id + '/backlog_projects').pipe(
      map(data => data),
      catchError((err) => {
        return throwError(err);
      })
    );
  }

  public getBackLogIssueTypes(id: Number, projectId: String): Observable<JSON> {
    let params: HttpParams;
    params = new HttpParams().append('project', projectId.toString());
    return this.http.get<JSON>(this.URLConstants.integrationsUrl + '/' + id + '/search_backlog_issue_types', {
      headers: this.httpHeaders.contentTypeApplication,
      params
    }).pipe(
      map(data => data),
      catchError((err) => {
        return throwError(err);
      })
    );
  }

  public searchBackLogIssues(id: Number, project: String, issueTypeId: number, priorityId: number, term?: string): Observable<any> {
    let params: HttpParams;
    params = new HttpParams().append('project', project.toString())
      .append('issueTypeId', issueTypeId.toString())
      .append('priorityId', priorityId.toString());
    if(term)
      params = params.append('keyword', term)
    return this.http.get<any>(this.URLConstants.integrationsUrl + '/' + id + '/search_backlog_issues', {
      headers: this.httpHeaders.contentTypeApplication,
      params
    }).pipe(
      map(data => data),
      catchError((err) => {
        return throwError(err);
      })
    );
  }

  public getBackLogIssue(id: Number, issueId: String): Observable<any> {
    let params: HttpParams;
    params = new HttpParams()
      .append('issueId', issueId.toString());
    return this.http.get<any>(this.URLConstants.integrationsUrl + '/' + id + '/search_backlog_issue', {
      headers: this.httpHeaders.contentTypeApplication,
      params
    }).pipe(
      map(data => data),
      catchError((err) => {
        return throwError(err);
      })
    );
  }


  public getClickUpTeams(id: Number): Observable<JSON> {
    return this.http.get<JSON>(this.URLConstants.integrationsUrl + '/' + id + '/clickup_teams').pipe(
      map(data => data),
      catchError((err) => {
        return throwError(err);
      })
    );
  }

  public getClickUpSpaces(id: Number, teamId: String): Observable<JSON> {
    let params: HttpParams;
    params = new HttpParams().append('teamId', teamId.toString());
    return this.http.get<JSON>(this.URLConstants.integrationsUrl + '/' + id + '/clickup_spaces', {
      headers: this.httpHeaders.contentTypeApplication,
      params
    }).pipe(
      map(data => data),
      catchError((err) => {
        return throwError(err);
      })
    );
  }

  public getClickUpFolders(id: Number, spaceId: String): Observable<JSON> {
    let params: HttpParams;
    params = new HttpParams().append('spaceId', spaceId.toString());
    return this.http.get<JSON>(this.URLConstants.integrationsUrl + '/' + id + '/clickup_folders', {
      headers: this.httpHeaders.contentTypeApplication,
      params
    }).pipe(
      map(data => data),
      catchError((err) => {
        return throwError(err);
      })
    );
  }

  public getClickUpLists(id: Number, folderId: String): Observable<JSON> {
    let params: HttpParams;
    params = new HttpParams().append('folderId', folderId.toString());
    return this.http.get<JSON>(this.URLConstants.integrationsUrl + '/' + id + '/clickup_lists', {
      headers: this.httpHeaders.contentTypeApplication,
      params
    }).pipe(
      map(data => data),
      catchError((err) => {
        return throwError(err);
      })
    );
  }

  public searchClickUpIssues(id: Number, listId: String): Observable<JSON> {
    let params: HttpParams;
    params = new HttpParams().append('listId', listId.toString());
    return this.http.get<JSON>(this.URLConstants.integrationsUrl + '/' + id + '/clickup_tasks', {
      headers: this.httpHeaders.contentTypeApplication,
      params
    }).pipe(
      map(data => data),
      catchError((err) => {
        return throwError(err);
      })
    );
  }


  public getZepelProjects(id: Number): Observable<JSON> {
    return this.http.get<JSON>(this.URLConstants.integrationsUrl + '/' + id + '/zepel_projects').pipe(
      map(data => data),
      catchError((err) => {
        return throwError(err);
      })
    );
  }

  public getZepelIssueTypes(id: Number, projectId: String): Observable<JSON> {
    let params: HttpParams;
    params = new HttpParams().append('project', projectId.toString());
    return this.http.get<JSON>(this.URLConstants.integrationsUrl + '/' + id + '/search_zepel_issue_types', {
      headers: this.httpHeaders.contentTypeApplication,
      params
    }).pipe(
      map(data => data),
      catchError((err) => {
        return throwError(err);
      })
    );
  }

  public searchZepelIssues(id: Number, project: String, issueTypeId: number): Observable<any> {
    let params: HttpParams;
    params = new HttpParams().append('project', project.toString())
      .append('issueTypeId', issueTypeId.toString());
    return this.http.get<any>(this.URLConstants.integrationsUrl + '/' + id + '/search_zepel_issues', {
      headers: this.httpHeaders.contentTypeApplication,
      params
    }).pipe(
      map(data => data),
      catchError((err) => {
        return throwError(err);
      })
    );
  }

  public getYoutrackProjects(id: Number): Observable<YoutrackProject[]> {
    return this.http.get<YoutrackProject[]>(this.URLConstants.integrationsUrl + "/" + id + "/youtrack_projects", {
      headers: this.httpHeaders.contentTypeApplication
    }).pipe(
      map(data => {
        let list: YoutrackProject[] = [];
        data.forEach(config => list.push(new YoutrackProject().deserialize(config)))
        return list;
      }),
      catchError((err) => {
        return throwError(err)
      })
    )
  }

  public searchYoutrackIssues(id: Number,  term?: String): Observable<YoutrackIssue[]> {
    let params: HttpParams;
    if (term)
      params = new HttpParams().append("title", term.toString());
    return this.http.get<YoutrackIssue[]>(this.URLConstants.integrationsUrl + "/" + id + "/search_youtrack_issues", {
      headers: this.httpHeaders.contentTypeApplication,
      params: params
    }).pipe(
      map(data => {
        let list: YoutrackIssue[] = [];
        data.forEach(config => list.push(new YoutrackIssue().deserialize(config)))
        return list;
      }),
      catchError((err) => {
        return throwError(err)
      })
    )
  }
  public testIntegration(plug: Integrations): Observable<JSON> {
    console.log(plug.serialize());
      return this.http.post<JSON>(this.URLConstants.integrationsUrl+ "/test_"+plug.name.toLowerCase()+"_integration", plug.serialize()).pipe(
      map(data => data),
      catchError((err) => {
        return throwError(err)
      })
    )
  }

  public getBugZillaProjects(id: Number): Observable<JSON> {
    return this.http.get<JSON>(this.URLConstants.integrationsUrl + '/' + id + '/bugzilla_projects').pipe(
      map(data => data),
      catchError((err) => {
        return throwError(err);
      })
    );
  }

  public searchBugZillaIssues(id: Number, project: String, issueType: String, version: String): Observable<JSON> {
    let params: HttpParams;
    params = new HttpParams().append('project', project.toString())
      .append('issueType', issueType.toString())
      .append('version', version.toString());
    return this.http.get<JSON>(this.URLConstants.integrationsUrl + '/' + id + '/search_bugzilla_issues', {
      headers: this.httpHeaders.contentTypeApplication,
      params: params
    }).pipe(
      map(data => data),
      catchError((err) => {
        return throwError(err)
      })
    );
  }

  public getBugZillaIssue(id: Number, issueId: String): Observable<JSON> {
    return this.http.get<JSON>(this.URLConstants.integrationsUrl + '/' + id + '/search_bugzilla_issue/' + issueId, {
      headers: this.httpHeaders.contentTypeApplication,
    }).pipe(
      map(data => data),
      catchError((err) => {
        return throwError(err);
      })
    );
  }

  public getTrelloProjects(id: Number): Observable<any> {
    return this.http.get<any>(this.URLConstants.integrationsUrl + '/' + id + '/trello_projects').pipe(
      map(data => data),
      catchError((err) => {
        return throwError(err);
      })
    );
  }

  public getTrelloIssueTypes(id: Number, projectId: String): Observable<any> {
    let params: HttpParams;
    params = new HttpParams().append('project', projectId.toString());
    return this.http.get<any>(this.URLConstants.integrationsUrl + '/' + id + '/search_trello_issue_types', {
      headers: this.httpHeaders.contentTypeApplication,
      params
    }).pipe(
      map(data => data),
      catchError((err) => {
        return throwError(err);
      })
    );
  }

  public searchTrelloIssues(id: Number, issueType: String): Observable<any> {
    let params: HttpParams;
    params = new HttpParams().append('issueTypeId', issueType.toString());
    return this.http.get<any>(this.URLConstants.integrationsUrl + '/' + id + '/search_trello_issues', {
      headers: this.httpHeaders.contentTypeApplication,
      params
    }).pipe(
      map(data => data),
      catchError((err) => {
        return throwError(err);
      })
    );
  }

  public getTrelloIssue(id: Number, issueId: String): Observable<JSON> {
    let params: HttpParams;
    params = new HttpParams().append('issueId', issueId.toString());
    return this.http.get<JSON>(this.URLConstants.integrationsUrl + '/' + id + '/search_trello_issue/', {
      headers: this.httpHeaders.contentTypeApplication,
      params
    }).pipe(
      map(data => data),
      catchError((err) => {
        return throwError(err);
      })
    );
  }

  public getLinearTeams(id: Number): Observable<any> {
    return this.http.get<any>(this.URLConstants.integrationsUrl + '/' + id + '/linear_teams').pipe(
      map(data => data),
      catchError((err) => {
        return throwError(err);
      })
    );
  }

  public getLinearProjects(id: Number, teamId: String): Observable<any> {
    let params: HttpParams;
    params = new HttpParams().append('teamId', teamId.toString());
    return this.http.get<any>(this.URLConstants.integrationsUrl + '/' + id + '/search_linear_projects', {
      headers: this.httpHeaders.contentTypeApplication,
      params
    }).pipe(
      map(data => data),
      catchError((err) => {
        return throwError(err);
      })
    );
  }

  public searchLinearIssues(id: Number, projectId: String): Observable<any> {
    let params: HttpParams;
    params = new HttpParams().append('projectId', projectId.toString());
    return this.http.get<any>(this.URLConstants.integrationsUrl + '/' + id + '/search_linear_issues', {
      headers: this.httpHeaders.contentTypeApplication,
      params
    }).pipe(
      map(data => data),
      catchError((err) => {
        return throwError(err);
      })
    );
  }

  public getLinearIssue(id: Number, issueId: String): Observable<any> {
    let params: HttpParams;
    params = new HttpParams().append('issueId', issueId.toString());
    return this.http.get<any>(this.URLConstants.integrationsUrl + '/' + id + '/search_linear_issue', {
      headers: this.httpHeaders.contentTypeApplication,
      params
    }).pipe(
      map(data => data),
      catchError((err) => {
        return throwError(err);
      })
    );
  }
}
