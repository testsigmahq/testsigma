import {Base} from "../shared/models/base.model";
import {PageObject} from "../shared/models/page-object";
import {alias, custom, deserialize, object, optional, serializable} from "serializr";
import {RestCompareType} from "../enums/rest-compare-type.enum";
import {RestMethod} from "../enums/rest-method.enum";
import {RestAuthorization} from "../enums/rest-authorization.enum";
import {RestStepAuthorizationValue} from "./rest-step-authorization-value.model";

export class RestStepEntity extends Base implements PageObject {
  @serializable
  public url: String;
  @serializable
  public method: RestMethod;
  @serializable(custom(v => v, v => v))
  public requestHeaders: JSON;
  @serializable(custom(v => v, v => v))
  public payload: JSON;
  @serializable
  public expectedResultType: String;
  @serializable
  public status: Number;
  @serializable(custom(v => v, v => v))
  public responseHeaders: JSON;
  @serializable
  public responseCompareType: RestCompareType;
  @serializable
  public response: string;
  @serializable
  public stepId: Number;
  @serializable
  public storeMetadata: Boolean;
  @serializable(custom(v => v, v => v))
  public headerRuntimeData: JSON;
  @serializable(custom(v => v, v => v))
  public bodyRuntimeData: JSON;
  @serializable
  public followRedirects: Boolean;
  @serializable(custom(v => {
    if (v == RestAuthorization.NONE)
      return 0;
    else if (v == RestAuthorization.BASIC)
      return 1;
    else if(v == RestAuthorization.BEARER)
      return 2;
    else
      return v;
  }, v => v))
  public authorizationType: RestAuthorization;
  @serializable(optional(object(RestStepAuthorizationValue)))
  public authorizationValue: RestStepAuthorizationValue;

  deserialize(input: any): this {
    return Object.assign(this, deserialize(RestStepEntity, input))
  }

  deserializeRawValue(input: JSON): this {
    input['requestHeaders'] = this.convertToJSON((input['requestHeaders'] || []));
    input['responseHeaders'] = this.convertToJSON((input['responseHeaders'] || []));
    if (input['responseCompareType'] == RestCompareType.JSON_PATH) {
      input['response'] = JSON.stringify(this.convertJSONObjectFromString((input['responseBodyJson'] || [])));
    }
    input['headerRuntimeData'] = this.convertToJSON((input['headerRuntimeData'] || []));
    input['bodyRuntimeData'] = this.convertToJSON((input['bodyRuntimeData'] || []));
    if(!input['authorizationValue'])
      input['authorizationValue'] = {};
    input['authorizationValue']['Bearertoken'] = input['authorizationValue']['token'];
    input['expectedResponseStatus'] = input['status'];
    input['storeMetadata'] = input['storeMetadata']!= undefined ? input['storeMetadata'] : true;
    let returnValue = Object.assign(this, deserialize(RestStepEntity, input));
    if (returnValue.status)
      this.expectedResultType = "1"
    if (returnValue.responseHeaders && Object.keys(returnValue.responseHeaders).length > 0)
      this.expectedResultType = this.expectedResultType ? this.expectedResultType + ",2" : "2";
    if (returnValue.response)
      this.expectedResultType = this.expectedResultType ? this.expectedResultType + ",3" : "3";
    if (returnValue.status && returnValue.responseHeaders && Object.keys(returnValue.responseHeaders).length > 0 && returnValue.response) {
      this.expectedResultType += ",4"
    }
    return returnValue;
  }

  serializeRawValueForDryAPICall(input) {
    let returnValue = {};
    returnValue["url"] = encodeURI(input["url"])  ;
    returnValue["request_method"] = input["method"];
    returnValue["request_headers"] = input["requestHeaders"];
    returnValue["follow_redirects"] = input["followRedirects"];
    returnValue["authorizationType"] = input["authorizationType"];
    returnValue["authorizationValue"] = JSON.stringify(input["authorizationValue"]);
    returnValue["payload"] = JSON.parse(input["payload"]);
    return returnValue;
  }

  public convertToJSON(json: Array<any>): any {
    let returnValue = {};
    (json || []).forEach(header => {
      if (header.key != '' && header.value != '')
        returnValue[header.key] = header.value;
    });
    return <JSON>returnValue;
  }
  public convertToJSONArray(json: any): any {
    let returnValue = Array<JSON>();
    Object.keys(json || []).forEach(header=> {
      let param = JSON.parse('{}');
      param['key'] = header;
      param['value'] =  typeof json[header] == 'string' ? json[header]: JSON.stringify(json[header]);
      returnValue.push(param)
    });
    return returnValue;
  }
  public convertToJSONObject(json: any): any {
    let returnValue:any = JSON.parse('{}');
    Object.keys(json || {}).forEach(header => {
      if (header!= '' && json[header] != '' &&  typeof json[header] != 'string')
        returnValue[header] = JSON.stringify(json[header]);
      else
        returnValue[header] = json[header];
    });
    return returnValue;
  }
  public convertJSONObjectFromString(json: any): any {
    let returnValue:any = JSON.parse('{}');
    if(json)
     json.forEach((header, index) => {
      if (header!= '' && header['key'] != '')
        if(typeof header['value'] == 'string' && header['value'].trim().startsWith("{")) {
          returnValue[header['key']] = JSON.parse(header['value']);
        }else{
          returnValue[header['key']] = header['value'];
      }
    });
    return returnValue;
  }
  public convertJSONObjectFromStr(json: any): any {
    let returnValue:any = JSON.parse('{}');
    Object.keys(json || {}).forEach((header, index) => {
      if (header!= '' && json[header] != ''){
        if(typeof json[header] == 'string' && json[header] .trim().startsWith("{"))
          returnValue[header] = JSON.parse(json[header]);
        else {
          returnValue[header] = json[header];
        }
      }
    });
    return returnValue;
  }
}
