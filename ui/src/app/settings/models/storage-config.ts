import {Base} from "../../shared/models/base.model";
import {OnInit} from "@angular/core";
import {deserialize, optional, primitive, serializable, serialize} from "serializr";
import {PageObject} from "../../shared/models/page-object";
import {StorageType} from "../../enums/storage-type.enum";

export class StorageConfig extends Base implements PageObject{

  @serializable(primitive())
  public id: number;
  @serializable(primitive())
  public storageType:StorageType;
  @serializable(primitive())
  public awsBucketName: string;
  @serializable(primitive())
  public awsRegion:string;
  @serializable(primitive())
  public awsEndpoint:string;
  @serializable(primitive())
  public awsAccessKey:string;
  @serializable(primitive())
  public awsSecretKey:string;

  @serializable(primitive())
  public azureContainerName:string;
  @serializable(primitive())
  public azureConnectionString:string;

  @serializable(primitive())
  public onPremiseRootDirectory:string;

  deserialize(input: any): this {
    return Object.assign(this, deserialize(StorageConfig, input))
  }
}
