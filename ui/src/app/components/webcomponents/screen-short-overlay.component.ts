import {Component, Inject, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA} from "@angular/material/dialog";

@Component({
  selector: 'app-srceen-short-overlay',
  template: `
    <div class="theme-overlay-container">
      <div class="theme-overlay-header">
        <span class="theme-overlay-title" [translate]="'step_result.screen_short'"></span>
        <button class="theme-overlay-close"
                type="button"
                [matTooltip]="'hint.message.common.close' | translate"
                mat-dialog-close>
        </button>
      </div>
      <div class="theme-overlay-content">
        <img
          width="100%"
          [src]="screenShortUrl">
      </div>
    </div>
  `,
  styles: []
})
export class ScreenShortOverlayComponent implements OnInit {
  public screenShortUrl: string;

  constructor(@Inject(MAT_DIALOG_DATA) public option: { screenShortUrl: string }) {
    this.screenShortUrl = this.option.screenShortUrl;
  }

  ngOnInit() {
  }

}
