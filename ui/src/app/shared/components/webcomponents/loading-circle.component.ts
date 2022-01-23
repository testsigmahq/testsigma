/*
 * *****************************************************************************
 *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  All rights reserved.
 *  ****************************************************************************
 */

import {Component} from '@angular/core';

@Component({
  selector: 'loading-circle',
  template: `
    <div class="text-center" [textContent]="'loading_text' | translate">

    </div>
  `,
  styles: []
})

export class LoadingCircleComponent {

}
