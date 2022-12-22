import {Base} from "./base.model";
import {PageObject} from "./page-object";
import {alias, deserialize, object, optional, serializable} from 'serializr';
import {Integration} from "../enums/integration.enum";
import {IntegrationMetaData} from "./integration-meta-data.model";

export class Integrations extends Base implements PageObject {
  @serializable
  public name: String;
  @serializable
  public username: String;
  @serializable
  public password: String;
  @serializable
  public token: String;
  @serializable
  public workspace: Integration;
  @serializable
  public workspaceId: Number;
  @serializable
  public url: String;
  @serializable(alias('metadata', optional(object(IntegrationMetaData))))
  public metaData: IntegrationMetaData;
  @serializable
  public isAssetsPushEnabled;

  deserialize(input: any): this {
    return Object.assign(this, deserialize(Integrations, input));
  }

  get isMantis(): Boolean {
    return this.workspace == Integration.Mantis;
  }

  get isJira(): Boolean {
    return this.workspace == Integration.Jira;
  }

  get isAzure(): Boolean {
    return this.workspace == Integration.Azure;
  }

  get isYoutrack(): Boolean {
    return this.workspace == Integration.Youtrack;
  }

  get isBugzilla(): Boolean {
    return this.workspace == Integration.BugZilla;
  }

  get isZepel(): Boolean {
    return this.workspace == Integration.Zepel;
  }

  get isFreshrelease(): Boolean {
    return this.workspace == Integration.Freshrelease;
  }

  get isTestsigmaLab(): Boolean {
    return this.workspace == Integration.TestsigmaLab;
  }

  get isXray(): Boolean {
    return this.workspace == Integration.XrayCloud;
  }

  get isPrivateLab(): Boolean {
    return this.workspace == Integration.PrivateGrid;
  }

  get isBackLog(): Boolean {
    return this.workspace == Integration.BackLog;
  }

  get isTrello(): Boolean {
    return this.workspace == Integration.Trello;
  }

  get isLinear(): Boolean {
    return this.workspace == Integration.Linear;
  }

  get isClickUp(): Boolean {
    return this.workspace == Integration.ClickUp;
  }

  get isTestProject():Boolean {
    return this.workspace == Integration.TestProjectImport;
  }
}
