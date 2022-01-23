import {deserialize, serializable} from 'serializr';

export class OnBoardingPreference {
  @serializable
  public challenges: string;
  @serializable
  public email: String;
  @serializable
  public evaluation: String;
  @serializable
  public processes: string;
  @serializable
  public isExperienced: Boolean
  @serializable
  public isAutomationEngineer: Boolean;
  @serializable
  public requirement: String
  @serializable
  public roleType: String
  @serializable
  public skipButton: boolean;
  @serializable
  public supportVia: String;
  @serializable
  public useCaseType: string;
  @serializable
  public useCaseCategory: string;
  @serializable
  public automationTests: string;
  @serializable
  public tests: string;
  @serializable
  public testExecution: string;


  deserialize(input: any): this {
    return Object.assign(this, deserialize(OnBoardingPreference, input));
  }

  public deserializeRawValue(input: JSON): this {

    input['tests'] = this.getUseCaseCategory(input['tests']);
    input['skipButton']= false;
    input['automationTests'] = JSON.stringify((input['automationTests'] || []));
    let returnValue = Object.assign(this, deserialize(OnBoardingPreference, input));
    return returnValue;
  }
  private getUseCaseCategory(useCaseType) {
    let category = null;

    if (useCaseType.includes('mobile_apps') && useCaseType.length > 1) {
      category = 'mobilePlus';
    } else if (useCaseType.includes('web_application') && useCaseType.length > 1) {
      category = 'webPlus';
    } else if (useCaseType.includes('mobile_apps') && useCaseType.length == 1) {
      category = 'onlyMobile';
    } else if (useCaseType.includes('rest_api') && useCaseType.length == 1) {
      category = 'onlyRest';
    } else if (!useCaseType.includes('web_application')) {
      category = 'exceptWeb';
    } else if (useCaseType.includes('web_application')){
      category = 'onlyWeb';
    }
    return category
  }
}
