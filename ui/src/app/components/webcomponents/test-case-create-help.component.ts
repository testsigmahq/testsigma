import {Component, Input, OnInit} from '@angular/core';
import {TestCase} from "../../models/test-case.model";
import {WorkspaceVersion} from "../../models/workspace-version.model";
import {WorkspaceType} from "../../enums/workspace-type.enum";

@Component({
  selector: 'app-test-case-create-help',
  template: `
    <div class="fa-help fz-24"></div>
    <span class="small-sub-header rb-medium" [textContent]="('test_case.create_help.description.label'|translate) +':'"></span>
    <p [translate]="'test_case.create_help.description.details'"></p>
    <div *ngIf="detailsShowed">
      <span
        *ngIf="!testCase.isStepGroup">
        <span class="small-sub-header rb-medium" [textContent]="('test_case.create_help.priority.label'|translate) +':'"></span>
        <p [translate]="'test_case.create_help.priority.details'"></p>
    </span>
      <span *ngIf="!testCase.isStepGroup">
        <span class="small-sub-header rb-medium" [textContent]="('test_case.create_help.type.label'|translate) +':'"></span>
        &nbsp;
        <p [translate]="'test_case.create_help.type.details'"></p>
    </span>
      <span class="small-sub-header rb-medium" [textContent]="('test_case.create_help.status.label'|translate) +':'"></span>
      <p><span [translate]="'test_case.create_help.status.details'"></span><br>
      <span class="small-sub-header" [translate]="'message.common.note'"></span>
      <span [translate]="'test_case.create_help.status.note'"></span></p>
      <span *ngIf="!testCase.isStepGroup">
        <span class="small-sub-header rb-medium" [textContent]="('test_case.create_help.prerequisite.label'|translate) +':'"></span>
        <p [translate]="'test_case.create_help.prerequisite.details'"></p>
    </span>
      <span *ngIf="!testCase.isStepGroup">
        <span class="small-sub-header rb-medium" [textContent]="('test_case.create_help.test_data.label'|translate) +':'"></span>
        <p>
          <span [translate]="'test_case.create_help.test_data.details'"></span><br>
          <span [translate]="'message.common.refer_this'"></span>
          &nbsp;
          <a
            class="small-sub-header rb-medium text-link"
            rel="noreferrer nofollow"
            href="https://testsigma.com/tutorials/test-cases/data-driven-testing/"
            target="_blank" [translate]="'message.common.help_document'"></a>
          &nbsp;
          <span [translate]="'test_case.create_help.test_data.help_document_description'"></span> for more details on Data-driven Test Cases.</p>
        <span class="small-sub-header rb-medium" [textContent]="('test_case.create_help.data_driven.label'|translate) +':'"></span>
        <p>
          <span [translate]="'test_case.create_help.data_driven.details'"></span><br>
          <span [translate]="'message.common.refer_this'"></span>&nbsp;
          <a
            class="small-sub-header rb-medium text-link"
            rel="noreferrer nofollow"
            href="https://testsigma.com/tutorials/test-cases/data-driven-testing/"
            target="_blank" [translate]="'message.common.help_document'"></a>
            &nbsp;
          <span [translate]="'test_case.create_help.data_driven.help_document_description'"></span></p>
    </span>
    </div>
    <div>
      <div
        *ngIf="!showVideo"
        class="d-flex align-items-center justify-content-center w-100 theme-border border-rds-12 theme-gray-highlight pointer" style="height: 200px; color: red">
        <i
          (click)="showVideo= !showVideo"
          class="fa-youtube fz-38"></i>
      </div>
      <iframe
        *ngIf="showVideo && isWeb"
        class="border-rds-12"
        width="100%" height="300"
        src="https://s3.amazonaws.com/assets.testsigma.com/videos/test-cases/web/create.mp4"
        frameborder="0"
        allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture"
        allowfullscreen></iframe>
      <iframe
        *ngIf="showVideo && isMobileWeb"
        class="border-rds-12"
        width="100%" height="300"
        src="https://s3.amazonaws.com/assets.testsigma.com/videos/test-cases/mobile-web/create.mp4"
        frameborder="0"
        allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture"
        allowfullscreen></iframe>
      <iframe
        *ngIf="showVideo && isAndroidNative"
        class="border-rds-12"
        width="100%" height="300"
        src="https://s3.amazonaws.com/assets.testsigma.com/videos/test-cases/android/create.mp4"
        frameborder="0"
        allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture"
        allowfullscreen></iframe>
      <iframe
        *ngIf="showVideo && isIosNative"
        class="border-rds-12"
        width="100%" height="300"
        src="https://s3.amazonaws.com/assets.testsigma.com/videos/test-cases/ios/create.mp4"
        frameborder="0"
        allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture"
        allowfullscreen></iframe>
      <iframe
        *ngIf="showVideo && isRest"
        class="border-rds-12"
        width="100%" height="300"
        src="https://s3.amazonaws.com/assets.testsigma.com/videos/test-cases/rest-api/create.mp4"
        frameborder="0"
        allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture"
        allowfullscreen></iframe>
    </div>
  `,
  styles: []
})
export class TestCaseCreateHelpComponent implements OnInit {
  @Input('testCase') testCase: TestCase;
  @Input('detailsShowed') detailsShowed: Boolean;
  @Input('workspaceVersion') workspaceVersion: WorkspaceVersion;
  public showVideo: Boolean = false;

  constructor() {
  }

  ngOnInit(): void {
  }

  get isWeb(){
    return this.workspaceVersion.workspace.workspaceType == WorkspaceType.WebApplication;
  }

  get isMobileWeb(){
    return this.workspaceVersion.workspace.workspaceType == WorkspaceType.MobileWeb;
  }

  get isAndroidNative(){
    return this.workspaceVersion.workspace.workspaceType == WorkspaceType.AndroidNative;
  }

  get isIosNative(){
    return this.workspaceVersion.workspace.workspaceType == WorkspaceType.IOSNative;
  }

  get isRest(){
    return this.workspaceVersion.workspace.workspaceType == WorkspaceType.Rest;
  }

}
