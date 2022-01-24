import {alias, custom, deserialize, list, object, optional, serializable, SKIP} from 'serializr';
import {Base} from "../shared/models/base.model";
import {PageObject} from "../shared/models/page-object";
import {ChromeRecorderDependentElement} from "./chrome-recorder-dependent-element.model";

export class ElementMetaData extends Base implements PageObject {
  @serializable(optional())
  public xPath: string;
  @serializable(optional(list(object(ChromeRecorderDependentElement))))
  public parents: ChromeRecorderDependentElement[];
  @serializable(alias('current-element', custom(v => v , v => {
    if(v && typeof v == 'string')
      return JSON.parse(v);
    else if(v)
      return v;
  })))
  public currentElement: JSON;
  @serializable(alias('following-sibling', optional(list(object(ChromeRecorderDependentElement)))))
  public followingSiblings: ChromeRecorderDependentElement[];
  @serializable(alias('preceding-sibling', optional(list(object(ChromeRecorderDependentElement)))))
  public precedingSiblings: ChromeRecorderDependentElement[];
  @serializable(alias('childs_first_level', optional(list(object(ChromeRecorderDependentElement)))))
  public firstLevelChildren: ChromeRecorderDependentElement[];
  @serializable(alias('childs_second_level', optional(list(object(ChromeRecorderDependentElement)))))
  public secondLevelChildren: ChromeRecorderDependentElement[];
  @serializable(alias('testdata',optional(custom(v => v, v => SKIP))))
  public testData: JSON;

  deserialize(input: any): this {
    return Object.assign(this, deserialize(ElementMetaData, input));
  }
}
