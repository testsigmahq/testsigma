import {custom, deserialize, serializable} from 'serializr';
import {JiraFieldAllowedValue} from "./jira-field-allowed-value.model";
import {Deserializable} from "../shared/models/deserializable";

export class JiraIssueField implements Deserializable {
  @serializable
  public id: String;
  @serializable
  public name: String;
  @serializable
  public required: Boolean;
  @serializable
  public hasDefaultValue: Boolean;
  @serializable
  public autoCompleteUrl: URL;
  @serializable(custom(v => v, v => v))
  public schema: Map<String, String>;
  @serializable
  public key: String;
  @serializable(custom(v => v, v => {
    let returnValue: JiraFieldAllowedValue[] = [];
    (v || []).forEach(value => returnValue.push(new JiraFieldAllowedValue().deserialize(value)));
    return returnValue;
  }))
  public allowedValues: JiraFieldAllowedValue[]

  deserialize(input: any): this {
    return Object.assign(this, deserialize(JiraIssueField, input));
  }

  get isSystemField(): Boolean {
    return !!this.schema['system'];
  }

  get isSummaryField(): Boolean {
    return this.key == "summary";
  }

  get isDescriptionField(): Boolean {
    return this.key == "description";
  }

  get isPriorityField(): Boolean {
    return this.key == "priority";
  }

  get isDueDateField(): Boolean {
    return this.key == "duedate";
  }

  get isCustomField(): Boolean {
    return this.key.includes("customfield_");
  }

  get isCustomTextField(): Boolean {
    return this.schema['custom'] && this.schema['custom'].includes("text");
  }

  get isCustomTextAreaField(): Boolean {
    return this.schema['custom'] && this.schema['custom'].includes("textarea");
  }

  get isCustomSelect(): Boolean {
    return this.schema['custom'] && this.schema['custom'].includes("select");
  }

  get isCustomMultiSelect(): Boolean {
    return this.schema['custom'] && this.schema['custom'].includes("multiselect");
  }

  get isCustomRadio(): Boolean {
    return this.schema['custom'] && this.schema['custom'].includes("radiobuttons");
  }

  get isCustomMultiCheckbox(): Boolean {
    return this.schema['custom'] && this.schema['custom'].includes("multicheckboxes");
  }

  get isCustomNumberField(): Boolean {
    return this.schema['custom'] && this.schema['custom'].includes("float");
  }

  get isCustomURLField(): Boolean {
    return this.schema['custom'] && this.schema['custom'].includes("url");
  }

  get canShowOnTSForm(): Boolean {
    return this.isSummaryField || this.isDescriptionField || this.isPriorityField || this.isDueDateField
      || (this.isCustomField && (this.isCustomURLField || this.isCustomNumberField || this.isCustomTextField || this.isCustomTextAreaField || this.allowedValues.length > 0))
  }

  get type(): String {
    if (this.isDescriptionField || this.isCustomTextAreaField)
      return 'textarea';
    else if (this.isSummaryField || this.isCustomTextField)
      return 'text';
    else if (this.isCustomNumberField)
      return 'number';
    else if (this.isCustomURLField)
      return 'url';
    else if (this.isPriorityField)
      return 'priority';
    else if (this.isCustomSelect || this.isCustomMultiSelect || this.isPriorityField)
      return 'dropdown';
    else if (this.isCustomMultiCheckbox)
      return 'multicheckbox';
    else if (this.isCustomRadio)
      return 'radio';
    else if (this.isDueDateField)
      return 'date';
  }


}
