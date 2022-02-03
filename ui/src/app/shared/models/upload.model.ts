import {Base} from "./base.model";
import {PageObject} from "./page-object";
import {deserialize, serializable} from 'serializr';
import {UploadType} from "../enums/upload-type.enum";
import {PlatformType} from "../enums/platform-type.enum";
import {UploadStatus} from "../enums/upload-status.enum";

export class Upload extends Base implements PageObject {
  @serializable
  public name: String;
  @serializable
  public appPath: String;
  @serializable
  public fileName: String;
  @serializable
  public type: UploadType;
  @serializable
  public platformType: PlatformType;
  @serializable
  public uploadStatus: UploadStatus;
  @serializable
  public version: String;
  @serializable
  public isPublic: Boolean;
  @serializable
  public comments: String;
  @serializable
  public message: String;
  @serializable
  public fileSize: number;
  @serializable
  public preSignedURL: String;
  @serializable
  public signed: boolean;

  public selected: Boolean;
  public filePathCopied: boolean;
  public isDisabled: boolean;

  deserialize(input: any): this {
    return Object.assign(this, deserialize(Upload, input));
  }

  get sizeInWords() {
    const extensions = ['Bytes', 'KB', 'MB', 'GB'];
    if (this.fileSize == 0) return '0 Byte';
    if (!this.fileSize) return '';
    let i = parseInt(String(Math.floor(Math.log(this.fileSize) / Math.log(1024))));
    return (Math.round((this.fileSize / Math.pow(1024, i))*100)/100) + ' ' + extensions[i];
  }

  get fName(): String {
    let fileParts = this.appPath?.split("/");
    return fileParts ? fileParts[fileParts.length-1] : '';
  }

  get isInProgress() {
    return this.uploadStatus == UploadStatus.InProgress;
  }

  get isCompleted(){
    return this.uploadStatus == UploadStatus.Completed;
  }

  get isFailed(){
    return this.uploadStatus == UploadStatus.Failed;
  }
}
