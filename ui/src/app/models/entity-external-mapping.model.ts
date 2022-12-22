import {custom, deserialize, serializable} from 'serializr';
import {Base} from "../shared/models/base.model";
import {PageObject} from "../shared/models/page-object";
import {Integrations} from "../shared/models/integrations.model";
import {EntityType} from "../enums/entity-type.enum";

export class EntityExternalMapping extends Base implements PageObject {
  @serializable
  public id: number;
  @serializable
  public applicationId: number;
  @serializable
  public entityType: EntityType;
  @serializable
  public entityId: number;
  @serializable
  public externalId: String;
  @serializable
  public linkToExisting: Boolean;
  @serializable
  public message: String
  @serializable
  public pushFailed: Boolean;
  @serializable(custom(v => {
    return v;
  }, v => v))
  public fields: Map<String, Object>;
  @serializable
  public assetsPushFailed: Boolean;

  public rePushInitialized: boolean;

  deserialize(input: any): this {
    return Object.assign(this, deserialize(EntityExternalMapping, input));
  }

  public application: Integrations;

}
