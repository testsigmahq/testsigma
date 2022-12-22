import {Component, OnInit} from "@angular/core";
import {ChromeRecorderService} from "../../services/chrome-recoder.service";
import {MatDialog} from "@angular/material/dialog";
import {FirstChromeInstallComponent} from "./first-chrome-install.component";

@Component({
  selector: 'app-chrome-record-button',
  template: `
    <button
      *ngIf="chromeRecorderService?.isChrome"
      (click)="chromeRecorderService?.isStepRecorder? stopRecoding() : openRecorderPopup()"
      class="theme-btn-clear-default nlp-record-default-button ml-14 position-relative py-6 text-nowrap">
      <span class="recorder-beta-btn" [translate]="'message.common.beta_tag'"></span>
      <div
        class="rounded-circle btn mr-5 p-4 my-2"
        [class.result-status-1]="chromeRecorderService?.isStepRecorder"
        [class.result-status-0]="!chromeRecorderService?.isStepRecorder"></div>
      <span
        [translate]="(!chromeRecorderService?.isStepRecorder)?'btn.common.record':'message.common.stop'"></span>
    </button>
    <div class="d-none nlp-record-button">
      <button
        *ngIf="chromeRecorderService?.isChrome"
        (click)="chromeRecorderService?.isStepRecorder? stopRecoding() : openRecorderPopup()"
        class="theme-btn-primary ml-14 position-relative py-8 text-nowrap border-rds-26 pl-15">
        <span
          [translate]="(!chromeRecorderService?.isStepRecorder)?'btn.common.start_record':'message.common.stop'"></span>
        <i class="fa-arrow-right text-white pl-15"></i>
      </button>
    </div>
  `,
  styles: [
  ]
})
export class ChromeRecordButtonComponent implements OnInit {

  constructor(
    public chromeRecorderService: ChromeRecorderService,
    private matModal: MatDialog) { }

  ngOnInit(): void {
  }

  stopRecoding() {
    this.chromeRecorderService.stopSpying();
    this.chromeRecorderService.isStepRecorder = false;
  }

  openRecorderPopup() {
    if(this.chromeRecorderService.isInstalled) {
      this.chromeRecorderService.getStepList();
    } else
      this.openFirstInstallChrome()
  }

  openFirstInstallChrome() {
    this.matModal.open(FirstChromeInstallComponent, {
      width: '512px',
      position: {left: '80px', bottom:'60px'},
      panelClass: ['mat-overlay', 'onboarding-help-container'],
      disableClose: true
    });
  }

  private startRecording() {
    this.chromeRecorderService.isStepRecorder = true;
    this.chromeRecorderService.startRecording()
  }

}
