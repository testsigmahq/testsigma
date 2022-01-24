import {Base} from "../../shared/models/base.model";
import {deserialize, serializable} from 'serializr';

export class Platform extends Base {
  @serializable
  name: String;

  deserialize(input: any): this {
    return Object.assign(this, deserialize(Platform, input));
  }

  get isWindows() {
    return this.name == "Windows";
  }

  get isLinux() {
    return this.name == "Linux";
  }

  get isMac() {
    return this.name == "Mac";
  }

  get isAndroid() {
    return this.name == "Android";
  }

  get isIOS() {
    return this.name == "iOS";
  }
}
