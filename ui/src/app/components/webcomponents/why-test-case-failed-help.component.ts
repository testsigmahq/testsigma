import {Component, Inject, OnInit, ViewChild} from '@angular/core';
import {Page} from "../../shared/models/page";
import {SuggestionEngine} from "../../models/suggestion-engine.model";
import {CdkVirtualScrollViewport} from '@angular/cdk/scrolling';
import {SuggestionEngineService} from "../../services/suggestion-engine.service";
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material/dialog';
import {TestStepResult} from "../../models/test-step-result.model";

@Component({
  selector: 'app-why-test-case-failed-help',
  template: `
    <div
      class="w-100 h-100 rb-regular">
      <div class="theme-overlay-container">
        <div class="theme-overlay-header theme-border-b" id="overlay-header">
          <div class="theme-overlay-title align-items-center">
            <i class="fa-idea result-status-text-4 pr-10 fz-24"></i>
            <span [translate]="'why_failed.popup.title'"></span>
          </div>
          <button
            class="theme-overlay-close"
            type="button"
            [matTooltip]="'hint.message.common.close' | translate"
            mat-dialog-close>
          </button>
        </div>

        <div class="theme-overlay-content without-footer p-0">
          <div class="d-flex overflow-x-auto">
            <div *ngIf="showSuggestions" class="ts-col-50 py-40 pl-50">
              <span class="fz-16 rb-medium d-block pb-20" [translate]="'why_failed.popup.sub_heading'"></span>
              <cdk-virtual-scroll-viewport
                #suggestionEngineViewPort
                itemSize="59"
                *ngIf="renderingSuggestion?.length"
                class=" virtual-scroll-viewport w-100">
                <ol
                  class="pl-0 pr-30">
                  <li
                    *ngFor='let suggestion of renderingSuggestion'
                    class="d-flex justify-space-between mb-15 suggestion-item pt-10">
                    <div class="icon-container position-relative pr-25">
                      <div class="progress-indicator">
                        <div class="first"></div>
                        <div class="second"></div>
                      </div>
                      <div
                        class="align-items-center bg-white d-flex fz-24 justify-content-around position-absolute rounded-circle suggestion-icon">
                        <i
                          class="{{'line-height-none pt-4 fz-36 fa-'+NG_SUGGESTION_ICONS[suggestion.suggestionId]}}"></i>
                      </div>
                      <div class="d-inline-block position-absolute suggestion-status-icon">
                        <i
                          [class.fa-check-circle-solid]="suggestion.isPassed"
                          [class.text-primary]="suggestion.isPassed"
                          [class.fa-exclamation-circle-solid]="!suggestion.isPassed"
                          [class.result-status-text-1]="!suggestion.isPassed"
                          class="fz-20"></i>
                      </div>
                    </div>
                    <div class="suggestion-msg-container pt-10">
                      <div
                        [class.primary-textstatus-color]="suggestion.isPassed"
                        [class.failed-textstatus-color]="!suggestion.isPassed"
                        [textContent]="'suggestion.'+suggestion.message | translate"
                        class="rb-medium fz-16 ts-col-100"></div>
                      <div
                        class="d-flex fz-14 rest-api-add-icon pt-5 pr-5"
                        [textContent]="'suggestion.desc.'+suggestion.message | translate">
                      </div>

                      <span *ngIf="suggestion?.metaData?.suggestions||suggestion.message=='checkbox_list'"
                            class="d-flex fz-14 rest-api-add-icon pt-5 rb-semi-bold"
                            [textContent]="metaDataValue(suggestion?.metaData?.suggestions, suggestion)||('message.common.none'|translate)">
                      </span>
                    </div>
                  </li>

                </ol>
              </cdk-virtual-scroll-viewport>
            </div>
            <div [class.ts-col-100]="!showSuggestions"
                 [class.failure-modal]="showSuggestions"
                 class="ts-col-50 pb-40 pt-10 px-35">

              <div class="d-flex mt-30">
                <i class="fa fa-check-error desaturated-blue fz-22"></i>
                <div class="pl-15">
                  <span class="rb-medium fz-16 d-block pb-5" [translate]="'why_failed.popup.check_error'"></span>
                  <span class="text-secondary fz-14" [translate]="'why_failed.popup.check_error.description'"></span>
                </div>
              </div>
              <div class="d-flex mt-30">
                <i class="fa fa-check-screenshot desaturated-blue fz-22"></i>
                <div class="pl-15">
                  <span class="rb-medium fz-16 d-block pb-5" [translate]="'why_failed.popup.check_screenshot'"></span>
                  <span class="text-secondary fz-14"
                        [translate]="'why_failed.popup.check_screenshot.description'"></span>
                </div>
              </div>
              <div class="d-flex mt-30">
                <i class="fa fa-watch-video desaturated-blue fz-22"></i>
                <div class="pl-15">
                  <span class="rb-medium fz-16 d-block pb-5" [translate]="'why_failed.popup.watch_video'"></span>
                  <span class="text-secondary fz-14" [translate]="'why_failed.popup.watch_video.description'"></span>
                </div>
              </div>
              <div class="d-flex mt-30">
                <i class="fa fa-perform-manually desaturated-blue fz-22"></i>
                <div class="pl-15">
                  <span class="rb-medium fz-16 d-block pb-5" [translate]="'why_failed.popup.perform_manually'"></span>
                  <span class="text-secondary fz-14"
                        [translate]="'why_failed.popup.perform_manually.description'"></span>
                </div>
              </div>

            </div>
          </div>
          <div class="d-flex failed-popup-bg align-items-center justify-content-center py-25 px-50 flex-wrap"
               [class.pl-75]="!showSuggestions">
            <div
              [class.ts-col-50]="showSuggestions"
              [class.ts-col-100]="!showSuggestions"
              [translate]="'why_failed.popup.help.content'"
              class="fz-15 text-secondary ts-col-50 pr-20"></div>
            <div [class.mt-10]="!showSuggestions" [class.ts-col-100]="!showSuggestions">
              <a
                onclick="javascript:window.parent.fcWidget.open()"
                herf='javascript:void(0)'
                class="btn btn-purple-blue large py-10 border-rds-2 px-20 mr-20">
                <i class="fa-messager pr-7"></i>
                <span [translate]="'suggestion.engine.btn.talk_to_expert'"></span>
              </a>
              <a
                href="https://support.testsigma.com/a/tickets/new"
                target="_blank"
                class="btn btn-purple-blue large py-10 border-rds-2 ml-0 px-20 d-inline-flex align-items-center">
                <i class="fa fa-email-box pr-7 line-height-none mb-n4"></i>
                <span
                  [translate]="'suggestion.engine.btn.email_with_us'"></span>
              </a>
            </div>

          </div>
        </div>
      </div>
    </div>
  `,
  styles: []
})
export class WhyTestCaseFailedHelpComponent implements OnInit {

  public suggestions: Page<SuggestionEngine>;
  public commonSuggestion: any;
  public renderingSuggestion: SuggestionEngine[] = [];
  @ViewChild('suggestionEngineViewPort') public suggestionEngineViewPort: CdkVirtualScrollViewport;
  NG_SUGGESTION_ICONS = {
    "1": "element-is-hidden",
    "2": "investigation",
    "3": "iframe",
    "4": "iframe",
    "5": "invalid-selector-selection",
    //"6": "alert",
    "6": "scroll",
    "7": "page-loading",
    "8": "alert-exists",
    "9": "alert-exists",
    "10": "checkbox_list",
    "11": "investigation",
    "12": "alert-exists",
    "13": "alert-exists",
    "14": "checkbox_list",
    "15": "investigation",
    "16": "radio-with-lable",
    "17": "page-element",
    "18": "frame-list",
    "19": "class",
    "20": "css",
    "21": "investigation",
    "22": "page-element",
    "23": "investigation",
    "24": "current-page-url",
    "29": "investigation",
    "30": "elements",
    "31": "elements",
    "32": "investigation",
    "33": "investigation",
    "34": "investigation",
    "35": "textbox-placeholder",
    "36": "investigation",
    "37": "page-element",
    "38": "elements",
    "39": "page-element",
    "40": "elements",
    "41": "elements",
    "42": "page-element",
    "43": "checkbox_list",
    "44": "checkbox_list",
    "45": "investigation",
    "46": "investigation",
    "47": "textbox-placeholder",
    "48": "investigation",
    "49": "elements",
    "50": "elements",
    "51": "textbox-placeholder",
    "52": "investigation",
    "53": "investigation",
    "54": "investigation",
    "55": "textbox-placeholder",
    "56": "investigation",
    "57": "checkbox_list",
    "58": "checkbox_list",
    "59": "checkbox_list",
    "60": "radio-with-lable",
    "61": "textbox-placeholder",
    "62": "textbox-placeholder",
    "63": "textbox-placeholder",
    "64": "page-element",
    "65": "investigation",
    "66": "investigation",
    "67": "investigation",
    "68": "elements",
    "69": "radio-with-lable",
    "70": "checkbox_list",
    "71": "checkbox_list",
    "72": "class",
    "73": "page-element",
    "74": "investigation",
    "75": "investigation",
    "76": "textbox-placeholder",
    "77": "list-of-frames",
    "78": "element-is-in-diffrent-frame",
    "79": "element-is-in-diffrent-frame",
    "80": "element-is-in-diffrent-frame",
    "81": "list-of-frames",
    "82": "list-of-frames",
    "83": "frame-list",
    "84": "frame-list",
    "85": "current-page-url",
    "88": "alert-exists",
    "89": "elements",
    "90": "elements"
  }
  constructor(
    @Inject(MAT_DIALOG_DATA) public options: {
      testStepResult: TestStepResult
    },
    private dialogRef: MatDialogRef<WhyTestCaseFailedHelpComponent>,
    private suggestionEngineService: SuggestionEngineService) {
  }

  get showSuggestions(): boolean {
    return this.options.testStepResult && this.renderingSuggestion.length > 0;
  }

  ngOnInit(): void {
    this.fetchStepSuggestion();
  }

  fetchStepSuggestion() {
    this.suggestionEngineService.findAll(this.options.testStepResult.id).subscribe(res => {
      this.suggestions = res;
      if (this.suggestions.content && this.suggestions.content.length) {
        this.fetchSuggestion();
      }
    })
  }
  fetchSuggestion() {
    this.dialogRef.updateSize('72%', 'auto');
    this.suggestionEngineService.findSuggestion().subscribe(res => {
      let suggestionIds = Object.keys(res);
      suggestionIds.filter(item => {
        this.suggestions.content.filter(suggestion => {
          if (suggestion.suggestionId == parseInt(item)) {
            suggestion.title = res[item];
          }
        })
      })
      this.renderingSuggestion.push(this.suggestions.content[0])
      this.delayMe(1, this.renderingSuggestion);
    })
  }


  delayMe(index, tempArr) {
    if (index >= this.suggestions.content.length) {
      return;
    }
    (new Promise(resolve => setTimeout(resolve, 2000))).then(() => {
      tempArr.push(this.suggestions.content[index]);
      this.scrollActiveToView()
      this.delayMe(index + 1, tempArr)
    })

  }

  scrollActiveToView() {
    setTimeout(() => {
      let index = this.renderingSuggestion && this.renderingSuggestion.length;

      if (index > 4) {
        index += 3;
      }

      this.suggestionEngineViewPort && this.suggestionEngineViewPort.scrollToIndex(index, 'smooth');
    }, 100);
  }


  metaDataValue(suggestionMetaData, suggestion:SuggestionEngine) {
    if(suggestion.message == 'list_of_frames')
      return suggestionMetaData?.list?.map( suggestion => "//iframe["+suggestion["Frame Index"]+"]")?.join(" , ")||"-";
    else if(suggestion.message == 'iframe')
      return suggestionMetaData?.list? ("( //iframe["+suggestionMetaData?.list["Frame Index"]+"] )"):"-";
    else if(suggestion.message == 'select_option_values')
      return suggestionMetaData?.list?.map( suggestion => suggestion["Option Text"])?.join(" , ")||"-";
    else if(suggestion.message == 'checkbox_list')
      return suggestionMetaData?.list?.length||'';
    else if(suggestion.message == 'input_boxes')
      return suggestionMetaData?.list?.length||'';
    else if(suggestion.message == 'current_page_url' || suggestion.message == 'element_type'
            ||  suggestion.message =="select_option_count" || suggestion.message == "all_cookies") {
      let key = Object.keys(suggestionMetaData?.list[0])[0];
      return suggestionMetaData?.list && suggestionMetaData?.list[0][key]? "(  " + suggestionMetaData?.list[0][key] + " )": null;
    }
    // TODO , Shud be Added For Some more Templates
  }
}
