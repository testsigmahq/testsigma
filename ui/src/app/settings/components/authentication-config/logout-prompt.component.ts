import {Component, Inject, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA} from "@angular/material/dialog";

@Component({
  selector: 'confirm-restart-server-dialog',
  styles:[],
  template: `
    <div class="w-100 h-100">
      <div class="theme-overlay-container">
        <div class="theme-overlay-header">
          <span class="theme-overlay-title" [translate]="'settings.auth.alert.logout'"></span>
        </div>

        <div class="theme-overlay-content">
          <div  class="form-group ts-form">
            <div class="text-truncate rb-medium mt-8">
              <p [translate]="'message.common.prompt.logout'"></p>
            </div>
          </div>

        </div>

        <div class="theme-overlay-footer">
          <div class="ml-10 d-inline">
            <button type="submit"
                    [mat-dialog-close]="true"
                    class="btn btn-primary">
              <span [translate]="'btn.common.ok'"></span>
            </button>
          </div>
        </div>
      </div>
    </div>`,
})
export class LogoutPromptComponent {
  constructor(
    @Inject(MAT_DIALOG_DATA) public modalData: any) {
  }

}
