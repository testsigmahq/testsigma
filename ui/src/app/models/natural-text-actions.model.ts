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
  public allowedValues: Map<string, string[]>;
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
    naturalText = this.testDataReplacer(naturalText, this.allowedValues)
    if (naturalText.match(/#{elementnameTo}/g) || naturalText.match(/#{from-element}/g)) {
      naturalText = naturalText.replace(/#{elementname}|#{from-element}/g, '<span class="element">from element</span>');
      naturalText = naturalText.replace(/#{elementnameTo}|#{to-element}/g, '<span class="element">to element</span>');
    }
    naturalText = naturalText.replace(/#{elementname}/g, '<span class="element">element</span>')
      .replace(/#{element}/g, '<span class="element">element</span>')
      .replace(/@{attribute}/g, '<span class="attribute">attribute</span>');
    return naturalText;
  }

  testDataReplacer(grammar: String, allowedValues: Map<string,string[]>) {
    let dataList = this.data?.testData ? Object.keys(this.data?.testData) : [];
    let testDataList = grammar.match(/\$\{([^}]+)\}/g);
    if(testDataList?.length)
      testDataList.forEach(
        (item, index) => {
          let testDataReplaced = item.replace(/<em +(.*?)>/g, 'HIGH_LIGHT_START').
          replace(/<\/em>/g, 'HIGH_LIGHT_END').
          replace(/[&#,+()$~%.'":*?<>{}]/g,"");
          let parameter = item.replace(/<em +(.*?)>/g, '').
          replace(/<\/em>/g, '').
          replace(/[&#,+()$~%'":*?<>{}]/g,"");
          let span_class= allowedValues?.[parameter]?.length ? 'selected_list':'test_data';
          grammar = grammar.replace(new RegExp(item.replace(/\$/g, "\\$").
            replace(/\{/g, "\\{").
            replace(/\}/g, "\\}")),
            "<span class='"+(span_class+' test_data')+"' data-reference='"+parameter+"'>"+(this.data?.testData?.[parameter] ? (this.data?.testData?.[parameter].replace(/HIGH_LIGHT_START/g, '<em class="nlp-highlight">').replace(/HIGH_LIGHT_END/g, '</em>')) : '' )+"</span>");
        })
    if(dataList?.length) {
      dataList.forEach(parameter => {
        let rejext = new RegExp("\\$\\{"+parameter+"\\}");
        let span_class= allowedValues?.[parameter]?.length ? 'selected_list':'test_data';
        grammar = grammar.replace(rejext, "<span class='"+(span_class +' test_data')+"' data-reference='"+parameter+"'>"+this.data?.testData?.[parameter]+"</span>");
      })
    }
    return grammar;
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
