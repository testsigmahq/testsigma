import {deserialize, serializable} from 'serializr';

export class ChromePreference {
  @serializable
  public defaultCType: String;

  deserialize(input: any): this {
    return Object.assign(this, deserialize(ChromePreference, input));
  }
}
