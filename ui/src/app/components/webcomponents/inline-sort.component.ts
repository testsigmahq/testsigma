import {Component, Input, ViewChild} from '@angular/core';
import {MatTooltip} from "@angular/material/tooltip";

@Component({
  selector: 'app-inline-sort',
  template: `
    <div #tooltipDiv="matTooltip"
         class="w-fit-content sort-header"
         [matTooltip]="getToolTip|translate"
         [matTooltipPosition]="'after'">
      <span [translate]="heading"></span>
      <span *ngIf="ascending" class='fa-down-sort'></span>
      <span *ngIf="ascending==false" class='fa-up-sort'></span>
      <span *ngIf="ascending==undefined" class='fa-down-sort opaque-50'></span>
    </div>
  `,
  styles: [
  ]
})
export class InlineSortComponent{
  @Input() ascending: Boolean;
  @Input() heading: String;
  @ViewChild('tooltipDiv')tooltipDiv: MatTooltip;
  toolTip;

  get getToolTip(){
    if(this.ascending==true)
      return this.heading?.includes('created_at')?'message.common.sort_by.old':'message.common.sort_by.ascending';
    else if(this.ascending==false)
      return this.heading?.includes('created_at')?'message.common.sort_by.new':'message.common.sort_by.descending';
    else
      return 'message.common.sort';
  }

  ngOnChanges(): void {
    if(this.ascending!=undefined) {
      this.tooltipDiv?.hide()
      this.tooltipDiv?.show(200);
    }
  }

}
