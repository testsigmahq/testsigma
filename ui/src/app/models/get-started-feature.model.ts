import {GetStartedTopicModel} from "./get-started-topic.model";
import {GetStartedFeatureType} from "../enums/get-started-feature-type.enum";

export class GetStartedFeatureModel {
  public featureType: GetStartedFeatureType;
  public featureTopics: GetStartedTopicModel[];
}
