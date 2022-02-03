import {Base} from "../shared/models/base.model";
import {deserialize, list, object, serializable} from "serializr";
import {KibbutzTestDataFunctionParameter} from "./kibbutz-test-data-function-parameter.model";
import {PageObject} from "../shared/models/page-object";
import {optional} from "serializr";
import {WorkspaceType} from "../enums/workspace-type.enum";


export class KibbutzTestDataFunction  extends Base implements PageObject {
  @serializable(optional())
  public id: number;
  @serializable
  public displayName: string;
  @serializable
  public fullyQualifiedName: string;
  @serializable
  public description: string;
  @serializable
  public applicationType: WorkspaceType;
  @serializable
  public deprecated:boolean;
  @serializable(optional())
  public addonId: number;
  @serializable
  public externalUniqueId: string;
  @serializable(list(object(KibbutzTestDataFunctionParameter)))
  public parameters: KibbutzTestDataFunctionParameter[];

  public isAddon=true;

  public showInfo = false;

  deserialize(input: any): this {
    return Object.assign(this, deserialize(KibbutzTestDataFunction, input));
  }
}
