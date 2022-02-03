import {Base} from "../../shared/models/base.model";
import {deserialize, serializable} from 'serializr';

export class PlatformBrowser extends Base {
  @serializable
  name: String;

  deserialize(input: any): this {
    return Object.assign(this, deserialize(PlatformBrowser, input));
  }

  get isChrome() {
    return this.name == 'GoogleChrome';
  }

  get isFirefox() {
    return this.name == 'MozillaFirefox';
  }

  get isSafari() {
    return this.name == 'Safari';
  }

  get isEdge() {
    return this.name == 'MicrosoftEdge';
  }

}
