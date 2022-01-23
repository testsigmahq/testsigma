import { Component, OnInit, Input } from '@angular/core';

@Component({
  selector: 'app-vi-icon',
  template: `
    <div
      *ngIf="isVisuallyPassed != null"
      class="pl-10">
      <i
        [matTooltip]="(!isVisuallyPassed ? 'visual_test.hint.differences' : 'visual_test.hint.no_differences') | translate"
        class="fa-camera"
        [class.result-status-text-1]="!isVisuallyPassed"
        [class.result-status-text-0]="isVisuallyPassed">
      </i>
    </div>
  `,
  styles: [
  ]
})
export class ViIconComponent implements OnInit {
  @Input('isVisuallyPassed') isVisuallyPassed: boolean;
  constructor() { }

  ngOnInit(): void {
  }

}
