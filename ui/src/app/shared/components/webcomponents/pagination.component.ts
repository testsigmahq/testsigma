/*
 *
 *  * *****************************************************************************
 *  *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  *  All rights reserved.
 *  *  ****************************************************************************
 *
 */

import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {Page} from "../../models/page";
import {Pageable} from "../../models/pageable";
import {PageObject} from "../../models/page-object";

@Component({
  selector: 'app-pagination',
  template: `
    <div
      class="pagination"
      id="pagination" *ngIf="paginationData && !paginationData?.empty">
      <button
        type="button"
        class="pagination-btn">
        <i
          class="previous"
          [matTooltip]="'pagination.previous' | translate"
          [class.text-muted]="paginationData.first"
          (click)="previous()">
        </i>

        <span [textContent]="(currentPage.pageNumber * currentPage.pageSize) +1"
              class="start-item"></span>
        <span> - </span>
        <span class="end-item"
              [textContent]="(currentPage.pageSize * (currentPage.pageNumber + 1)) >= paginationData.totalElements ?
          paginationData.totalElements : (currentPage.pageSize * (currentPage.pageNumber + 1))"></span>
        <span
          *ngIf="(currentPage.pageSize * (currentPage.pageNumber + 1)) <= paginationData.totalElements"
          [textContent]="' OF '+ paginationData.totalElements"
          class="total-item"></span>

        <i
          class="next"
          [matTooltip]="'pagination.next' | translate"
          [class.text-muted]="paginationData.last"
          (click)="next()">
        </i>
      </button>
    </div>
  `,
  styles: []
})
export class PaginationComponent implements OnInit {
  @Input() paginationData: Page<PageObject>;
  @Input() currentPage: Pageable;
  @Output() public paginationAction = new EventEmitter<void>();

  constructor() {
  }

  ngOnInit() {
  }

  next() {
    if (this.paginationData.last) {
      return
    }
    this.currentPage.pageNumber += 1;
    this.paginationAction.emit();
  }

  previous() {
    if (this.paginationData.first) {
      return;
    }
    this.currentPage.pageNumber -= 1;
    this.paginationAction.emit();
  }
}
