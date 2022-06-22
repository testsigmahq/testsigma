import {Base} from "../shared/models/base.model";
import {PageObject} from "../shared/models/page-object";
import {alias, custom, deserialize, list, object, optional, serializable, SKIP} from "serializr";
import {TestDataSet} from "./test-data-set.model";
import {InfiniteScrollableDataSource} from "../data-sources/infinite-scrollable-data-source";

export class TestData extends Base implements PageObject {
  @serializable
  public id: number;
  @serializable(alias('testDataName'))
  public name: String;
  @serializable(list(object(TestDataSet)))
  public data: TestDataSet[];
  @serializable
  public versionId: number;
  public isSelected: Boolean;
  public parameters?:string[];
  public testDataProfileStepId?:number;
  public startIndex?:number;
  public endIndex?:number;
  public stepDisplayNumber?:string;
  public testCases?:InfiniteScrollableDataSource;
  @serializable(optional(custom(v=> {let jsonObject = {};
  if(v)
    v.forEach((value, key) => {
      jsonObject[key] = value
    }); return jsonObject;}, v=> SKIP)))
  public renamedColumns: Map<String, String>;

  deserialize(input: any): this {
    return Object.assign(this, deserialize(TestData, input))
  }

  deserializeRawValue(rawValue: any): this {
    this.name = rawValue.name;
    this.data = [];
    rawValue.parameterNames.forEach((name, index) => {
    });

    rawValue.dataSets.forEach((dataSet) => {
      let set = new TestDataSet();
      set.name = dataSet.name;
      set.description = dataSet.description;
      set.expectedToFail = dataSet.expectedToFail;
      set.data = JSON.parse("{}");
      dataSet.data.forEach((value, index) => {
        set.data[rawValue.parameterNames[index].trim().replaceAll("\"", "\\\"")] = value.replaceAll("\"", "\\\"");
      })
      this.data.push(set);

    })
    return this;
  }

  setRenamedValues(rawValue: any, oldDataSet: TestDataSet): void {
    this.renamedColumns = new Map<String, String>();
    var oldNames =Object.keys(oldDataSet.data);
    for (var i=0, j=0; i<rawValue.dataWithOrder.length && j<rawValue.parameterNames.length;j++) {
      if(!rawValue.dataWithOrder[j]){
        continue;
      }

      if(rawValue.parameterNames[i]!=oldNames[j]){
        this.renamedColumns.set(oldNames[j], rawValue.parameterNames[i]);
      }
      i++;
    }
  }

}
