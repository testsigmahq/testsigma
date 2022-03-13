import {Base} from "../shared/models/base.model";
import {deserialize, list, object, serializable} from "serializr";
import {AddonTestDataFunctionParameter} from "./addon-test-data-function-parameter.model";
import {PageObject} from "../shared/models/page-object";
import {optional} from "serializr";
import {WorkspaceType} from "../enums/workspace-type.enum";


export class AddonTestDataFunction  extends Base implements PageObject {
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
  @serializable(list(object(AddonTestDataFunctionParameter)))
  public parameters: AddonTestDataFunctionParameter[];

  public isAddon=true;

  public showInfo = false;

  deserialize(input: any): this {
    return Object.assign(this, deserialize(AddonTestDataFunction, input));
  }
}
