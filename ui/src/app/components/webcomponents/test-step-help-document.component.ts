import {Component, Input, OnInit} from '@angular/core';
import {TestCase} from "../../models/test-case.model";
import {WorkspaceVersion} from "../../models/workspace-version.model";
import {TestStepType} from "../../enums/test-step-type.enum";
import {DomSanitizer, SafeUrl} from "@angular/platform-browser";

@Component({
  selector: 'app-test-step-help-document',
  templateUrl: './test-step-help-document.component.html',
  styles: []
})
export class TestStepHelpDocumentComponent implements OnInit {
  @Input('testcase') testcase: TestCase;
  @Input('version') version: WorkspaceVersion;
  @Input('stepType') stepType: string;
  public videoUrlString: SafeUrl;
  public showVideo: Boolean = false;

  constructor(private sanitizer: DomSanitizer) {
  }

  ngOnInit(): void {
  }
  ngOnChanges(): void {
    this.videoUrl();
  }
  get isActionText() {
    return this.stepType == TestStepType.ACTION_TEXT;
  }

  get isRest() {
    return this.stepType == TestStepType.REST_STEP;
  }

  get isStepGroup() {
    return this.stepType == TestStepType.STEP_GROUP;
  }

  get isRestApp() {
    return this.version?.workspace?.isRest;
  }

  videoUrl(): SafeUrl {
    let url = "";
    if (this.testcase) {
      url = this.version?.workspace?.isWeb && !this.isRest ? 'https://s3.amazonaws.com/assets.testsigma.com/videos/test-cases/web/create-steps.mp4' :
        !this.version?.workspace?.isMobileNative ? 'https://s3.amazonaws.com/assets.testsigma.com/videos/test-cases/mobile-web/create-steps.mp4' :
          this.version?.workspace?.isAndroidNative ? 'https://s3.amazonaws.com/assets.testsigma.com/videos/test-cases/android/create-steps.mp4' :
            this.version?.workspace?.isIosNative ? 'https://s3.amazonaws.com/assets.testsigma.com/videos/test-cases/ios/create-steps.mp4' :
              this.isRest ? 'https://s3.amazonaws.com/assets.testsigma.com/videos/test-cases/rest-api/create-steps.mp4' :
                'https://s3.amazonaws.com/assets.testsigma.com/videos/test-cases/rest-api/create-steps.mp4';
    }
    this.videoUrlString = this.sanitizer?.bypassSecurityTrustResourceUrl(url);
    return this.videoUrlString;
  }

}
