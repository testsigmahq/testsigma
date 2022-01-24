/*
 *
 * ****************************************************************************
 *  * Copyright (C) 2019 Testsigma Technologies Inc.
 *  * All rights reserved.
 *  ****************************************************************************
 *
 */
import {fabric} from 'fabric';
import {MobileElement} from "./mobile-element.model";

export class MobileElementRect extends fabric.Rect {
  public mobileElement: MobileElement;
  public _originalElement: MobileElement;
  public elementSelected: Boolean;
}
