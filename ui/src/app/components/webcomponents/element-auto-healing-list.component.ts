/*
 *
 *  * *****************************************************************************
 *  *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  *  All rights reserved.
 *  *  ****************************************************************************
 *
 */

import {Component, Inject, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA} from "@angular/material/dialog";
import {TestStepResultMetaSelfHealing} from "../../models/test-step-result-meta-self-healing.model";

@Component({
  selector: 'app-auto-healing',
  templateUrl: './element-auto-healing-list.component.html',
  styles: []
})
export class ElementAutoHealingListComponent implements OnInit {
  public selfHealMetadata: TestStepResultMetaSelfHealing;
  public isAutoHeal: string;

  constructor(@Inject(MAT_DIALOG_DATA) public options: { selfHealMetadata: TestStepResultMetaSelfHealing }) {
    this.selfHealMetadata = this.options.selfHealMetadata;
  }

  ngOnInit() {
    this.isAutoHeal = this.selfHealMetadata && this.selfHealMetadata.priorityXpaths &&
    this.selfHealMetadata.priorityXpaths.length ? 'passedXpath' : 'failedXpath'
  }

  toggleAutoHeal(toggleValue) {
    this.isAutoHeal = toggleValue;
  }
}
