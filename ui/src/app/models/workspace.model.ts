import {alias, deserialize, serializable} from 'serializr';
import {WorkspaceType} from "../enums/workspace-type.enum";
import {Base} from "../shared/models/base.model";
import {PageObject} from "../shared/models/page-object";

export class Workspace extends Base implements PageObject {
  @serializable
  public id: number;
  @serializable
  public name: String;
  @serializable
  public description: String;
  @serializable
  public workspaceType: WorkspaceType;
  @serializable(alias('is_demo'))
  public isDemo: Boolean;

  get workspaceTypeNumber(): number {
    switch (this.workspaceType) {
      case WorkspaceType.WebApplication:
        return 1;
      case WorkspaceType.MobileWeb:
        return 2;
      case WorkspaceType.AndroidNative:
        return 3;
      case WorkspaceType.IOSNative:
        return 6;
      case WorkspaceType.Rest:
        return 8;
    }

  }

  deserialize(input: any): this {
    return Object.assign(this, deserialize(Workspace, input));
  }

  get isMobileNative() {
    return this.workspaceType == WorkspaceType.AndroidNative || this.workspaceType == WorkspaceType.IOSNative;
  }

  get isAndroidNative() {
    return this.workspaceType == WorkspaceType.AndroidNative;
  }

  get isIosNative() {
    return this.workspaceType == WorkspaceType.IOSNative;
  }

  get isWeb() {
    return this.workspaceType == WorkspaceType.WebApplication;
  }

  get isMobile() {
    return this.isMobileWeb || this.isMobileNative;
  }

  get isRest() {
    return this.workspaceType == WorkspaceType.Rest;
  }

  get isMobileWeb() {
    return this.workspaceType == WorkspaceType.MobileWeb;
  }

  get isWebMobile() {
    return this.workspaceType == WorkspaceType.MobileWeb || this.workspaceType == WorkspaceType.WebApplication;
  }


}
