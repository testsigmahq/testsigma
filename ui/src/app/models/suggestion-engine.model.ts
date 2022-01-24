import {SuggestionResult} from "../enums/suggestion-result.enum";
import {Base} from "../shared/models/base.model";
import {PageObject} from "../shared/models/page-object";
import {alias, custom, deserialize, list, object, serializable} from "serializr";
import {TestStepResult} from "./test-step-result.model";
import {SuggestionEngineMetadata} from "./suggestion-engine-metadata";

export class SuggestionEngine extends Base implements PageObject {
  @serializable
  public id: number;

  @serializable
  public result: SuggestionResult;

  @serializable
  public message: String;

  @serializable(object(SuggestionEngineMetadata))
  public metaData: SuggestionEngineMetadata;

  @serializable
  public stepResult: TestStepResult;

  @serializable
  public suggestionId: Number;

  @serializable
  public stepResultId: Number;

  public title: String;

  deserialize(input: any): this {
    return Object.assign(this, deserialize(SuggestionEngine, input))
  }

  get isPassed() {
    return this.result == SuggestionResult.SUCCESS;
  }

}
