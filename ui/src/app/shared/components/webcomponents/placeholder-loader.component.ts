import {Component, Input, OnInit} from '@angular/core';

@Component({
  selector: 'app-placeholder-loader',
  template: `
    <div
      *ngIf="!isLogoLoader && !isDetails && !isChartWith"
      style="padding: 15px 24px 15px 20px">
      <div
        *ngFor="let three of [1, 2, 3]"
        class="loader-section">
        <div class="chart-loader md"></div>
        <div class="d-flex flex-wrap text-loader ts-col-85">
          <div class="text-line-loader thin-lg ts-col-80"></div>
          <div class="text-line-loader thin-sm w-50"></div>
        </div>
      </div>
    </div>
    <div
      *ngIf="isLogoLoader && !isDetails && !isChartWith">
      <div class="loading-logo"></div>
      <div class="justify-content-center d-flex">
        <span
          class="d-flex fz-20 justify-content-center text-t-highlight"
          [innerHTML]="displayText ? displayText : ('step_result.executing.loading.msg' | translate)"></span>
        <span *ngIf="showLoadingDots" class="loading-dots" ></span>
      </div>
    </div>
    <div *ngIf="isDetails" class="loader-section ts-col-100 flex-wrap">
      <div class="ts-col-60 mb-30 ml-30">
        <div class="text-line-loader"></div>
        <div class="d-flex">
          <div
            class="mw-15"
            [class.ml-30]="trackIndex != 0"
            style="flex: auto"
            *ngFor="let two of [1,2], let trackIndex = index">
            <div class="text-line-loader"></div>
          </div>
        </div>
      </div>
      <div class="details-container ts-col-100">
        <div
          class="ml-30 mw-20"
          style="flex: auto"
          *ngFor="let two of [1,2]">
          <div class="text-line-loader"></div>
          <div class="text-line-loader mw-75"></div>
        </div>
      </div>
    </div>
    <div
      *ngIf="isChartWith"
      class="py-15" style="padding-left: 20px;padding-right: 22px">
      <div
        *ngFor="let three of [1,2,3]"
        class="loader-section">
        <div class="chart-loader md"></div>
        <div class="d-flex flex-column text-loader ts-col-35">
          <div class="text-line-loader"></div>
          <div class="text-line-loader w-50"></div>
        </div>
        <div class="ml-20 text-line-loader ts-col-20">
        </div>
      </div>
    </div>
  `,
  styles: []
})
export class PlaceholderLoaderComponent implements OnInit {
  @Input('isLogoLoader') isLogoLoader: Boolean = false;
  @Input('displayText') displayText?: string;
  @Input('isDetails') isDetails: boolean = false;
  @Input('isChartWith') isChartWith: boolean = false;
  @Input('showLoadingDots') showLoadingDots: boolean = true

  constructor() {
  }

  ngOnInit(): void {
  }

}
