import {alias, deserialize, list, object, serializable, serialize,} from "serializr";
import {Deserializable} from "../../shared/models/deserializable";

export class MobileElement implements Deserializable {
  @serializable
  public id: String;
  @serializable
  public uuid: String;
  @serializable
  public name: String;
  @serializable
  public value: String;
  @serializable
  public type: String;
  @serializable
  public xpath: String;
  @serializable
  public enabled: Boolean;
  @serializable
  public visible: Boolean;
  @serializable
  public index: Number;
  @serializable
  public x1: Number;
  @serializable
  public y1: Number;
  @serializable
  public x2: Number;
  @serializable
  public y2: Number;
  @serializable
  public contentDesc: String;
  @serializable
  public resourceId: String;
  @serializable
  public password: Boolean;
  @serializable
  public clickable: Boolean;
  @serializable
  public checked: Boolean;
  @serializable
  public longClickable: Boolean;
  @serializable
  public selected: Boolean;
  @serializable
  public scrollable: Boolean;
  @serializable
  public checkable: Boolean;
  @serializable
  public focusable: Boolean;
  @serializable
  public text: String;
  @serializable
  public packageName: String;
  @serializable
  public label: String;
  @serializable
  public valid: Boolean;
  @serializable
  public depth: Number;
  @serializable
  public accessibilityId: String;
  @serializable(alias('childElements', list(object(MobileElement))))
  public childElements: MobileElement[];
  @serializable
  public webViewName: string;
  @serializable
  public contextNames: String[];
  @serializable
  public attributes: Map<String, String>;

  public parent: MobileElement;
  public hasWebViewChild: boolean;

  public serialize(): JSON {
    return serialize(this);
  }

  deserialize(input: any): this {
    Object.assign(this, deserialize(MobileElement, input));
    console.log(this);
    return this;
  }

}
