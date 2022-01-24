/*
 * *****************************************************************************
 *  Copyright (C) 2020 Testsigma Technologies Inc.
 *  All rights reserved.
 *  ****************************************************************************
 */

import {Base} from "../shared/models/base.model";
import {PageObject} from "../shared/models/page-object";
import {deserialize, serializable} from 'serializr';

export class ElementScreenName extends Base implements PageObject {
  @serializable
  public name: String;
  @serializable
  public workspaceVersionId: number;
  private  createdDate : Date;
  private  updatedDate : Date;

  deserialize(input: any): this {
    return Object.assign(this, deserialize(ElementScreenName, input));
  }

  public  equals( anotherObject:ElementScreenName){
    if(this.name==anotherObject.name && this.workspaceVersionId==anotherObject.workspaceVersionId){
      return true;
    }else{
      return false;
    }
  }
}

