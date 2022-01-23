import {custom, deserialize, serializable} from 'serializr';
import {Base} from "../shared/models/base.model";
import {PageObject} from "../shared/models/page-object";
import {JiraIssueField} from "./jira-issue-field.model";

export class JiraIssueType extends Base implements PageObject {
  @serializable
  public id: number;
  @serializable
  public name: String;
  @serializable
  public description: String;
  @serializable
  public iconUrl: URL;
  @serializable(custom(v => v, v => {
    let returnValue: JiraIssueField[] = [];
    for (let key in v) {
      returnValue.push(new JiraIssueField().deserialize(v[key]));
    }
    return returnValue;
  }))
  public fields: JiraIssueField[];

  deserialize(input: any): this {
    return Object.assign(this, deserialize(JiraIssueType, input));
  }

  get formFields(): JiraIssueField[] {
    let returnResult: JiraIssueField[] = [];
    let tsFormFields = this.fields.filter(field => field.canShowOnTSForm);
    returnResult.push(tsFormFields.find(field => field.isSummaryField));
    returnResult.push(tsFormFields.find(field => field.isDescriptionField));
    returnResult.push(tsFormFields.find(field => field.isPriorityField));
    returnResult.push(tsFormFields.find(field => field.isDueDateField));
    return returnResult.concat(tsFormFields.filter(field => !field.isDueDateField && !field.isPriorityField && !field.isSummaryField && !field.isDescriptionField));
  }


}
