import {Base} from "./base.model";
import {PageObject} from "./page-object";
import {deserialize, serializable} from 'serializr';
import {UploadType} from "../enums/upload-type.enum";
import {PlatformType} from "../enums/platform-type.enum";
import {UploadStatus} from "../enums/upload-status.enum";
import {UploadVersion} from "./upload-version.model";

export class Upload extends Base implements PageObject {
  @serializable
  public name: String;

  @serializable
  public latestVersionId: number;

  public selected: Boolean;
  public filePathCopied: boolean;
  public isDisabled: boolean;
  public latestVersion: UploadVersion;

  deserialize(input: any): this {
    return Object.assign(this, deserialize(Upload, input));
  }

  get sizeInWords() {
    return this.latestVersion?.sizeInWords;
  }

  get fName(): String {
    return this.latestVersion?.path;
  }

  get isInProgress() {
    return this.latestVersion?.isInProgress;  }

  get isCompleted(){
    return this.latestVersion?.isCompleted;
  }

  get signed() {
    return this.latestVersion?.signed;
  }
}
