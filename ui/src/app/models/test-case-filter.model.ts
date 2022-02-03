import {custom, deserialize, serializable} from "serializr";
import {FilterQuery} from "./filter-query";
import {Base} from "../shared/models/base.model";
import {PageObject} from "../shared/models/page-object";
import {FilterOperation} from "../enums/filter.operation.enum";
import {TestCaseStatus} from "../enums/test-case-status.enum";
import * as moment from "moment";
import {ResultConstant} from "../enums/result-constant.enum";

export class TestCaseFilter extends Base implements PageObject {
  @serializable
  public id: number;
  @serializable
  public name: String;
  @serializable(custom(v => JSON.stringify(v), v => {
    let filter: FilterQuery[] = [];
    v = JSON.parse(v);
    v.forEach(query => {
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
  @serializable
  public isMappedToTestSuite: Boolean;

  public normalizedQuery: FilterQuery[];

  deserialize(input: any): this {
    return Object.assign(this, deserialize(TestCaseFilter, input));
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

  normalizeCustomQuery(queryHash: string) {
    let normalizedQueryHash: FilterQuery[] = [];
    queryHash.split(",").forEach(query => {
      if (query == '')
        return;
      let normalizedQuery = new FilterQuery();
      if (query.indexOf(":") > 0) {
        normalizedQuery.operation = FilterOperation.EQUALITY;
        normalizedQuery.key = query.split(":")[0];
        normalizedQuery.value = query.split(":")[1];
        if (normalizedQuery.key == "isStepGroup" || normalizedQuery.key == "deleted" ||
            normalizedQuery.key == "suiteMapping") {
          normalizedQuery.value = query.split(":")[1] != 'false';
        } else if (normalizedQuery.key.indexOf("cf_") != 0)
          normalizedQuery.value = <number>Number(query.split(":")[1]);
      } else if (query.indexOf("@") > 0) {
        normalizedQuery.operation = FilterOperation.IN;
        normalizedQuery.key = query.split("@")[0];
        normalizedQuery.value = query.split("@")[1].split("#");
        if (normalizedQuery.key == "status")
          normalizedQuery.value = normalizedQuery.value.map(v => <TestCaseStatus>v);
        else if (normalizedQuery.key == "result")
          normalizedQuery.value = normalizedQuery.value.map(v => <ResultConstant>v);
        else if(normalizedQuery.key.indexOf("cf_") == 0)
          normalizedQuery.value = normalizedQuery.value.map(v => v);
        else
          normalizedQuery.value = normalizedQuery.value.map(v => parseInt(v));
      } else if (query.indexOf(">") > 0) {
        normalizedQuery.operation = FilterOperation.GREATER_THAN;
        normalizedQuery.key = query.split(">")[0];
        normalizedQuery.value = query.split(">")[1];
      } else if (query.indexOf("<") > 0) {
        normalizedQuery.operation = FilterOperation.LESS_THAN;
        normalizedQuery.key = query.split("<")[0];
        normalizedQuery.value = query.split("<")[1];
      }
      normalizedQueryHash.push(normalizedQuery);
    });
    this.normalizedQuery = normalizedQueryHash;
  }

  get isDeleted() {
    return this.name == 'Trash (Deleted Test Cases)';
  }

  get isAllCases() {
    return this.name == 'All Test Cases';
  }

  get queryString() {
    let queryString = "";
    if (this.normalizedQuery.find(query => query.key == "status"))
      queryString += ",status@" + (<TestCaseStatus[]>this.normalizedQuery.find(query => query.key == "status").value).join("#");
    if (this.normalizedQuery.find(query => query.key == "result"))
      queryString += ",result@" + (<ResultConstant[]>this.normalizedQuery.find(query => query.key == "result").value).join("#");
    if (this.normalizedQuery.find(query => query.key == "isStepGroup"))
      queryString += ",isStepGroup:" + <boolean>this.normalizedQuery.find(query => query.key == "isStepGroup").value;
    if (this.normalizedQuery.find(query => query.key == "deleted") || this.isDeleted)
      queryString += ",deleted:" + <boolean>this.normalizedQuery.find(query => query.key == "deleted").value;
    if (this.normalizedQuery.find(query => query.key == "suiteMapping") || this.isMappedToTestSuite)
      queryString += ",suiteMapping:" + <boolean>this.normalizedQuery.find(query => query.key == "suiteMapping").value;
    if (this.normalizedQuery.find(query => query.key == "workspaceVersionId"))
      queryString += ",workspaceVersionId:" + <number>this.normalizedQuery.find(query => query.key == "workspaceVersionId").value;
    if (this.normalizedQuery.find(query => query.key == "priority"))
      queryString += ",priority@" + (<TestCaseStatus[]>this.normalizedQuery.find(query => query.key == "priority").value).join("#");
    if (this.normalizedQuery.find(query => query.key == "type"))
      queryString += ",type@" + (<number[]>this.normalizedQuery.find(query => query.key == "type").value).join("#")
    if (this.normalizedQuery.find(query => query.key == "tagId"))
      queryString += ",tagId@" + (<number[]>this.normalizedQuery.find(query => query.key == "tagId").value).join("#")
    if (this.normalizedQuery.find(query => query.key == "requirementId"))
      queryString += ",requirementId@" + (<number[]>this.normalizedQuery.find(query => query.key == "requirementId").value).join("#");
    if (this.normalizedQuery.find(query => query.key == "createdDate" && query.operation == FilterOperation.LESS_THAN))
      queryString += ",createdDate<" + moment(<number>this.normalizedQuery.find(query => query.key == "createdDate" && query.operation == FilterOperation.LESS_THAN).value).format("YYYY-MM-DD");
    if (this.normalizedQuery.find(query => query.key == "createdDate" && query.operation == FilterOperation.GREATER_THAN))
      queryString += ",createdDate>" + moment(<number>this.normalizedQuery.find(query => query.key == "createdDate" && query.operation == FilterOperation.GREATER_THAN).value).format("YYYY-MM-DD");
    if (this.normalizedQuery.find(query => query.key == "updatedDate" && query.operation == FilterOperation.LESS_THAN))
      queryString += ",updatedDate<" + moment(<number>this.normalizedQuery.find(query => query.key == "updatedDate" && query.operation == FilterOperation.LESS_THAN).value).format("YYYY-MM-DD");
    if (this.normalizedQuery.find(query => query.key == "updatedDate" && query.operation == FilterOperation.GREATER_THAN))
      queryString += ",updatedDate>" + moment(<number>this.normalizedQuery.find(query => query.key == "updatedDate" && query.operation == FilterOperation.GREATER_THAN).value).format("YYYY-MM-DD");
    if(this.normalizedQuery.find(query => query.key.indexOf("cf_") > -1)) {
      this.normalizedQuery.filter(query => query.key.indexOf("cf_") > -1).forEach(filterQuery => {
        if(filterQuery.operation == FilterOperation.IN)
          queryString+=`,${filterQuery.key}@${(<string[]>filterQuery.value).join('#')}`;
        else
          queryString+=`,${filterQuery.key}:${filterQuery.value}`;
      })
    }
    return queryString
  }

}
