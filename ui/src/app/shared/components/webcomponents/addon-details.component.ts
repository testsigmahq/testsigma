import {Component, Inject, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material/dialog';
import {DomSanitizer} from '@angular/platform-browser';
import {AddonTestDataFunctionService,} from "../../../services/addon-default-data-generator.service";

@Component({
  selector: 'app-addon-details',
  template: `
    <div class="theme-overlay-container">
      <div class="theme-overlay-header with-br-bottom">
        <div
          class="theme-overlay-title"
          [translate]="'addon.details.title'">
        </div>
        <button
          class="theme-overlay-close"
          type="button"
          [matTooltip]="'hint.message.common.close' | translate"
          mat-dialog-close>
        </button>
      </div>
      <iframe [src]="url" class="h-100 w-100 border-0" *ngIf="url">
      </iframe>
    </div>
  `,
  styles: []
})
export class AddonDetailsComponent implements OnInit {
  public addonId;
  public url;

  constructor(
    private dialogRef: MatDialogRef<AddonDetailsComponent, any>,
    @Inject(MAT_DIALOG_DATA) public option: { functionId: number },
    private sanitizer: DomSanitizer,
    private addonTestDataFunctionService: AddonTestDataFunctionService,
  ) {
  }

  ngOnInit(): void {
    this.fetchTestDataFunction(this.option.functionId);
  }

  fetchTestDataFunction(id) {
    this.addonTestDataFunctionService.show(id).subscribe(res => {
      this.addonId = res.externalUniqueId;
      this.url = this.sanitizer.bypassSecurityTrustResourceUrl('/kibbutz/login?redirect_uri=/ui/details/' + this.addonId);
    })
  }
}
