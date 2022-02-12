/*
 *
 *  * *****************************************************************************
 *  *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  *  All rights reserved.
 *  *  ****************************************************************************
 *
 */

import {Base} from "../shared/models/base.model";
import {PageObject} from "../shared/models/page-object";
import {custom, deserialize, object, serializable} from 'serializr';
import {WorkspaceType} from "../enums/workspace-type.enum";
import {NaturalTextActionData} from "./natural-text-action-data.model";
import {TestStepConditionType} from "../enums/test-step-condition-type.enum";
import {StepActionType} from "../enums/step-action-type.enum";

export class NaturalTextActions extends Base implements PageObject {

  @serializable
  public workspaceType: WorkspaceType;
  @serializable
  public naturalText: String;
  @serializable
  public displayName: String;
  @serializable
  public action: String;
  @serializable(custom(v => v, v => v))
  public allowedValues: string[];
  @serializable(object(NaturalTextActionData))
  public data: NaturalTextActionData;
  @serializable(custom(v => v, v => v))
  public stepActionType: StepActionType;

  get isConditionalWhileLoop(): Boolean {
    return this.stepActionType == StepActionType.WHILE_LOOP;
  }

  public displayOrder: number;
  public isAddon: boolean =false;

  deserialize(input: any): this {
    return Object.assign(this, deserialize(NaturalTextActions, input));
  }

  get extractTestDataString():string {
    let testDataVar = this.naturalText.match(/\$\{([^}]+)\}/g);
    if (testDataVar!=null) return testDataVar[0].replace(/[&#,+()$~%.'":*?<>{}]/g,"");
    else return "";
  }
  get htmlGrammar(): String {
    let naturalText = this.naturalText;
    if (naturalText.match(/#{elementnameTo}/g) || naturalText.match(/#{from-element}/g)) {
      naturalText = naturalText.replace(/#{elementname}|#{from-element}/g, '<span class="element">from element</span>');
      naturalText = naturalText.replace(/#{elementnameTo}|#{to-element}/g, '<span class="element">to element</span>');
    }
    let span_class= this.allowedValues?'selected_list':'test_data';
    naturalText = naturalText.replace(/#{elementname}/g, '<span class="element">element</span>')
      .replace(/#{element}/g, '<span class="element">element</span>')
      .replace(/\$\{([^}]+)\}/g, "<span class="+span_class+">"+this.extractTestDataString+"</span>")
      .replace(/@{attribute}/g, '<span class="attribute">attribute</span>');
    return naturalText;
  }

  get searchableGrammar(): string{
    return this.naturalText
      .replace(/#{elementname}/g, "Element")
      .replace(/#{elementnameTo}|#{to-element}/g, "Element")
      .replace(/#{from-element}/g, "Element")
      .replace(/#{element}/g, 'Element')
      .replace(/\$\{([^}]+)\}/g, this.extractTestDataString)
      .replace(/\${testdata}/g, this.extractTestDataString)
      .replace(/@{attribute}/g, "attribute");
  }

  actionText(element?:string, testData?: string){
    return this.naturalText
      .replace(/#{elementname}/g, element)
      .replace(/#{elementnameTo}|#{to-element}/g, element)
      .replace(/#{from-element}/g, element)
      .replace(/#{element}/g, element)
      .replace(/\$\{([^}]+)\}/g, testData )
      .replace(/\${testdata}/g, testData );
  }

}
