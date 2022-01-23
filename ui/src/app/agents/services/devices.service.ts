import {Injectable} from '@angular/core';
import {HttpClient, HttpParams} from "@angular/common/http";
import {HttpHeadersService} from "../../shared/services/http-headers.service";
import {Observable, throwError} from "rxjs";
import {MobileElement} from "../models/mobile-element.model";
import {Page} from "../../shared/models/page";
import {AgentDevice} from "../models/agent-device.model";
import {Pageable} from "../../shared/models/pageable";
import {map} from 'rxjs/internal/operators/map';
import {catchError} from 'rxjs/internal/operators/catchError';
import {Position} from "../models/position.model";
import {MirroringResponse} from "../models/mirroring-response.model";
import {SessionCreationRequest} from "../models/session-creation-request.model";
import {SessionResponse} from "../models/session-response.model";
import {ScreenDimensions} from "../models/screen-dimensions.model";
import {SendKeysRequest} from "../models/send-keys-request.model";
import {Platform} from "../../enums/platform.enum";
import {MobileInspection} from '../../models/mobile-inspection.model';
import {ElementLocatorType} from "../../enums/element-locator-type.enum";
import {AuthUser} from "../../models/auth-user.model";
import {AuthenticationGuard} from "../../shared/guards/authentication.guard";


@Injectable({
  providedIn: 'root'
})
export class DevicesService {
  public localAgentUrl:String = "https://local.testsigmaagent.com:9494";

  constructor(
    private http: HttpClient,
    public authGuard: AuthenticationGuard,
    private httpHeaders: HttpHeadersService) {
  }

  public findAll(agentId: Number, pageable?: Pageable, provisioning?: boolean): Observable<Page<AgentDevice>> {
    pageable || ((pageable = new Pageable()) && (pageable.pageNumber = 0));
    if(provisioning == undefined) {
      provisioning = false;
    }
    return this.http.get<Page<AgentDevice>>(
      "/settings/agent_devices?agentId=" + agentId + "&page=" + pageable.pageNumber+"&provisioned="+provisioning,
      {
        headers: this.httpHeaders.contentTypeApplication,
        params: this.httpHeaders.mapToParams(pageable)
      })
      .pipe(map((data) => {
          return new Page<AgentDevice>().deserialize(data, AgentDevice)
        }),
        catchError((err) => {
          return throwError('Problem while fetching Agent Devices', err)
        })
      );
  }

  public show(device: AgentDevice): Observable<AgentDevice> {
    return this.http.get<AgentDevice>(this.localAgentUrl + "/agent/api/v1/agent_devices/" + device.uniqueId).pipe(
      map((device) => {
        return new AgentDevice().deserialize(device);
      }),
      catchError((err) => {
        return throwError('Problem while fetching Device Availability', err)
      })
    );
  }

  public startMirroring(device: AgentDevice): Observable<MirroringResponse> {
    const data = {
      osName: device.osName, uniqueId: device.uniqueId
    };
    return this.http.post<MirroringResponse>(this.localAgentUrl + "/agent/api/v1/device_mirroring", data).pipe(
        map((res) => {
          return new MirroringResponse().deserialize(res);
        }),
        catchError((err) => {
          return throwError('Problem while inspecting device', err)
        })
      );
  }

  public createSession(sessionCreationRequest: SessionCreationRequest): Observable<SessionResponse> {
    return this.http.post<SessionResponse>(this.localAgentUrl
      + "/agent/api/v1/device_sessions", sessionCreationRequest.serialize()).pipe(
      map((res) => {
        return new SessionResponse().deserialize(res);
      }),
      catchError((err) => {
        return throwError('Problem while creation session', err)
      })
    );
  }

  public startSession(mobileInspection: MobileInspection): Observable<MobileInspection> {
    return this.http.post<MobileInspection>("/mobile_inspections",
      mobileInspection.serialize()).pipe(
      map((res) => {
        return new MobileInspection().deserialize(res);
      }),
      catchError((err) => {
        return throwError(err)
      })
    );
  }

  public updateSession(mobileInspection: MobileInspection): Observable<MobileInspection> {
    return this.http.put<MobileInspection>("/mobile_inspections/" + mobileInspection.id,
      mobileInspection.serialize()).pipe(
      map((res) => {
        return new MobileInspection().deserialize(res);
      }),
      catchError((err) => {
        return throwError('Problem while updating mobile inspection status' + err);
      })
    );
  }

  public deleteSession(sessionId: String): Observable<void> {
    return this.http.delete<void>(this.localAgentUrl + "/agent/api/v1/device_sessions/" + sessionId);
  }

  public getSession(sessionId: String): Observable<SessionResponse> {
    return this.http.get<SessionResponse>(this.localAgentUrl + "/agent/api/v1/device_sessions/" + sessionId).pipe(
      map((res) => {
        return new SessionResponse().deserialize(res);
      }),
      catchError((err) => {
        return throwError('Problem while fetching session', err)
      })
    );
  }

  public deviceSwipe(device: AgentDevice, tapPoints: Position[]): Observable<any> {
    return this.http.post<any>(this.localAgentUrl + "/agent/api/v1/device_actions/" + device.uniqueId + "/swipe"
      , tapPoints)
  }

  public deviceTap(device: AgentDevice, tapPoint: Position): Observable<any> {
    return this.http.post<any>(this.localAgentUrl + "/agent/api/v1/device_actions/" + device.uniqueId + "/tap"
      , tapPoint).pipe(map(data => data), catchError((error) => throwError(error)));
  }

  public navigateBack(device: AgentDevice): Observable<any> {
    return this.http.get<any>(this.localAgentUrl + "/agent/api/v1/device_actions/" + device.uniqueId + "/navigate/back")
  }

  public stopMirroring(device: AgentDevice): Observable<void> {
    return this.http.delete<void>(this.localAgentUrl + "/agent/api/v1/device_mirroring/" + device.uniqueId);
  }

  public getPageSource(sessionId: String, platform: Platform): Observable<MobileElement> {
    return this.http.get<MobileElement>(this.localAgentUrl
      + "/agent/api/v1/session_actions/" + sessionId + "/page_source" + "?platform=" + platform);
  }

  public getScreenshot(Agent, sessionId: String): Observable<String> {
    return this.http.get(this.localAgentUrl + "/agent/api/v1/session_actions/" + sessionId + "/screenshot",
      {responseType: 'text'});
  }

  public getScreenDimensions(sessionId: String): Observable<ScreenDimensions> {
    return this.http.get<ScreenDimensions>(this.localAgentUrl
      + "/agent/api/v1/session_actions/" + sessionId + "/screen_dimensions").pipe(
      map((res) => {
        return new ScreenDimensions().deserialize(res);
      }),
      catchError((err) => {
        return throwError('Problem while fetching screen dimensions', err)
      })
    );
  }

  public sessionSwipe(sessionId: String, tapPoints: Position[]): Observable<any> {
    return this.http.post<any>(this.localAgentUrl + "/agent/api/v1/session_actions/" + sessionId + "/swipe"
      , tapPoints);
  }

  public sessionTap(sessionId: String, tapPoint: Position): Observable<any> {
    return this.http.post<any>(this.localAgentUrl + "/agent/api/v1/session_actions/" + sessionId + "/tap"
      , tapPoint);
  }

  public sendKeys(sessionId: String, sendKeysRequest: SendKeysRequest): Observable<any> {
    return this.http.post<any>(this.localAgentUrl
      + "/agent/api/v1/session_actions/" + sessionId + "/send_keys", sendKeysRequest);
  }

  public clearElement(sessionId: String, mobileElement: MobileElement): Observable<any> {
    return this.http.post<any>(this.localAgentUrl
      + "/agent/api/v1/session_actions/" + sessionId + "/clear_element", mobileElement);
  }

  public tapElement(sessionId: String, mobileElement: MobileElement): Observable<any> {
    return this.http.post<any>(this.localAgentUrl
      + "/agent/api/v1/session_actions/" + sessionId + "/tap_element", mobileElement);
  }

  public sessionNavigateBack(sessionId: String): Observable<any> {
    return this.http.get<any>(this.localAgentUrl + "/agent/api/v1/session_actions/" + sessionId + "/navigate/back");
  }

  public findElements(sessionId: String, platform: Platform, locatorType: ElementLocatorType , byValue): Observable<MobileElement[]> {
    return this.http.get<MobileElement[]>(this.localAgentUrl
      + "/agent/api/v1/session_actions/" + sessionId + "/find_elements"
      + "?platform=" + platform + "&locatorType=" + locatorType + "&byValue=" + encodeURIComponent(byValue) ).pipe(
      map(data => data),
      catchError((error) => throwError(error))
    );
  }

  public goToHome(sessionId: String) {
    return this.http.get<void>(this.localAgentUrl
      + "/agent/api/v1/session_actions/" + sessionId + "/navigate/home").pipe(
      map(data => data),
      catchError((error) => throwError(error))
    );
  }

  public changeOrientation(sessionId: String) {
    return this.http.get<void>(this.localAgentUrl
      + "/agent/api/v1/session_actions/" + sessionId + "/change_orientation").pipe(
      map(data => data),
      catchError((error) => throwError(error))
    );
  }

  public getOrientation(sessionId: String) {
    return this.http.get<any>(this.localAgentUrl
      + "/agent/api/v1/session_actions/" + sessionId + "/get_orientation").pipe(
      map(data => data),
      catchError((error) => throwError(error))
    );
  }

  searchAndTapElement(sessionId: String, platform: Platform, locatorType: ElementLocatorType, byValue: string,
                      index: number, webViewName: string): Observable<void> {
    let params;
    if(webViewName!=null) {
      params = new HttpParams().set("platform", platform)
        .set("byValue", byValue)
        .set("locatorType", locatorType)
        .set("index", index.toString())
        .set("webViewName", webViewName);
    } else {
      params = new HttpParams().set("platform", platform)
        .set("byValue", byValue)
        .set("locatorType", locatorType)
        .set("index", index.toString());
    }
    return this.http.post<void>(this.localAgentUrl + "/agent/api/v1/session_actions/" + sessionId + "/search_and_tap",
      null,
      {
        headers: this.httpHeaders.contentTypeApplication,
        params: params
      });
  }

  searchAndClearElement(sessionId: String, platform: Platform, locatorType: ElementLocatorType, byValue: string,
                        index: number, webViewName: string): Observable<void> {
    let params;
    if(webViewName!=null) {
      params = new HttpParams().set("platform", platform)
        .set("byValue", byValue)
        .set("locatorType", locatorType)
        .set("index", index.toString())
        .set("webViewName", webViewName);
    } else {
      params = new HttpParams().set("platform", platform)
        .set("byValue", byValue)
        .set("locatorType", locatorType)
        .set("index", index.toString());
    }
    return this.http.post<void>(this.localAgentUrl + "/agent/api/v1/session_actions/" + sessionId + "/search_and_clear",
      null,
      {
        headers: this.httpHeaders.contentTypeApplication,
        params: params
      });
  }

  searchAndSendKeys(sessionId: String, platform: Platform, locatorType: ElementLocatorType, byValue: string,
                    index: number, keys: string, webViewName: string): Observable<void> {
    let params;
    if(webViewName!=null) {
      params = new HttpParams().set("platform", platform)
        .set("byValue", byValue)
        .set("locatorType", locatorType)
        .set("index", index.toString())
        .set("keys", keys)
        .set("webViewName", webViewName);
    } else {
      params = new HttpParams().set("platform", platform)
        .set("byValue", byValue)
        .set("locatorType", locatorType)
        .set("keys", keys)
        .set("index", index.toString());
    }
    return this.http.post<void>(this.localAgentUrl + "/agent/api/v1/session_actions/" + sessionId + "/search_and_send_keys",
      null,
      {
        headers: this.httpHeaders.contentTypeApplication,
        params: params
      });
  }

  findByAgentId(agentId: Number): Observable<Page<AgentDevice>>  {
    return this.http.get<Page<AgentDevice>>("/settings/agent_devices?agentId=" + agentId, {headers: this.httpHeaders.contentTypeApplication}).pipe(
      map(data => new Page<AgentDevice>().deserialize(data, AgentDevice)),
      catchError(() => throwError('Problem while getting Agent Device::' + agentId))
    );
  }

  public findUniqueXpath(sessionId: String, platform: Platform, mobileElement:MobileElement): Observable<String> {
    return this.http.post(this.localAgentUrl
      + "/agent/api/v1/session_actions/" + sessionId + "/unique_xpath"
      + "?platform=" + platform , mobileElement, {responseType: 'text'}).pipe(
      map(data => data),
      catchError((error) => throwError(error))
    );
  }
}

