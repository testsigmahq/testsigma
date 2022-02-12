import {custom, deserialize, list, object, primitive, serializable} from "serializr";
import {Base} from "../shared/models/base.model";
import {PageObject} from "../shared/models/page-object";
import {AddonNaturalTextActionParameter} from "./addons-parameter.model";
import {WorkspaceType} from "../enums/workspace-type.enum";
import {AddonParameterType} from "../enums/addon-parameter-type.enum";
import {StepActionType} from "../enums/step-action-type.enum";

export class AddonNaturalTextAction extends Base implements PageObject {
  @serializable
  public naturalText: String;
  @serializable
  public description: string;
  @serializable
  public workspaceType: WorkspaceType;
  @serializable
  public deprecated:boolean;
  @serializable(list(object(AddonNaturalTextActionParameter)))
  public parameters: AddonNaturalTextActionParameter[];
  @serializable(custom(v => v, v => v))
  public stepActionType: StepActionType;

  public isAddon=true;

  deserialize(input: any): this {
    return Object.assign(this, deserialize(AddonNaturalTextAction, input));
  }

  get isConditionalWhileLoop(): Boolean {
    return this.stepActionType == StepActionType.WHILE_LOOP;
  }

  get htmlGrammar(): String {
    let naturalText = this.naturalText;
    if(this.parameters?.length) {
      this.parameters.forEach((parameter:AddonNaturalTextActionParameter) => {
        let referenceName = new RegExp(parameter.reference);
        if(parameter.isElement ){
          naturalText = naturalText.replace(referenceName, '<TSELEMENT ref="'+parameter.reference+'">'+parameter.name+'</TSELEMENT>')
        } else if(parameter.isTestData){
          naturalText = naturalText.replace(referenceName, '<TSTESTDAT ref="'+parameter.reference+'">'+parameter.name+'</TSTESTDAT>')
        } else if(parameter.isTestData && !parameter.allowedValues?.length){
          let selectedListVar = parameter.allowedValues.toString().replace(new RegExp(",", 'g'),'/');
          naturalText = naturalText.replace(referenceName, '<TSTESTDAT ref="'+parameter.reference+'">'+selectedListVar+'</TSTESTDAT>');
        }
      })

      this.parameters.forEach((parameter:AddonNaturalTextActionParameter) => {
        if(parameter.isTestData && !parameter.allowedValues?.length) {
          naturalText = naturalText.replace('<TSTESTDAT ref="'+parameter.reference+'">', '<span class="test_data" data-reference="'+parameter.reference+'">')
        } else if(parameter.isElement) {
          naturalText = naturalText.replace('<TSELEMENT ref="'+parameter.reference+'">', '<span class="element" data-reference="'+parameter.reference+'">')
        }
      })
      // naturalText = naturalText.replace(new RegExp('TS_TE_ST', 'g'), 'test data');
      // naturalText = naturalText.replace(new RegExp('TS_ELE_MENT', 'g'), 'element')
      naturalText = naturalText.replace(new RegExp('</TSTESTDAT>', 'g'), '</span>')
      naturalText = naturalText.replace(new RegExp('</TSELEMENT>', 'g'), '</span>')
    }
    return naturalText;
  }

  get searchableGrammar(): String{
    let naturalText = this.naturalText;
    if(this.parameters?.length) {
      this.parameters.forEach((parameter:AddonNaturalTextActionParameter) => {
        naturalText = naturalText.replace(new RegExp(parameter.reference,"g"), parameter.name)
      })
    }
    return naturalText;
  }
}
