import {deserialize, object, serializable, alias, custom, SKIP} from 'serializr';
import {Workspace} from "./workspace.model";
import {Base} from "../shared/models/base.model";
import {PageObject} from "../shared/models/page-object";

export class WorkspaceVersion extends Base implements PageObject {
  @serializable
  public id: number;
  @serializable
  public workspaceId: number;
  @serializable
  public versionName: String;
  @serializable
  public description: String;
  @serializable(custom(v=>{
    if(v)
      return v.serialize();
  }, v => {
    if(v)
      return new Workspace().deserialize(v);
  }))
  public workspace: Workspace;

  // @ts-ignore
  get name() {
    return this.versionName;
  }

  deserialize(input: any): this {
    return Object.assign(this, deserialize(WorkspaceVersion, input));
  }

}
