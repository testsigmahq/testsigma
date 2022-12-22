import {Component, ElementRef, EventEmitter, Input, OnInit, Output, ViewChild} from '@angular/core';
import {UploadVersion} from "../../models/upload-version.model";
import {FileHandle} from "../../directives/dragDrop.directive";

@Component({
  selector: 'app-upload-drag-and-drop',
  template: `
      <div class="border-rds-4 d-flex-wrap justify-content-center align-items-center h-100" style="border: 1px dashed #C9CBCE" appDrag (files)="filesDropped($event)">
        <div>
        <div class="d-flex fz-20 justify-content-center ts-col-100">
          <i
            [class.fa-cloud_upload_waiting]="!isValidUpload"
            [class.fa-cloud_done]="isValidUpload"
            class="bg-highlight d-block line-height-none px-12 py-20 rounded-circle text-brand"></i>
        </div>
        <div class="d-flex ts-col-100 align-items-center justify-content-center py-8 fz-14"
             *ngIf="!uploadedFileObject">
          <div [translate]="'upload_path_suggestion.drag_drop'"></div>
          <form action="">
            <input type="file" #fileInput class="hide" (change)="uploadedFile($event)" [accept]="acceptedFileTypes" />
          </form>
          <div
            (click)="inputClick()"
            [translate]="'upload_path_suggestion.browse'"
            class="text-underline text-brand ml-8 pointer rb-semi-medium"></div>
        </div>
        <div class="d-flex ts-col-100 align-items-center justify-content-center py-8 fz-14"
             *ngIf="uploadedFileObject">
          <span [textContent]="uploadedFileObject?.name+ ' ( size : ' + fileSizeInWord(uploadedFileObject?.size)+ ' ) '"></span>
          <div class="ml-8 text-danger text-underline ml-8 pointer rb-semi-medium"
               [translate]="'upload_path_suggestion.remove'" (click)="removeUpload()"></div>
        </div>
        <div *ngIf = "canShowMaxSizeErrorMessage"
             [translate]="'upload_path_suggestion.max_size'" class="d-flex justify-content-center"
             style="color: #647488"></div>
      </div>
      </div>
  `,
  styles: [
  ]
})
export class UploadDragAndDropComponent implements OnInit {

  @Input() canShowMaxSizeErrorMessage : boolean = true;
  @Input() isValidUpload : boolean = false;
  //comma seperated string with accepted types. if none given, will take */*
  @Input() acceptedFileTypes : string = "*/*";
  @Output() onFileUploadEvent : EventEmitter<any> = new EventEmitter<any>();
  @Output() onFileRemoveEvent : EventEmitter<any> = new EventEmitter<any>();
  @ViewChild('fileInput') fileInput: ElementRef;


  public uploadedFileObject;

  constructor() { }

  ngOnInit(): void {

  }

  filesDropped(files: FileHandle[]): void {
    this.uploadedFile(files[0].file, true);
  }

  public removeUpload() {
    this.uploadedFileObject = null;
    this.isValidUpload = false;
    this.onFileRemoveEvent.emit();
  }

  public uploadedFile(event, isDragAndDrop ?: boolean): void {
    if(isDragAndDrop)
      this.uploadedFileObject = event;
    else
      this.uploadedFileObject = event.target.files ? event.target.files[0] : null;
    this.isValidUpload = true;
    this.onFileUploadEvent.emit(this.uploadedFileObject);
  }

  fileSizeInWord(size:number) {
    const extensions = ['Bytes', 'KB', 'MB', 'GB'];
    if (size == 0) return '0 Byte';
    if (!size) return '';
    let i = parseInt(String(Math.floor(Math.log(size) / Math.log(1024))));
    return (Math.round((size / Math.pow(1024, i))*100)/100) + ' ' + extensions[i];
  }

  inputClick() {
    this.fileInput?.nativeElement?.click()
  }

}
