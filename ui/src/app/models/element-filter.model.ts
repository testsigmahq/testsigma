import {custom, deserialize, serializable} from 'serializr';
import {Base} from "../shared/models/base.model";
import {PageObject} from "../shared/models/page-object";
import {FilterQuery} from "./filter-query";
import {FilterOperation} from "../enums/filter.operation.enum";
import * as moment from "moment";
function byPassSpecialCharacters(definition) {
  return definition.replace(/\*/,"ts_asterisk")
    .replace(/,/,"ts_comma")
    .replace(/!/g, 'ts_negation')
    .replace(/~/g, 'ts_like')
    .replace(/:/g, 'ts_colon')
    .replace(/;/g, 'ts_semicolon')
    .replace(/>/g, 'ts_greater_than')
    .replace(/</g, 'ts_lesser_than')
    .replace(/@/g, 'ts_at_sign')
    .replace(/\$/g, 'ts_dollar_sign');
}

export class ElementFilter extends Base implements PageObject {
  @serializable
  public id: number;
  @serializable
  public name: String;
  @serializable(custom(v => JSON.stringify(v), v => {
    let filter: FilterQuery[] = [];
    v = JSON.parse(v);
    v.forEach(query => {
      if(query["key"] == "locatorValue"){
        query.value = "*" +
          byPassSpecialCharacters(query.value.slice(1, query.value.length-1))
          + "*";
      }
      filter.push(new FilterQuery().deserialize(query));
    });
    return filter;
  }))
  public queryHash: FilterQuery[];
  @serializable
  public versionId: Number;
  @serializable
  public isPublic: Boolean;
  @serializable
  public isDefault: Boolean;


  public normalizedQuery: FilterQuery[];

  private normaliseCurrentValue(value, currentValue) {
    let returnValue = value;
    if (value instanceof Array) {
      returnValue = (<number[]>value).map(value => {
        if (value == -1)
          return currentValue;
        else
          return value;
      });
    } else if (value == "-1") {
      returnValue = currentValue;
    }
    return returnValue;
  }

  normalizeQuery(versionId: number): void {
    let normalizedQueryHash: FilterQuery[] = [];
    this.queryHash.forEach((query) => {
      let newQuery = new FilterQuery();
      newQuery.key = query.key;
      newQuery.operation = query.operation;
      if (query.key == 'workspaceVersionId') {
        query.value = this.normaliseCurrentValue(query.value, versionId);
      }
      newQuery.value = query.value;
      normalizedQueryHash.push(newQuery);
    });
    this.normalizedQuery = normalizedQueryHash;
  }

  normalizeCustomQuery(queryHash: string) {
    let normalizedQueryHash: FilterQuery[] = [];
    queryHash.split(",").forEach(query => {
      let normalizedQuery = new FilterQuery(), separator, secondSeparator, operation;
      if (query == '') return;
      if (query.indexOf(":") > 0) {
        separator = ":"; operation = FilterOperation.EQUALITY;
      } else if (query.indexOf(">") > 0) {
      separator = ">"; operation = FilterOperation.GREATER_THAN;
      } else if (query.indexOf("<") > 0) {
      separator = "<"; operation = FilterOperation.LESS_THAN;
      } else if (query.indexOf("@") > 0) {
        separator = "@"; secondSeparator = "#"; operation = FilterOperation.IN;
      }
      normalizedQuery.operation = operation;
      normalizedQuery.key = query.split(separator)[0];

      if (secondSeparator) {
        normalizedQuery.value = query.split(separator)[1].split(secondSeparator);
        if (normalizedQuery.key != "locatorType")
          normalizedQuery.value = normalizedQuery.value.map(v => parseInt(v));
      } else {
        normalizedQuery.value = query.split(separator)[1];
      }
      if (normalizedQuery.key == "locatorValue")
          normalizedQuery.value = decodeURIComponent(<string>normalizedQuery.value);

      normalizedQueryHash.push(normalizedQuery);
    });
    this.normalizedQuery = normalizedQueryHash;
  }

  get queryString() {
    let queryString = "";
    if (this.normalizedQuery.find(query => query.key == "workspaceVersionId"))
      queryString += ",workspaceVersionId:" + <number>this.normalizedQuery.find(query => query.key == "workspaceVersionId").value;
    if (this.normalizedQuery.find(query => query.key == "name"))
      queryString += ",name:" + this.normalizedQuery.find(query => query.key == "name").value
    if (this.normalizedQuery.find(query => query.key == "type"))
      queryString += ",type@" + (<number[]>this.normalizedQuery.find(query => query.key == "type").value).join("#")
    if (this.normalizedQuery.find(query => query.key == "screenName"))
      queryString += ",screenName:" + this.normalizedQuery.find(query => query.key == "screenName").value
    if (this.normalizedQuery.find(query => query.key == "createdDate" && query.operation == FilterOperation.LESS_THAN))
      queryString += ",createdDate<" + moment(<number>this.normalizedQuery.find(query => query.key == "createdDate" && query.operation == FilterOperation.LESS_THAN).value).format("YYYY-MM-DD");
    if (this.normalizedQuery.find(query => query.key == "createdDate" && query.operation == FilterOperation.GREATER_THAN))
      queryString += ",createdDate>" + moment(<number>this.normalizedQuery.find(query => query.key == "createdDate" && query.operation == FilterOperation.GREATER_THAN).value).format("YYYY-MM-DD");
    if (this.normalizedQuery.find(query => query.key == "updatedDate" && query.operation == FilterOperation.LESS_THAN))
      queryString += ",updatedDate<" + moment(<number>this.normalizedQuery.find(query => query.key == "updatedDate" && query.operation == FilterOperation.LESS_THAN).value).format("YYYY-MM-DD");
    if (this.normalizedQuery.find(query => query.key == "updatedDate" && query.operation == FilterOperation.GREATER_THAN))
      queryString += ",updatedDate>" + moment(<number>this.normalizedQuery.find(query => query.key == "updatedDate" && query.operation == FilterOperation.GREATER_THAN).value).format("YYYY-MM-DD");
    if (this.normalizedQuery.find(query => query.key == "locatorValue")) {
      queryString += ",locatorValue:" +
        byPassSpecialCharacters(this.normalizedQuery.find(query => query.key == "locatorValue").value.toString())
        + "*";
    }
    if (this.normalizedQuery.find(query => query.key == "tagId"))
      queryString += ",tagId@" + (<number[]>this.normalizedQuery.find(query => query.key == "tagId").value).join("#")
    if (this.normalizedQuery.find(query => query.key == "isUsed"))
      queryString += ",isUsed:" + this.normalizedQuery.find(query => query.key == "isUsed").value;
    return queryString;
  }

  byPassSpecialCharacters(definition){
    return byPassSpecialCharacters(definition);
  }

  deserialize(input: any): this {
    return Object.assign(this, deserialize(ElementFilter, input));
  }

}
